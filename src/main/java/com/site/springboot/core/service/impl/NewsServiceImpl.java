package com.site.springboot.core.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.site.springboot.core.dao.NewsCommentMapper;
import com.site.springboot.core.dao.NewsMapper;
import com.site.springboot.core.entity.*;
import com.site.springboot.core.service.CategoryService;
import com.site.springboot.core.service.CommentService;
import com.site.springboot.core.service.NewsService;
import com.site.springboot.core.util.PageQueryUtil;
import com.site.springboot.core.util.PageResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@Service
public class NewsServiceImpl implements NewsService {

    @Autowired
    private NewsMapper newsMapper;
    @Resource
    private CategoryService categoryService;

    @Autowired
    private NewsCommentMapper commentMapper;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private static final Logger logger = Logger.getLogger(NewsServiceImpl.class.getName());

    @CachePut(value = "news", key = "'news:' + #news.newsId")
    @Override
    public News saveNews(News news) {
        if (newsMapper.insertSelective(news) > 0) {
            return news;
        }
        return null;
    }

    @Override
    public PageResult getNewsPage(PageQueryUtil pageUtil) {
        List<News> newsList = newsMapper.findNewsList(pageUtil);
        int total = newsMapper.getTotalNews(pageUtil);
        PageResult pageResult = new PageResult(newsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }

    @CacheEvict(value = "news", key = "'news:'+#ids", allEntries = false)
    @Override
    public Boolean deleteBatch(Integer[] ids) {
        return newsMapper.deleteBatch(ids)>0;

    }

    @Cacheable(value = "news", key = "'news:' + #newsId")
    @Override
    public News queryNewsById(Long newsId) {
        logger.info("查询数据库queryNewsById:"+"newId");
        return newsMapper.selectByPrimaryKey(newsId);
    }

    @CachePut(value = "news", key = "'news:' + #news.newsId")
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
    @Override
    public List<News> getNewsRecent(){
        return newsMapper.getNewsRecent();
    }

    @Override
    public NewsVo getNewsAndComments(Long newsId) {
        News news = newsMapper.selectByPrimaryKey(newsId);
        List<NewsComment> newsComments = commentMapper.findNewsCommentListByNewsId(newsId);
        NewsVo newsVo = new NewsVo();
        newsVo.setNewsId(news.getNewsId());
        newsVo.setNewsTitle(news.getNewsTitle());
        newsVo.setNewsCoverImage(news.getNewsCoverImage());
        newsVo.setNewsContent(news.getNewsContent());
        newsVo.setNewsStatus(news.getNewsStatus());
        newsVo.setNewsCategoryId(news.getNewsCategoryId());
        newsVo.setNewsViews(news.getNewsViews());
        newsVo.setCreateTime(news.getCreateTime());
        newsVo.setComments(newsComments);
        return newsVo;
    }

    @Override
    public PageResult getLastedNews(PageQueryUtil pageUtil) {
        List<NewsVo> newsList = this.findLastedNewsList();
        int total = newsMapper.getTotalNews(pageUtil);
        PageResult pageResult = new PageResult(newsList, total, pageUtil.getLimit(), pageUtil.getPage());
        return pageResult;
    }
    @Override
    public List<NewsVo> findLastedNewsList() {
        List<NewsVo> newsVoList = new ArrayList<>();
        List<News> news = newsMapper.getNewsRecent();
        for (News news1 : news){
            NewsVo newsVo = new NewsVo();
            List<NewsComment> comments = commentMapper.findNewsCommentListByNewsId(news1.getNewsId());
            newsVo.setNewsId(news1.getNewsId());
            newsVo.setNewsTitle(news1.getNewsTitle());
            newsVo.setNewsCoverImage(news1.getNewsCoverImage());
            newsVo.setNewsStatus(news1.getNewsStatus());
            newsVo.setNewsViews(news1.getNewsViews());
            newsVo.setCreateTime(news1.getCreateTime());
            newsVo.setComments(comments);
            newsVoList.add(newsVo);
        }
        return newsVoList;
    }

    @Override
    public String likeNews(Long newsId, HttpServletRequest request) {
        try {
            Object loginUserIdObj = request.getAttribute("loginUserId");
            Long userId;
            if(loginUserIdObj != null) userId=Long.parseLong(loginUserIdObj.toString());
            else throw new Exception("未登录");
            String key="news:"+newsId+":likes";
            Long addResult=stringRedisTemplate.opsForSet().add(key,userId.toString());
            if (addResult!=null && addResult==1) {
                stringRedisTemplate.opsForValue().increment(key,1);
                return "success";
            }else return "fail";
        }catch (Exception e){
            return "fail";
        }
    }
    @Override
    public String dislikeNews(Long newsId, HttpServletRequest request) {
        try {
            Object loginUserIdObj = request.getAttribute("loginUserId");
            Long userId;
            if(loginUserIdObj != null) userId=Long.parseLong(loginUserIdObj.toString());
            else throw new Exception("未登录");
            String key="news:"+newsId+":likes";

            // 判断用户是否已经点赞
            boolean isMember = stringRedisTemplate.opsForSet().isMember(key, userId.toString());
            if (isMember) {
                // 如果已点赞，则从点赞集合中移除用户
                stringRedisTemplate.opsForSet().remove(key, userId.toString());
                // 点赞数减一
                stringRedisTemplate.opsForValue().decrement(key);
                return "success";
            } else {
                return "already_liked";
            }
        } catch (Exception e) {
            return "fail";
        }
    }


    @Override
    public Long getNewsLikes(Long newsId){
        String count=stringRedisTemplate.opsForValue().get("news:"+newsId+":likes:count");
        return count!=null?Long.parseLong(count):0L;
    }

    @Override
    //获取用户点赞状态
    public Boolean isHasLiked(Long newsId, HttpServletRequest request) throws Exception{
        Object loginUserIdObj = request.getAttribute("loginUserId");
        String userId;
        if(loginUserIdObj!=null){
            userId=loginUserIdObj.toString();
        }else {throw new Exception("未登录");}
        return stringRedisTemplate.opsForSet().isMember("news:"+newsId+":likes", userId);
    }
}