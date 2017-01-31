package org.scholix.consumer.api;

import javax.ws.rs.core.Response;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import static org.elasticsearch.index.query.QueryBuilders.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.json.JSONArray;
import org.json.JSONObject;
import org.scholix.consumer.api.RestService.InjectedClient;

import java.net.UnknownHostException;
import java.util.Iterator;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("datasets/dataset")
public class DatasetsDatasetResource {

	@Inject
	InjectedClient injectedClient;

	@GET
	@Produces("application/json")
	public Response getPublications(@QueryParam("identifier") String identifier) throws UnknownHostException {
		Client client = injectedClient.getClient();

		QueryBuilder builder = boolQuery()
				.should(boolQuery().must(matchQuery("source.identifier.identifier", identifier).operator(Operator.AND))
						.must(termQuery("source.objectType", ObjectType.DATASET))
						.must(termQuery("target.objectType", ObjectType.DATASET)))
				.should(boolQuery().must(matchQuery("target.identifier.identifier", identifier).operator(Operator.AND))
						.must(termQuery("target.objectType", ObjectType.DATASET))
						.must(termQuery("source.objectType", ObjectType.DATASET)));

		SearchResponse response = client.prepareSearch("scholix").setTypes("link").setQuery(builder).execute()
				.actionGet();

		SearchHits hits = response.getHits();

		Iterator<SearchHit> it = hits.iterator();

		JSONArray results = new JSONArray();

		while (it.hasNext()) {
			SearchHit hit = it.next();
			results.put(new JSONObject(hit.getSourceAsString()));
		}

		return Response.ok(results.toString(2)).build();
	}
}
