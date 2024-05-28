package com.site.springboot.core.controller.admin;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.site.springboot.core.dao.NewsCategoryMapper;
import com.site.springboot.core.entity.NewsCategory;
import com.site.springboot.core.service.CategoryService;
import com.site.springboot.core.util.PageQueryUtil;
import com.site.springboot.core.util.PageResult;
import com.site.springboot.core.util.Result;
import com.site.springboot.core.util.ResultGenerator;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/admin")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private NewsCategoryMapper categoryMapper;


    @GetMapping("/categories")
    public String categoryPage(HttpServletRequest request) {
        request.setAttribute("path", "categories");
        return "admin/category";
    }

    /**
     * 分类列表
     */
    @RequestMapping(value = "/categories/list", method = RequestMethod.GET)
    @ResponseBody
    public Result list(@RequestParam Map<String, Object> params) {
        if (ObjectUtils.isEmpty(params.get("page")) || ObjectUtils.isEmpty(params.get("limit"))) {
            return ResultGenerator.genFailResult("参数异常！");
        }

        PageQueryUtil pageUtil = new PageQueryUtil(params);
        IPage<NewsCategory> page = new Page<>(pageUtil.getPage(), pageUtil.getLimit());

        QueryWrapper<NewsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_deleted", 0).orderByDesc("category_id");

        IPage<NewsCategory> categoryIPage = categoryMapper.selectPage(page, queryWrapper);

        PageResult pageResult = new PageResult(categoryIPage.getRecords(),
                (int) categoryIPage.getTotal(),
                (int) categoryIPage.getSize(),
                (int) categoryIPage.getCurrent());
        return ResultGenerator.genSuccessResult(pageResult);

    }

    /**
     * 详情
     */
    @RequestMapping(value = "/categories/info/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Result info(@PathVariable("id") Long id) {
        NewsCategory newsCategory = categoryService.getById(id);
        if(newsCategory==null || newsCategory.getCategoryId() ==1) return ResultGenerator.genFailResult("分类不存在");
        return ResultGenerator.genSuccessResult(newsCategory);
    }


    /**
     * 分类添加
     */
    @RequestMapping(value = "/categories/save", method = RequestMethod.POST)
    @ResponseBody
    public Result save(@RequestParam("categoryName") String categoryName) {
        if (!StringUtils.hasText(categoryName)) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        QueryWrapper<NewsCategory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_name", categoryName);
        if(categoryService.getOne(queryWrapper)!=null) return ResultGenerator.genFailResult("名称重复");
        NewsCategory category = new NewsCategory();
        category.setCategoryName(categoryName);
        category.setIsDeleted((byte) 0);
        categoryService.save(category);
        return ResultGenerator.genSuccessResult();
    }


    /**
     * 分类修改
     */
    @RequestMapping(value = "/categories/update", method = RequestMethod.POST)
    @ResponseBody
    public Result update(@RequestParam("categoryId") Long categoryId,
                         @RequestParam("categoryName") String categoryName) {
        if (!StringUtils.hasText(categoryName)) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        NewsCategory newsCategory= categoryService.getById(categoryId);
        if(newsCategory==null||newsCategory.getIsDeleted()==1) return ResultGenerator.genFailResult("分类不存在");
        newsCategory.setCategoryName(categoryName);
        QueryWrapper<NewsCategory> queryWrapper = new QueryWrapper<>();

        boolean success = categoryService.updateById(newsCategory);
        if (success) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("分类名称重复");
        }
    }


    /**
     * 分类删除
     */
    @RequestMapping(value = "/categories/delete", method = RequestMethod.POST)
    @ResponseBody
    public Result delete(@RequestBody Integer[] ids) {
        if (ids.length < 1) {
            return ResultGenerator.genFailResult("参数异常！");
        }
        List<NewsCategory> newsCategories = categoryService.listByIds(Arrays.asList(ids));
        for(NewsCategory category : newsCategories)  category.setIsDeleted((byte) 1);
        boolean success = categoryService.updateBatchById(newsCategories);
        if (success) {
            return ResultGenerator.genSuccessResult();
        } else {
            return ResultGenerator.genFailResult("删除失败");
        }
    }

}
