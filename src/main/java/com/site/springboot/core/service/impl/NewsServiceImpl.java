package com.site.springboot.core.service.impl;

import com.alibaba.excel.util.ListUtils;
import com.site.springboot.core.dao.NewsMapper;
import com.site.springboot.core.entity.News;
import com.site.springboot.core.entity.NewsCategory;
import com.site.springboot.core.entity.NewsFile;
import com.site.springboot.core.service.CategoryService;
import com.site.springboot.core.service.NewsService;
import com.site.springboot.core.util.PageQueryUtil;
import com.site.springboot.core.util.PageResult;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsMapper newsMapper;
    @Resource
    private CategoryService categoryService;

    @Override
    public String saveNews(News news) {
        if (newsMapper.insertSelective(news) > 0) {
            return "success";
        }
        return "保存失败";
    }

    @Override
    public PageResult getNewsPage(PageQueryUtil pageUtil) {
        List<News> newsList = newsMapper.findNewsList(pageUtil);
        int total = newsMapper.getTotalNews(pageUtil);
        PageResult pageResult = new PageResult(newsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @Override
    public Boolean deleteBatch(Integer[] ids) {
        return newsMapper.deleteBatch(ids)>0;
    }

    @Override
    public News queryNewsById(Long newsId) {
        return newsMapper.selectByPrimaryKey(newsId);
    }

    @Override
    public String updateNews(News news) {
        News newsForUpdate = newsMapper.selectByPrimaryKey(news.getNewsId());
        if (newsForUpdate == null) {
            return "数据不存在";
        }
        news.setNewsCategoryId(news.getNewsCategoryId());
        news.setNewsContent(news.getNewsContent());
        news.setNewsCoverImage(news.getNewsCoverImage());
        news.setNewsStatus(news.getNewsStatus());
        news.setNewsTitle(news.getNewsTitle());
        news.setUpdateTime(new Date());
        if (newsMapper.updateByPrimaryKeySelective(news) > 0) {
            return "success";
        }
        return "修改失败";
    }

    public List<News> findNewsAll(){
        return newsMapper.findNewsAll();
    }

    @Override
    public List<NewsFile> getNewsFileList() {
        List<News> newsList = newsMapper.findNewsAll();
        List<NewsFile> res = new ArrayList<>();
        for (News news: newsList) {
            NewsFile file = new NewsFile();
            file.setNewsTitle(news.getNewsTitle());
            NewsCategory category = categoryService.getById(news.getNewsCategoryId());
            file.setNewsCategory(category.getCategoryName());
            file.setNewsCoverImage(news.getNewsCoverImage());
            file.setNewsContent(news.getNewsContent());
            if (news.getNewsStatus() ==0){
                file.setNewsStatus("草稿");
            }else {
                file.setNewsStatus("已发布");
            }
            file.setNewsViews(news.getNewsViews());
            file.setCreateTime(news.getCreateTime());
            res.add(file);
        }
        return res;
    }
}
