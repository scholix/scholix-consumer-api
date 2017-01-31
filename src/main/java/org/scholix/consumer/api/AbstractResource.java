package org.scholix.consumer.api;

import java.util.Iterator;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class AbstractResource {

	protected JSONArray execute(Client client, QueryBuilder builder) {
		SearchResponse response = client.prepareSearch("scholix").setTypes("link").setQuery(builder).execute()
				.actionGet();

		SearchHits hits = response.getHits();

		Iterator<SearchHit> it = hits.iterator();

		JSONArray results = new JSONArray();

		while (it.hasNext()) {
			SearchHit hit = it.next();
			results.put(new JSONObject(hit.getSourceAsString()));
		}

		return results;
	}
}
