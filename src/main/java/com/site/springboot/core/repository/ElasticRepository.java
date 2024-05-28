package com.site.springboot.core.repository;

import com.site.springboot.core.entity.NewsIndex;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * created by liush on 2024/5/28
 **/
public interface ElasticRepository extends ElasticsearchRepository<NewsIndex,Long> {

}
