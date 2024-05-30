package com.site.springboot.core.service;

import com.site.springboot.core.entity.News;
import com.site.springboot.core.entity.NewsFile;
import com.site.springboot.core.entity.NewsVo;
import com.site.springboot.core.util.PageQueryUtil;
import com.site.springboot.core.util.PageResult;

import java.util.List;

public interface NewsService {
    News saveNews(News news);

    PageResult getNewsPage(PageQueryUtil pageUtil);

    Boolean deleteBatch(Integer[] ids);

    /**
     * 根据id获取详情
     *
     * @param newsId
     * @return
     */
    News queryNewsById(Long newsId);

    /**
     * 后台修改
     *
     * @param news
     * @return
     */
    String updateNews(News news);

    List<News> findNewsAll();

    List<NewsFile> getNewsFileList();

    List<News> getNewsRecent();

    NewsVo getNewsAndComments(Long newsId);

    PageResult getLastedNews(PageQueryUtil pageUtil);
    List<NewsVo> findLastedNewsList();
}
