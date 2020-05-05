package hdd.es;

import com.alibaba.fastjson.JSONObject;
import hdd.es.entity.Blog;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

public class ElasticSearchTest {

    private TransportClient client;

    @Before
    public void before() throws UnknownHostException {
        //创建Settings对象
        Settings settings = Settings.builder().put("cluster.name","elasticsearch").build();
        //创建TransportClient对象(当集群中有多个节点时多次调用addTransportAddress设置)
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName("127.0.0.1"),9300));
    }

    @Test
    public void creatIndex(){
        IndicesAdminClient indices = client.admin().indices();
        if(indices.prepareExists("blog1").get().isExists())//判断索引库是否存在
            return;
        indices.prepareCreate("blog1").get();
        client.close();
    }

    @Test
    public void setMappings() throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                    .startObject("properties")
                        .startObject("blog_id")
                            .field("type", "text")
                            .field("store", true)
                        .endObject()
                        .startObject("title")
                            .field("type", "text")
                            .field("store", true)
                            .field("analyzer", "ik_smart")
                        .endObject()
                        .startObject("content")
                            .field("type", "text")
                            .field("store", true)
                            .field("analyzer", "ik_smart")
                        .endObject()
                    .endObject()
                .endObject();
        client.admin().indices()
                .preparePutMapping("blog1")//设置要做映射的索引库
                .setType("_doc")//设置文档类型，ES7.x版本一个索引库只支持一个文档类型
                .setSource(builder) //mapping信息，也可直接接收json字符串
                .get();//执行操作
        client.close();
    }

    @Test
    public void addDocument(){
        //创建一个实体对象
        Blog blog = new Blog();
        blog.setBlog_id("11");
        blog.setTitle("这是一篇博客的标题");
        blog.setContent("这是一篇博客修改后的内容");
        client.prepareIndex("blog1","_doc","11")
                .setSource(blog.toString(), XContentType.JSON)
                .get();
        client.close();
    }

    @Test
    public void queryDocumentById(){
        //创建查询对象
        QueryBuilder query = QueryBuilders.idsQuery().addIds("11","12");
        //执行查询
        search(query);
    }

    /**
     * term方式查询不分词（分词后的文档必须完全包含要搜索的关键词）
     */
    @Test
    public void queryDocumentByTerm(){
        //创建查询对象
        QueryBuilder query = QueryBuilders.termQuery("title","博客");
        //执行查询
        search(query);
    }

    @Test
    public void queryDocumentByQueryString(){
        //创建查询对象
        //QueryBuilder query = QueryBuilders.queryStringQuery("一篇的博客").defaultField("title");
        QueryBuilder query = QueryBuilders.queryStringQuery("一篇的博客");
        //执行查询
        searchHighLight(query);
    }

    private void search(QueryBuilder queryBuilder){
        SearchResponse searchResponse = client.prepareSearch("blog1")//指定索引库
                .setTypes("_doc")//指定类型
                .setQuery(queryBuilder)
                //设置分页信息
                .setFrom(0)
                .setSize(5)
                .get();
        //获取查询结果
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("查询结果总记录数："+searchHits.getTotalHits());
        //查询结果列表
        Iterator<SearchHit> iterator = searchHits.iterator();
        iterator.forEachRemaining( searchHit -> {
            //打印json格式文档对象
            System.out.println(searchHit.getSourceAsString());
            //取文档属性
            System.out.println("----------------文档的属性");
            Map<String,Object> map = searchHit.getSourceAsMap();
            System.out.println(map);
        });
    }

    //搜索结果高亮处理
    private void searchHighLight(QueryBuilder queryBuilder){
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        //设置高亮字段
        highlightBuilder.field("title");
        highlightBuilder.field("content");
        //设置高亮标签
        highlightBuilder.preTags("<em>");
        highlightBuilder.postTags("</em>");
        //执行查询
        SearchResponse searchResponse = client.prepareSearch("blog1")//指定索引库
                .setTypes("_doc")//指定类型
                .setQuery(queryBuilder)
                .highlighter(highlightBuilder)//高亮
                //设置分页信息
                .setFrom(0)
                .setSize(5)
                .get();
        //获取查询结果
        SearchHits searchHits = searchResponse.getHits();
        System.out.println("查询结果总记录数："+searchHits.getTotalHits());
        //查询结果列表
        Iterator<SearchHit> iterator = searchHits.iterator();
        iterator.forEachRemaining( searchHit -> {
            //打印json格式文档对象
            System.out.println(searchHit.getSourceAsString());
            //取文档属性
            System.out.println("----------------文档的属性");
            Map<String,Object> map = searchHit.getSourceAsMap();
            System.out.println(map);
            System.out.println("***************************高亮结果*********************");
            Map<String, HighlightField> highLightMap = searchHit.getHighlightFields();
            System.out.println(highLightMap);
            HighlightField field = highLightMap.get("title");
            Text [] fragments = field.getFragments();
            if(fragments != null) System.out.println(fragments[0].toString());
        });


    }

}
