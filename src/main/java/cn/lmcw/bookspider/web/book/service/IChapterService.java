package cn.lmcw.bookspider.web.book.service;

import cn.lmcw.bookspider.web.book.entity.SourceChapter;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IChapterService extends IService<SourceChapter> {

    List<SourceChapter> findChaptersByBookId(int bookId);

}
