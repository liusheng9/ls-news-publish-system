package com.site.springboot.core.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;


import java.util.Date;
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Data
@TableName("tb_news")
@Document(indexName = "news_index")
public class NewsIndex {

    @Field(type= FieldType.Long)
    @Id
    private Long newsId;
    @Field(type= FieldType.Keyword)
    private String newsTitle;
    @Field(type= FieldType.Long)
    private Long newsCategoryId;
    @Field(type= FieldType.Text)
    private String newsCoverImage;
    @Field(type= FieldType.Text)
    private String newsContent;
    @Field(type =FieldType.Byte)
    private Byte newsStatus;
    @Field(type= FieldType.Long)
    private Long newsViews;
    @Field(type =FieldType.Byte)
    private Byte isDeleted;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Field(type =FieldType.Date)
    private Date createTime;

    @Field(type =FieldType.Date)
    private Date updateTime;


    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle == null ? null : newsTitle.trim();
    }


    public void setNewsCoverImage(String newsCoverImage) {
        this.newsCoverImage = newsCoverImage == null ? null : newsCoverImage.trim();
    }


}