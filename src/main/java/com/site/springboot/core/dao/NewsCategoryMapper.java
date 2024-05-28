package com.site.springboot.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.site.springboot.core.entity.NewsCategory;
import com.site.springboot.core.util.PageQueryUtil;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Component;

import java.util.List;


public interface NewsCategoryMapper extends BaseMapper<NewsCategory> {

}