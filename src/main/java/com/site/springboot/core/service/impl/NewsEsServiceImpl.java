package com.site.springboot.core.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.site.springboot.core.dao.NewsMapper;
import com.site.springboot.core.entity.News;
import com.site.springboot.core.entity.NewsIndex;
import com.site.springboot.core.repository.NewsEsRepository;
import com.site.springboot.core.service.NewsEsService;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * created by liush on 2024/5/31
 **/
@Service
public class NewsEsServiceImpl implements NewsEsService {
    @Autowired
    private NewsEsRepository newsEsRepository;

    @Autowired
    private NewsMapper newsMapper;

    @Resource
    private ElasticsearchTemplate template ;

    @Override
    public NewsIndex saveNewsToEs(News news) {
        NewsIndex newsIndex = new NewsIndex();
        BeanUtil.copyProperties(news, newsIndex);
        return newsEsRepository.save(newsIndex);
    }

    public Optional<NewsIndex> getNewsByIdFromEs(Long id) {
        return newsEsRepository.findById(id);
    }

    // 删除（根据ID）操作
    public void deleteNewsByIdToEs(List<Long> ids) {
        newsEsRepository.deleteAllById(ids);
    }

    // 更新操作
    public NewsIndex updateNewsToEs(News updatedNews) throws NotFoundException {
        Optional<NewsIndex> existingNews = newsEsRepository.findById(updatedNews.getNewsId());
        if (existingNews.isPresent()) {
            NewsIndex newsEs = existingNews.get();
            //updatedNews属性拷贝到newsEs
            BeanUtil.copyProperties(updatedNews, newsEs);
            return newsEsRepository.save(newsEs);
        } else {
            throw new NotFoundException("News not found with id: " + updatedNews.getNewsId());
        }
    }

    @Override
    public List<News> searchNews(String keyword) {
        List<NewsIndex> newsIndexList = newsEsRepository.findByNewsContentLike(keyword);
        List<News> res = new ArrayList<>();
        for (NewsIndex newsIndex : newsIndexList)  res.add(newsMapper.selectByPrimaryKey(newsIndex.getNewsId()));
        return res;
    }

    @Override
    public void initIndex (){
        // 处理索引结构
        IndexOperations indexOps = template.indexOps(NewsIndex.class);
        if (indexOps.exists()){
            boolean delFlag = indexOps.delete();
            indexOps.createMapping(NewsIndex.class);
        } else {
            indexOps.createMapping(NewsIndex.class);
        }
        List<News> newsList = newsMapper.findNewsAll();
        if (newsList.size() > 0){
            List<NewsIndex> NewsEsList = new ArrayList<>() ;
            newsList.forEach(News -> {
                NewsIndex NewsIndex = new NewsIndex() ;
                BeanUtils.copyProperties(News,NewsIndex);
                NewsEsList.add(NewsIndex);
            });
            template.save(NewsEsList);
        }
    }
}