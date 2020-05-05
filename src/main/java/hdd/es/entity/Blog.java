package hdd.es.entity;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author hdd
 * @date 2020-05-04
 */
@Document(indexName = "blog", type = "blog")
public class Blog {

    @Id
    @Field(type = FieldType.Keyword, store = true)
    private String blog_id;
    @Field(type = FieldType.Text, store = true, analyzer = "ik_smart")
    private String title;
    @Field(type = FieldType.Text, store = true, analyzer = "ik_smart")
    private String content;
    @Field(type = FieldType.Text, store = true, analyzer = "ik_smart")
    private String keyword;
    @Field(type = FieldType.Date,store = true)
    private Date create_time;
    @Field(type = FieldType.Integer,store = true)
    private Integer read_count;
    @Field(type = FieldType.Integer,store = true)
    private Integer praise_count;
    @Field(type = FieldType.Keyword,store = true)
    private String user_id;
    @Field(type = FieldType.Keyword,store = true)
    private String category_id;
    @Field(type = FieldType.Text,store = true)
    private String summary;

    public String getBlog_id() {
        return blog_id;
    }

    public void setBlog_id(String blog_id) {
        this.blog_id = blog_id == null ? null : blog_id.trim();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword == null ? null : keyword.trim();
    }

    public Date getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Date create_time) {
        this.create_time = create_time;
    }

    public Integer getRead_count() {
        return read_count;
    }

    public void setRead_count(Integer read_count) {
        this.read_count = read_count;
    }

    public Integer getPraise_count() {
        return praise_count;
    }

    public void setPraise_count(Integer praise_count) {
        this.praise_count = praise_count;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id == null ? null : user_id.trim();
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id == null ? null : category_id.trim();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this).toString();
    }

}