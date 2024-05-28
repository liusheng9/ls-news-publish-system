package com.site.springboot.core.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.alibaba.excel.annotation.write.style.ContentRowHeight;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import lombok.Data;

import java.util.Date;

/**
 * created by liush on 2024/5/28
 **/

@ContentRowHeight(18)
@ColumnWidth(18)
@Data
@HeadRowHeight(18)
public class NewsFile {

    @ExcelProperty(value = "title", index = 0)
    private String newsTitle;

    @ExcelProperty(value = "category", index = 1)
    private String newsCategory;

    @ExcelProperty(value = "image", index = 2)
    private String newsCoverImage;

    @ExcelProperty(value = "content", index = 3)
    private String newsContent;

    @ExcelProperty(value = "state", index = 4)
    private String newsStatus;

    @ExcelProperty(value = "views", index = 5)
    private Long newsViews;
    @ExcelProperty(value = "create_time", index = 6)
    private Date createTime;

}

