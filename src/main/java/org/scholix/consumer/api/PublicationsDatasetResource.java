package org.scholix.consumer.api;

import javax.ws.rs.core.Response;

import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import static org.elasticsearch.index.query.QueryBuilders.*;
import org.json.JSONArray;
import org.scholix.consumer.api.RestService.InjectedClient;
import java.net.UnknownHostException;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("publications/dataset")
public class PublicationsDatasetResource extends AbstractResource {

	@Inject
	InjectedClient injectedClient;

	@GET
	@Produces("application/json")
	public Response getPublications(@QueryParam("identifier") String identifier) throws UnknownHostException {
		Client client = injectedClient.getClient();

		QueryBuilder builder = boolQuery()
				.should(boolQuery().must(matchQuery("source.identifier.identifier", identifier).operator(Operator.AND))
						.must(termQuery("source.objectType", ObjectType.DATASET))
						.must(termQuery("target.objectType", ObjectType.PUBLICATION)))
				.should(boolQuery().must(matchQuery("target.identifier.identifier", identifier).operator(Operator.AND))
						.must(termQuery("target.objectType", ObjectType.DATASET))
						.must(termQuery("source.objectType", ObjectType.PUBLICATION)));

		JSONArray results = execute(client, builder);

		return Response.ok(results.toString(2)).build();
	}
}
