package com.site.springboot.core.controller.admin;

import cn.hutool.captcha.ShearCaptcha;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.site.springboot.core.dao.AdminMapper;
import com.site.springboot.core.entity.Admin;

import com.site.springboot.core.service.*;
import com.site.springboot.core.util.*;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Controller
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private  NewsService newsService;

    @Autowired
    private CommentService commentService;



    @GetMapping({"/login"})
    public String login() {
        return "admin/login";
    }

    @GetMapping({"", "/", "/index", "/index.html"})
    public String index(HttpServletRequest request) {
        request.setAttribute("path", "index");
        return "admin/index";
    }

    @PostMapping(value = "/login")
    public String login(@RequestParam("userName") String userName,
                        @RequestParam("password") String password,
                        @RequestParam("verifyCode") String verifyCode,
                        HttpSession session) {
        if (!StringUtils.hasText(verifyCode)) {
            session.setAttribute("errorMsg", "验证码不能为空");
            return "admin/login";
        }
        if (!StringUtils.hasText(userName) || !StringUtils.hasText(password)) {
            session.setAttribute("errorMsg", "用户名或密码不能为空");
            return "admin/login";
        }
        ShearCaptcha shearCaptcha = (ShearCaptcha) session.getAttribute("verifyCode");
        if (shearCaptcha == null || !shearCaptcha.verify(verifyCode)) {
            session.setAttribute("errorMsg", "验证码错误");
            return "admin/login";
        }

        QueryWrapper<Admin> queryWrapper =new QueryWrapper<>();
        String passwordMd5 = MD5Util.MD5Encode(password, "UTF-8");
        queryWrapper.eq("login_name", userName).eq("login_password", passwordMd5);
        Admin adminUser = adminService.getOne(queryWrapper);
        if (adminUser != null) {
            session.setAttribute("loginUser", adminUser.getAdminNickName());
            session.setAttribute("loginUserId", adminUser.getAdminId());
            //session过期时间设置为7200秒 即两小时
            //session.setMaxInactiveInterval(60 * 60 * 2);
            return "redirect:/admin/index";
        } else {
            session.setAttribute("errorMsg", "登陆失败");
            return "admin/login";
        }
    }

    @GetMapping("/profile")
    public String profile(HttpServletRequest request) {
        Long loginUserId = (long) request.getSession().getAttribute("loginUserId");
        Admin adminUser = adminService.getById (loginUserId);
        if (adminUser == null) {
            return "admin/login";
        }
        request.setAttribute("path", "profile");
        request.setAttribute("loginUserName", adminUser.getLoginName());
        request.setAttribute("nickName", adminUser.getAdminNickName());
        return "admin/profile";
    }

    @PostMapping("/profile/password")
    @ResponseBody
    public String passwordUpdate(HttpServletRequest request, @RequestParam("originalPassword") String originalPassword,
                                 @RequestParam("newPassword") String newPassword) {
        if (!StringUtils.hasText(originalPassword) || !StringUtils.hasText(newPassword)) {
            return "参数不能为空";
        }
        Long loginUserId = (long) request.getSession().getAttribute("loginUserId");
        Admin adminUser = adminService.getById (loginUserId);
        if(adminUser!=null){
            String originalPasswordMd5 = MD5Util.MD5Encode(originalPassword, "UTF-8");
            String newPasswordMd5 = MD5Util.MD5Encode(newPassword, "UTF-8");
            //比较原密码是否正确
            if (originalPasswordMd5.equals(adminUser.getLoginPassword())) {
                //设置新密码并修改
                adminUser.setLoginPassword(newPasswordMd5);
                boolean success = adminService.updateById(adminUser);
                if (success==true) {
                    //修改成功后清空session中的数据，前端控制跳转至登录页
                    request.getSession().removeAttribute("loginUserId");
                    request.getSession().removeAttribute("loginUser");
                    request.getSession().removeAttribute("errorMsg");
                    return "success";
                }
            }

        }
        return "修改失败";
    }

    @PostMapping("/profile/name")
    @ResponseBody
    public String nameUpdate(HttpServletRequest request, @RequestParam("loginUserName") String loginUserName,
                             @RequestParam("nickName") String nickName) {
        if (!StringUtils.hasText(loginUserName) || !StringUtils.hasText(nickName)) {
            return "参数不能为空";
        }
        Long loginUserId = (long) request.getSession().getAttribute("loginUserId");
        Admin adminUser = adminService.getById (loginUserId);
        //当前用户非空才可以进行更改
        if (adminUser != null) {
            adminUser.setLoginName(loginUserName);
            adminUser.setAdminNickName(nickName);
            boolean success = adminService.updateById(adminUser);
            if (success==true) {
                //修改成功则返回true
                return "success";
            }
        }
        return "修改失败";
    }
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute("loginUserId");
        request.getSession().removeAttribute("loginUser");
        request.getSession().removeAttribute("errorMsg");
        return "admin/login";
    }
    @GetMapping("/lasted-news")
    @ResponseBody
    public Result listLastedNews(@RequestParam Map<String, Object> params) {
        if (ObjectUtils.isEmpty(params.get("page")) || ObjectUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        PageResult pageResult=newsService.getLastedNews( new PageQueryUtil(params));
        return ResultGenerator.genSuccessResult(pageResult);
    }


}
