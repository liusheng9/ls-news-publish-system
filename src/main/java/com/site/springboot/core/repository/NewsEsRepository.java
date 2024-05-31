package com.site.springboot.core.repository;

import com.site.springboot.core.entity.NewsIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

/**
 * created by liush on 2024/5/28
 **/
public interface NewsEsRepository extends ElasticsearchRepository<NewsIndex,Long> {

    List<NewsIndex> findByNewsContentLike(String content);
}
