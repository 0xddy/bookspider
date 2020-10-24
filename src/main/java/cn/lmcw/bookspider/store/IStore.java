package cn.lmcw.bookspider.store;

import cn.lmcw.bookspider.model.BookHref;
import cn.lmcw.bookspider.model.Category;
import cn.lmcw.bookspider.model.ChapterContent;
import cn.lmcw.bookspider.web.book.entity.SourceBook;
import cn.lmcw.bookspider.web.book.entity.SourceChapter;
import cn.lmcw.bookspider.web.bookurl.entity.Bookurl;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface IStore {

    Object getBookByHash(String hash);

    void saveBookUrl(BookHref bookHref, Category category, String projectId);

    int getBookUrlCount(String projectId);

    IPage<Bookurl> getUnfinishedBookUrl(String projectId, int page);

    SourceBook findSourceBook(String name, String author);

    boolean saveSourceBook(SourceBook sourceBook);

    List<SourceChapter> findSourceChaptersByBookId(int bookId);

    boolean saveChapters(List<SourceChapter> chapters);
}
