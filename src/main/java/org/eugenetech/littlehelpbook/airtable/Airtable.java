package org.eugenetech.littlehelpbook.airtable;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eugenetech.littlehelpbook.data.AirtableRecord;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Vector;

@Slf4j
public class Airtable {
    final static ObjectMapper mapper = new ObjectMapper();

    private String key;
    private String base;

    public Airtable(final String base, final String key) {
        this.base = base;
        this.key = key;
    }

    public List<AirtableRecord> getAll(final String table) throws Exception {
        final String url = this.base + "/" + table;
        String offset = null;
        @Cleanup final CloseableHttpClient httpClient = HttpClientBuilder.create().disableCookieManagement().disableAutomaticRetries().build();

        log.info("Downloading " + url);

        final List<AirtableRecord> records = new Vector<>();
        do {
            final URIBuilder builder = new URIBuilder(url);
            if (offset != null) builder.addParameter("offset", offset);

            final URI uri = builder.build();

            final HttpGet httpGet = new HttpGet(uri);
            httpGet.addHeader("Accept", "application/json");
            httpGet.addHeader("Authorization", "Bearer " + this.key);

            // Make the HTTP request
            @Cleanup final CloseableHttpResponse response = httpClient.execute(httpGet);

            // Check for success
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new Exception(response.getStatusLine().getReasonPhrase());
            }

            final InputStream content = response.getEntity().getContent();

           // log.info(response.getEntity().toString());

            // Convert the JSON results to a JsonNode that we can traverse
            final JsonNode jsonNode = mapper.readTree(content);

            //  log.debug(jsonNode.toPrettyString());

            if (jsonNode.has("offset"))
                offset = jsonNode.get("offset").asText();
            else
                offset = null;

            final ArrayNode results = (ArrayNode)jsonNode.get("records");

            for (final JsonNode result : results) {
                final AirtableRecord record = new AirtableRecord();

                record.setId(result.get("id").asText());
                record.setJsonNode(result.get("fields"));

                records.add(record);
            }

            log.info(records.size() + " records for " + table);

            Thread.sleep(250);
        } while (offset != null);

        log.info(records.size() + " total records for " + table);

        return records;
    }
}
