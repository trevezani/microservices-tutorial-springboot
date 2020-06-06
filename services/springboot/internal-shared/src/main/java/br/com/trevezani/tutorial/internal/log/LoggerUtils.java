package br.com.trevezani.tutorial.internal.log;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtils {
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	final static String packageAccept = "br.com.trevezani";
	
	public static String format(String message, Exception exception) {
		StringBuilder info = new StringBuilder();
		info.append(message);
		
		if (exception != null) {
			List<String> stack = new ArrayList<>();
			
		    for (StackTraceElement ste : exception.getStackTrace()) {
		    	if (!Objects.isNull(ste) && ste.getClassName().startsWith(packageAccept)) {
		    		stack.add(String.format("%s.%s (%d)", ste.getClassName(), ste.getMethodName(), ste.getLineNumber()));
		    	}
		    }
		    
		    info.append(" ").append(stack.toString());
		}

		return info.toString();		
	}
}
