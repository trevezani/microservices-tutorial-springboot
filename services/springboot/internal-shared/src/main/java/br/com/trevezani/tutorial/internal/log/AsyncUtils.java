package br.com.trevezani.tutorial.internal.log;

import java.util.Map;
import java.util.function.Supplier;

import org.slf4j.MDC;

public class AsyncUtils {

	public static Runnable withMdc(Runnable runnable) {
		Map<String, String> mdc = MDC.getCopyOfContextMap();
		
		return () -> {
			MDC.setContextMap(mdc);
			runnable.run();
		};
	}

	public static <U> Supplier<U> withMdc(Supplier<U> supplier) {
		Map<String, String> mdc = MDC.getCopyOfContextMap();
		
		return (Supplier<U>) () -> {
			MDC.setContextMap(mdc);
			return supplier.get();
		};
	}

}
