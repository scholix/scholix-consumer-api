package org.scholix.consumer.api;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEvent.Type;
import org.glassfish.jersey.server.monitoring.RequestEventListener;

public class ExceptionLogger implements ApplicationEventListener, RequestEventListener {

	private static final Logger log = Logger.getLogger(ExceptionLogger.class.getName());

	public RequestEventListener onRequest(final RequestEvent requestEvent) {
		return this;
	}

	public void onEvent(RequestEvent paramRequestEvent) {
		if (paramRequestEvent.getType() == Type.ON_EXCEPTION) {
			Throwable throwable = paramRequestEvent.getException();
			log.log(Level.SEVERE, throwable.getMessage(), throwable);
		}
	}

	public void onEvent(ApplicationEvent arg0) {
		
	}
}