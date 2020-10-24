package cn.lmcw.bookspider.store;

import cn.lmcw.bookspider.model.BookHref;
import cn.lmcw.bookspider.model.Category;
import cn.lmcw.bookspider.model.ChapterContent;
import cn.lmcw.bookspider.utils.StringUtils;
import cn.lmcw.bookspider.web.book.entity.SourceBook;
import cn.lmcw.bookspider.web.book.entity.SourceChapter;
import cn.lmcw.bookspider.web.book.service.IBookService;
import cn.lmcw.bookspider.web.book.service.ICategoryService;
import cn.lmcw.bookspider.web.book.service.IChapterService;
import cn.lmcw.bookspider.web.bookurl.entity.Bookurl;
import cn.lmcw.bookspider.web.bookurl.service.IBookurlService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MysqlStoreImpl implements IStore {

    @Autowired
    IBookurlService iBookurlService;

    @Autowired
    ICategoryService iCategoryService;
    @Autowired
    IBookService iBookService;
    @Autowired
    IChapterService iChapterService;

    @Override
    public Object getBookByHash(String hash) {
        return null;
    }

    @Override
    public void saveBookUrl(BookHref bookHref, Category category, String projectId) {

        if (!checkEntity(bookHref)) {
            return;
        }

        Bookurl bookurl = new Bookurl();
        bookurl.setAuthor(bookHref.getAuthor());
        bookurl.setProjectId(projectId);
        bookurl.setName(bookHref.getName());
        bookurl.setUrl(bookHref.getUrl());
        bookurl.setCategory(category.getTitle());
        bookurl.setStatus(bookHref.getStatus());

        UpdateWrapper<Bookurl> queryWrapper = new UpdateWrapper<Bookurl>()
                .setEntity(bookurl)
                .eq("name", bookurl.getName())
                .eq("author", bookurl.getAuthor())
                .eq("project_id", projectId);
        boolean ret = iBookurlService.saveOrUpdate(bookurl, queryWrapper);
    }

    @Override
    public int getBookUrlCount(String projectId) {

        return iBookurlService.count(new QueryWrapper<Bookurl>().eq("project_id", projectId));
    }

    @Override
    public IPage<Bookurl> getUnfinishedBookUrl(String projectId, int page) {

        return iBookurlService.getUnfinishedBookUrl(projectId, page);
    }

    @Override
    public SourceBook findSourceBook(String name, String author) {
        return iBookService.findBook(name, author);
    }

    @Override
    public boolean saveSourceBook(SourceBook sourceBook) {

        return iBookService.save(sourceBook);
    }

    @Override
    public List<SourceChapter> findSourceChaptersByBookId(int bookId) {

        return iChapterService.findChaptersByBookId(bookId);
    }

    @Override
    public boolean saveChapters(List<SourceChapter> chapters) {
        return iChapterService.saveBatch(chapters);
    }


    private boolean checkEntity(BookHref bookHref) {
        if (StringUtils.isEmpty(bookHref.getAuthor()) ||
                StringUtils.isEmpty(bookHref.getName()) ||
                StringUtils.isEmpty(bookHref.getUrl())) {
            return false;
        }
        return true;
    }

}
