package com.site.springboot.core.service;

import com.site.springboot.core.entity.News;
import com.site.springboot.core.entity.NewsIndex;
import org.apache.ibatis.javassist.NotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * created by liush on 2024/5/30
 **/
public interface NewsEsService {
    void initIndex();

    NewsIndex saveNewsToEs(News news);

    Optional<NewsIndex> getNewsByIdFromEs(Long id);


    void deleteNewsByIdToEs(List<Long> id);

    NewsIndex updateNewsToEs(News updatedNews) throws NotFoundException;

    List<News> searchNews(String keyword);


}