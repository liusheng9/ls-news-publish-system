package com.site.springboot.core.service.impl;

import com.site.springboot.core.dao.AdminMapper;
import com.site.springboot.core.entity.Admin;
import com.site.springboot.core.service.AdminService;
import com.site.springboot.core.util.MD5Util;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import jakarta.annotation.Resource;
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

}
