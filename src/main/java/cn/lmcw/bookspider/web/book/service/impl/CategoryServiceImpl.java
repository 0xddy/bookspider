package cn.lmcw.bookspider.web.book.service.impl;

import cn.lmcw.bookspider.web.book.entity.SourceCategory;
import cn.lmcw.bookspider.web.book.mapper.CategoryMapper;
import cn.lmcw.bookspider.web.book.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, SourceCategory> implements ICategoryService {
}
