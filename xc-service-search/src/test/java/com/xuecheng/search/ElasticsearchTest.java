package com.xuecheng.search;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchTest {
    @Autowired
    RestHighLevelClient client;
    @Autowired
    RestClient restClient;
    //映射内容
    private String mapStr = "{\n" +
            "  \"properties\": {\n" +
            "         \"name\": {\n" +
            "            \"type\": \"text\",\n" +
            "            \"analyzer\": \"ik_max_word\",\n" +
            "            \"search_analyzer\": \"ik_smart\"\n" +
            "         },\n" +
            "         \"description\": {\n" +
            "            \"type\": \"text\",\n" +
            "            \"analyzer\": \"ik_max_word\",\n" +
            "            \"search_analyzer\": \"ik_smart\"\n" +
            "         },\n" +
            "         \"studymodel\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "         },\n" +
            "          \"price\": {\n" +
            "              \"type\": \"float\"\n" +
            "         },\n" +
            "         \"timestamp\": {\n" +
            "              \"type\": \"date\",\n" +
            "              \"format\": \"yyyy-MM-dd HH:mm:ss||yyyy-MM-dd\"\n" +
            "         }\n" +
            "      }\n" +
            "}";
    //创建索引库
    @Test
    public void testCreateIndex() throws IOException {
        //创建索引请求对象，并设置索引名称
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("han");
        //设置索引参数
        createIndexRequest.settings(Settings.builder().put("number_of_shards", 1).put("number_of_replicas",0));
        //设置映射
        createIndexRequest.mapping("doc", mapStr, XContentType.JSON);

        //创建索引操作客户端
        IndicesClient indices = client.indices();
        //执行请求并响应
        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);
        //得到响应结果
        boolean acknowledged = createIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }
    //删除索引
    @Test
    public void testDeleteIndex() throws IOException {
        //设置删除请求对象
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("han");
        //执行删除索引方法
        DeleteIndexResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest);
        //响应
        boolean acknowledged = deleteIndexResponse.isAcknowledged();
        System.out.println(acknowledged);
    }
}
