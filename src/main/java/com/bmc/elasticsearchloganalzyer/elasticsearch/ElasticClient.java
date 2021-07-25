package com.bmc.elasticsearchloganalzyer.elasticsearch;

import com.bmc.elasticsearchloganalzyer.model.LogLine;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkProcessor;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

@Component
public class ElasticClient {


    private RestHighLevelClient client;
    private BulkProcessor bulkProcessor;
    private HashMap mapping = new HashMap();
    private String indiceName;

    private static final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            .setSerializationInclusion(JsonInclude.Include.NON_ABSENT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @PostConstruct
    public void connect() {
        try {
            client = new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost("localhost", 9200, "http")));

            BulkProcessor.Listener listener = new BulkProcessor.Listener() {
                @Override
                public void beforeBulk(long executionId, BulkRequest request) {

                }

                @Override
                public void afterBulk(long executionId, BulkRequest request,
                                      BulkResponse response) {

                }

                @Override
                public void afterBulk(long executionId, BulkRequest request,
                                      Throwable failure) {
                    failure.printStackTrace();
                }
            };

            BiConsumer<BulkRequest, ActionListener<BulkResponse>> bulkConsumer =
                    (request, bulkListener) ->
                            client.bulkAsync(request, RequestOptions.DEFAULT, bulkListener);
            this.bulkProcessor = BulkProcessor.builder(bulkConsumer, listener).build();
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public void createIndices(String hcuName) {
        indiceName = hcuName;
        Map<String, Object> logName = new HashMap<>();
        logName.put("type", "keyword");
        Map<String, Object> messageKeyword = new HashMap<>();
        Map<String, Object> fields = new HashMap<>();
        messageKeyword.put("type", "keyword");
        fields.put("keyword",messageKeyword);
        Map<String, Object> message = new HashMap<>();
        message.put("type", "text");
        message.put("fields",fields);
        Map<String, Object> timestamp = new HashMap<>();
        timestamp.put("type", "date");
        HashMap properties = new HashMap();
        properties.put("logName",logName);
        properties.put("message",message);
        properties.put("timestamp",timestamp);
        mapping.put("properties",properties);
        try {
            this.client.indices().create(new CreateIndexRequest(hcuName + "_hcu_logs_with_time").mapping(mapping),RequestOptions.DEFAULT);
            properties.remove("timestamp");
            this.client.indices().create(new CreateIndexRequest(hcuName + "_hcu_logs").mapping(mapping),RequestOptions.DEFAULT);
        }catch (IOException ex) {
            ex.printStackTrace();
        }

    }

    public void sendLog(LogLine log) {
        bulkProcessor.add(new IndexRequest(indiceName + "_hcu_logs").source(mapper.convertValue((log), Map.class)));
    }
    public void sendLogTime(LogLine log) {
        bulkProcessor.add(new IndexRequest(indiceName + "_hcu_logs_with_time").source(mapper.convertValue((log), Map.class)));
    }

    @PreDestroy
    private void close() throws IOException {
        client.close();
    }

    public RestHighLevelClient getClient() {
        return client;
    }

    public BulkProcessor getBulkProcessor() {
        return bulkProcessor;
    }
}
