package hdd.es;

import hdd.es.entity.Blog;
import hdd.es.repository.BlogRepository;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ElasticSearchBootTest {

    @Autowired
    private BlogRepository blogRepository;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 创建索引库
     */
    @Test
    public void createIndex(){
        //创建索引，并配置映射关系
        elasticsearchTemplate.createIndex(Blog.class);
        //配置映射关系
        //elasticsearchTemplate.putMapping(Blog.class);
    }

    /**
     * 添加文档
     */
    @Test
    public void addDocument(){
        //创建一个实体对象
        Blog blog = new Blog();
        blog.setBlog_id("11");
        blog.setCategory_id("2");
        blog.setUser_id("1");
        blog.setTitle("这是博客的标题");
        blog.setSummary("这是一篇博客的摘要");
        blog.setContent("这是一篇博客的内容");
        blog.setKeyword("blog blog");
        blog.setCreate_time(new Date());
        //把文档写入索引库
        blogRepository.save(blog);
    }

    /**
     * 修改文档
     */
    @Test
    public void updateDocument(){
        //创建一个实体对象
        Blog blog = new Blog();
        blog.setBlog_id("11");
        blog.setCategory_id("2");
        blog.setUser_id("1");
        blog.setTitle("这是一篇博客的标题");
        blog.setSummary("这是一篇博客的摘要");
        blog.setContent("这是一篇博客修改后的内容");
        blog.setKeyword("blog blog");
        blog.setCreate_time(new Date());
        //把文档写入索引库
        blogRepository.save(blog);
    }

    /**
     * 删除文档
     */
    @Test
    public void deleteDocument(){
        Blog blog = new Blog();
        blog.setBlog_id("11");
        //把文档写入索引库
        //blogRepository.delete(blog);
        //blogRepository.deleteAll();//删除所有文档
        //blogRepository.deleteById(11l);//根据id删除文档
    }

    /**
     * 查询文档
     */
    @Test
    public void queryDocument(){
        Iterable<Blog> blogIterable = blogRepository.findAll();//查询所有
        blogIterable.forEach(b -> System.out.println(b.getContent()));
        //Optional<Blog> op = blogRepository.findById(10l);//根据id查询
        //Blog blog = op.get();
        //blogRepository.findAllById();//根据多个id查询，参数类型为Iterable
    }

    /**
     * 自定义查询
     */
    @Test
    public void queryDocument2(){
        List<Blog> blogIterable = blogRepository.findByTitleOrContent("博客和blog","博客和blog");
        blogIterable.forEach(b -> System.out.println(b));
        List<Blog> blogIterable1 = blogRepository.findByTitleOrContentOrSummary("一篇的博客","一篇的博客","一篇的博客");
        blogIterable1.forEach(b -> System.out.println(b));
    }

    /**
     * 分页查询
     */
    @Test
    public void queryDocument3(){
        Pageable pageable = PageRequest.of(0,1);
        List<Blog> blogIterable = blogRepository.findByTitleOrContent("一篇的博客","一篇的博客",pageable);
        blogIterable.forEach(b -> System.out.println(b));
    }

    /**
     * 使用NativeSearchQuery查询（分词后使用or连接）
     */
    @Test
    public void queryDocument4(){
        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.queryStringQuery("博客和blog").defaultField("title"))
                .withPageable(PageRequest.of(0,15))
                //.withHighlightBuilder() //设置高亮
                .build();
        List<Blog> blogList = elasticsearchTemplate.queryForList(query,Blog.class);
        blogList.forEach(b -> System.out.println(b));
    }


}
