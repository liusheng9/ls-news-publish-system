package com.site.springboot.core.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.site.springboot.core.entity.Admin;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

@Component
public interface AdminMapper extends BaseMapper<Admin> {

}