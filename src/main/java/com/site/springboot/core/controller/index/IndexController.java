package com.site.springboot.core.controller.index;

import cn.hutool.captcha.ShearCaptcha;
import com.site.springboot.core.entity.News;
import com.site.springboot.core.entity.NewsComment;
import com.site.springboot.core.entity.NewsIndex;
import com.site.springboot.core.repository.NewsEsRepository;
import com.site.springboot.core.service.CommentService;
import com.site.springboot.core.service.NewsService;
import com.site.springboot.core.util.AntiXssUtils;
import com.site.springboot.core.util.Result;
import com.site.springboot.core.util.ResultGenerator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

import java.util.List;


@Controller
public class IndexController {
    @Resource
    private CommentService commentService;
    @Resource
    private NewsService newsService;

    private NewsEsRepository elasticRepository;

    /**
     * 详情页
     *
     * @return
     */
    @GetMapping({"/news/{newsId}"})
    public String detail(HttpServletRequest request, @PathVariable("newsId") Long newsId) throws Exception {
        News newsDetail = newsService.queryNewsById(newsId);
        if (newsDetail != null) {
            request.setAttribute("newsDetail", newsDetail);
            request.setAttribute("pageName", "详情");
            request.setAttribute("likesCount",newsService.getNewsLikes(newsId));
            request.setAttribute("isHasLiked",newsService.isHasLiked(newsId,request));
            return "index/detail";
        } else {
            return "error/error_404";
        }

    }
    /**
     * 展示前十个资讯和相应的评论
     */
    @GetMapping({"/", ""})
    public String indexPage(Model model) {
        return "admin/index";
    }

    /**
     * 评论操作
     */
    @PostMapping(value = "/news/comment")
    @ResponseBody
    public Result comment(HttpServletRequest request, HttpSession session,
                          @RequestParam Long newsId, @RequestParam String verifyCode,
                          @RequestParam String commentator, @RequestParam String commentBody) {
        if (!StringUtils.hasText(verifyCode)) {
            return ResultGenerator.genFailResult("验证码不能为空");
        }

        ShearCaptcha shearCaptcha = (ShearCaptcha) session.getAttribute("verifyCode");
        if (shearCaptcha == null || !shearCaptcha.verify(verifyCode)) {
            return ResultGenerator.genFailResult("验证码错误");
        }
        String ref = request.getHeader("Referer");
        if (!StringUtils.hasText(ref)) {
            return ResultGenerator.genFailResult("非法请求");
        }
        if (null == newsId || newsId < 0) {
            return ResultGenerator.genFailResult("非法请求");
        }
        if (!StringUtils.hasText(commentator)) {
            return ResultGenerator.genFailResult("请输入称呼");
        }
        if (!StringUtils.hasText(commentBody)) {
            return ResultGenerator.genFailResult("请输入评论内容");
        }
        if (commentBody.trim().length() > 200) {
            return ResultGenerator.genFailResult("评论内容过长");
        }
        NewsComment comment = new NewsComment();
        comment.setNewsId(newsId);
        comment.setCommentator(AntiXssUtils.cleanString(commentator));
        comment.setCommentBody(AntiXssUtils.cleanString(commentBody));
        session.removeAttribute("verifyCode");//留言成功后删除session中的验证码信息
        return ResultGenerator.genSuccessResult(commentService.addComment(comment));
    }

    @GetMapping("/search-es")
    public List<NewsIndex> searchByEs(@RequestParam("keyword") String keyword){
        List<NewsIndex> byNewsConntentLike = elasticRepository.findByNewsContentLike(keyword);
        return byNewsConntentLike;
    }
}
