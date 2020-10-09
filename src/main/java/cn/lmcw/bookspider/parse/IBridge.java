package cn.lmcw.bookspider.parse;

import cn.lmcw.bookspider.model.Book;
import cn.lmcw.bookspider.model.BookHref;
import cn.lmcw.bookspider.model.Category;
import cn.lmcw.bookspider.model.Project;

import javax.script.ScriptException;
import java.util.List;

public interface IBridge {

    String getBaseUrl();

    List<Category> getCategory() throws ScriptException, NoSuchMethodException;

    String getPageUrl(int cateId, int currentPage);

    List<BookHref> getBooks(String html) throws ScriptException, NoSuchMethodException;

    Book getBookDetail(String html) throws ScriptException, NoSuchMethodException;

    String getChapterContent(String html);

    Project project();

}
