package cn.lmcw.bookspider.web.book.service.impl;

import cn.lmcw.bookspider.web.book.entity.SourceBook;
import cn.lmcw.bookspider.web.book.mapper.BookMapper;
import cn.lmcw.bookspider.web.book.service.IBookService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author dingyong
 * @since 2020-10-17
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, SourceBook> implements IBookService {

    @Override
    public SourceBook findBook(String name, String author) {
        return getBaseMapper().selectOne(new QueryWrapper<SourceBook>().eq("name", name).eq("author", author));
    }
}
