package com.site.springboot.core.service;
import com.site.springboot.core.entity.News;
import com.site.springboot.core.entity.NewsIndex;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * created by liush on 2024/5/28
 **/
@Service
public class NewsIndexService {
    private static final Logger log = LoggerFactory.getLogger(NewsIndexService.class);
    @Resource
    private NewsService newsService ;

    @Resource
    private ElasticsearchTemplate template ;

    /**
     * 初始化索引结构和数据
     */
    public void initIndex(){
        // 处理索引结构
        IndexOperations indexOps = template.indexOps(NewsIndex.class);
        if (indexOps.exists()){
            boolean delFlag = indexOps.delete();
            log.info("news_index exists，delete:{}",delFlag);
            indexOps.createMapping(NewsIndex.class);
        } else {
            log.info("news_index not exists");
            indexOps.createMapping(NewsIndex.class);
        }
        // 同步数据库表记录
        List<News> newsList = newsService.findNewsAll();
        if (newsList.size() > 0){
            List<NewsIndex> newsIndexList = new ArrayList<>();
            newsList.forEach(news -> {
                NewsIndex newsIndex = new NewsIndex() ;
                BeanUtils.copyProperties(news,newsIndex);
                newsIndexList.add(newsIndex);
            });
            template.save(newsIndexList);
        }
        // ID查询
        NewsIndex contentsIndex = template.get("10",NewsIndex.class);
        log.info("contents-index-10:{}",contentsIndex);
    }
}
