package com.site.springboot.core.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.site.springboot.core.dao.NewsCategoryMapper;
import com.site.springboot.core.entity.NewsCategory;
import com.site.springboot.core.service.CategoryService;
import com.site.springboot.core.util.PageQueryUtil;
import com.site.springboot.core.util.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<NewsCategoryMapper, NewsCategory> implements CategoryService {

}
