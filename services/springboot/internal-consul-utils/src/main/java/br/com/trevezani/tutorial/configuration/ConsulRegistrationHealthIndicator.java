package br.com.trevezani.tutorial.configuration;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.serviceregistry.AbstractAutoServiceRegistration;
import org.springframework.cloud.consul.serviceregistry.ConsulRegistration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.SmartLifecycle;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.QueryParams;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.catalog.CatalogServiceRequest;
import com.ecwid.consul.v1.catalog.model.CatalogService;

@Configuration
public class ConsulRegistrationHealthIndicator implements SmartLifecycle {
	Logger log = LoggerFactory.getLogger(this.getClass());

	public static final String APPLICATION_NAME_KEY = "spring.application.name";
	public static final String APPLICATION_PORT_KEY = "server.port";
	public static final String ADDRESS_KEY = "spring.cloud.client.ip-address";

	@Value("${checkconsul.enable:true}")
	private String checkConsulEnable;

	@Autowired
	private AbstractAutoServiceRegistration<ConsulRegistration> serviceRegistration;

	@Autowired
	private ApplicationContext context;	
	
	private final Environment environment;
	private final ConsulClient client;

	private final TaskScheduler taskScheduler = getTaskScheduler();

	private final AtomicBoolean running = new AtomicBoolean(false);
	private ScheduledFuture<?> watchFuture = null;

	private final AtomicLong timeBeganError = new AtomicLong(0);
	private final AtomicLong timeoutCriticalCheck = new AtomicLong(0);
	
	public ConsulRegistrationHealthIndicator(Environment environment, ConsulClient client) {
		super();
		
		String healthCheckCriticalTimeout = environment.getRequiredProperty("spring.cloud.consul.discovery.health-check-critical-timeout");

		Optional<Long> timeout = Optional.ofNullable(healthCheckCriticalTimeout)
										 .flatMap(v -> Optional.ofNullable(v).map(x -> x.replace("m", "")))
										 .map(Long::parseLong);
		
		timeout.ifPresent(t -> {
			timeoutCriticalCheck.set(t * 60000);

			log.info("[Consul] Defined time to close app if consul down for a while -> {} minutes / {} milliseconds", t, timeoutCriticalCheck.get());
		});
		
		this.environment = environment;
		this.client = client;
	}

	private static ThreadPoolTaskScheduler getTaskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.initialize();

		return taskScheduler;
	}

	@Bean
	@ConditionalOnMissingBean
	public ConsulRegistrationHealthIndicator consulRegistrationHealthIndicator(Environment discoveryProperties, ConsulClient consulClient) {
		return new ConsulRegistrationHealthIndicator(discoveryProperties, consulClient);
	}

	@Override
	public void start() {
		if (this.running.compareAndSet(false, true)) {
			if (checkConsulEnable.equals("true")) {
				this.watchFuture = this.taskScheduler.scheduleWithFixedDelay(this::doCheck, 13000);
			} else {
				log.info("Check Consul disabled");
			}
		}
	}

	@Override
	public void stop(Runnable callback) {
		this.stop();
		callback.run();
	}

	@Override
	public void stop() {
		if (this.running.compareAndSet(true, false) && this.watchFuture != null) {
			this.watchFuture.cancel(true);
		}
	}

	@Override
	public boolean isRunning() {
		return false;
	}
	
	@Override
	public boolean isAutoStartup() {
		return true;
	}
	
	@Override
	public int getPhase() {
		return 0;
	}
	
	public void doCheck() {
		String applicationName = environment.getRequiredProperty(APPLICATION_NAME_KEY);
		String serviceAddress = environment.getRequiredProperty(ADDRESS_KEY);
		Integer servicePort = environment.getRequiredProperty(APPLICATION_PORT_KEY, Integer.class);

		log.debug("[Consul] Check '{}' :: IP [{}] PORT [{}]", applicationName, serviceAddress, servicePort);

		if (!serviceRegistration.isRunning()) {
			log.warn("[Consul] '{}' not running :: IP [{}] PORT [{}]", applicationName, serviceAddress, servicePort);
			return;
		}

		try {
			final CatalogServiceRequest catalogServiceRequest = CatalogServiceRequest.newBuilder().setQueryParams(QueryParams.DEFAULT).build();

			Response<List<CatalogService>> services = this.client.getCatalogService(applicationName, catalogServiceRequest);

			if (log.isDebugEnabled()) {
				services.getValue().stream().forEach(service -> {
					log.info("[Consul] Registered service address [{}] port [{}]", service.getServiceAddress(), service.getServicePort());
				});
			}

			Optional<CatalogService> catalogService = services.getValue().stream()
					.filter(service -> service.getServiceAddress().equals(serviceAddress) && service.getServicePort().equals(servicePort))
					.findAny();

			if (!catalogService.isPresent()) {
				log.warn("[Consul] '{}' not in the registry, do reregister", applicationName);

				serviceRegistration.stop();
				serviceRegistration.start();
			}
			
			timeBeganError.set(0l);
			
		} catch (Exception e) {
			if (timeoutCriticalCheck.get() > 0) {
				if (timeBeganError.get() == 0) {
					timeBeganError.set(System.currentTimeMillis());
				} else {
					if ((System.currentTimeMillis() - timeBeganError.get()) >= timeoutCriticalCheck.get()) {
						int exitCode = SpringApplication.exit(context, (ExitCodeGenerator) () -> 0);
						
					    System.exit(exitCode);
					}
				}
			}
			
			log.error("[Consul] '{}' -> couldn't query catalog service", applicationName, e);
		}
	}
}
