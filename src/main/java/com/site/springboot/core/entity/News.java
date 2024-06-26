package com.site.springboot.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@ToString
@Data
@TableName("tb_news")
public class News {
    @TableId(value ="news_id" ,type = IdType.AUTO)
    private Long newsId;

    private String newsTitle;

    private Long newsCategoryId;

    private String newsCoverImage;

    private String newsContent;

    private Byte newsStatus;

    private Long newsViews;

    private Byte isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    private Date updateTime;


    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle == null ? null : newsTitle.trim();
    }


    public void setNewsCoverImage(String newsCoverImage) {
        this.newsCoverImage = newsCoverImage == null ? null : newsCoverImage.trim();
    }


}