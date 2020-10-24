package cn.lmcw.bookspider.web.book.service;

import cn.lmcw.bookspider.web.book.entity.SourceBook;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author dingyong
 * @since 2020-10-17
 */
public interface IBookService extends IService<SourceBook> {
    SourceBook findBook(String name, String author);
}
