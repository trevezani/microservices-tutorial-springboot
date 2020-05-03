package br.com.trevezani.cityinformation.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.stereotype.Service;

@Service
public class RootService {
	@Autowired
	private BuildProperties buildProperties;	

	private Map<String, String> info = null;
	
	public synchronized Map<String, String> properties() {
		if (info == null) {
			info = new HashMap<>();
			
			String artifact = buildProperties.get("artifact");
			
			if (artifact == null) {
				artifact = "";
			}

			info.put("artifact", artifact);
			
			String version = buildProperties.get("version");

			if (version == null) {
				version = "";
			}

			info.put("version", version);
		}
		
		return info;
	}
}
