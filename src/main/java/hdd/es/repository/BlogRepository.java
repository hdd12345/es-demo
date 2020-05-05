package hdd.es.repository;

import hdd.es.entity.Blog;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.List;

public interface BlogRepository extends ElasticsearchRepository<Blog,Long> {
    List<Blog> findByTitleOrContent(String title, String content);
    List<Blog> findByTitleOrContent(String title, String content, Pageable pageable);
    List<Blog> findByTitleOrContentOrSummary(String title, String content, String summary);
}
