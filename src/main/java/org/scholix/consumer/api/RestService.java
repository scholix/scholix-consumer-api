package org.scholix.consumer.api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.scholix.consumer.api.RestService.InjectedClient;

public class RestService {

	private static final URI SERVICE_BASE_URI = UriBuilder.fromUri(Config.SERVICE_URI).port(Config.SERVICE_PORT)
			.path(Config.SERVICE_PATH).build();
	private static final Logger log = Logger.getLogger(RestService.class.getName());

	private void run() throws IllegalArgumentException, NullPointerException, IOException {
		final HttpServer server = getServer();

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				log.info("Stopping server..");
				server.shutdownNow();
			}
		}, "shutdownHook"));

		try {
			server.start();
			log.info("Press CTRL^C to exit..");
			Thread.currentThread().join();
		} catch (Exception e) {
			log.severe("There was an error while starting Grizzly HTTP server.");
		}
	}

	private HttpServer getServer() throws IllegalArgumentException, NullPointerException, IOException {
		final ResourceConfig rc = new ResourceConfig().packages("org.scholix.consumer.api");
		rc.register(new ExceptionLogger());
		rc.register(new Binder());

		return GrizzlyHttpServerFactory.createHttpServer(SERVICE_BASE_URI, rc);
	}

	public static void main(String[] args) throws IllegalArgumentException, NullPointerException, IOException {
		RestService app = new RestService();
		app.run();
	}

	private static class Binder extends AbstractBinder {

		@Override
		protected void configure() {
			bindAsContract(InjectedClient.class).in(Singleton.class);
		}
	}

	@Singleton
	public static class InjectedClient {

		private Client client;

		public InjectedClient() throws UnknownHostException {
			client = new PreBuiltTransportClient(Settings.EMPTY).addTransportAddress(
					new InetSocketTransportAddress(InetAddress.getByName(Config.ES_HOST), Config.ES_PORT));
		}

		public Client getClient() throws UnknownHostException {
			return client;
		}

		public void close() {
			client.close();
		}
	}

}
