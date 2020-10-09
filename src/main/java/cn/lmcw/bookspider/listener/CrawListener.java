package cn.lmcw.bookspider.listener;

import cn.lmcw.bookspider.model.BookHref;
import cn.lmcw.bookspider.model.Category;

import java.util.List;

public interface CrawListener {

    boolean onBeforeCrawPage(int currentPage, Category category);

    void onCrawPageListener(int currentPage, List<BookHref> bookHrefs, Category category);

    void onError(Exception e);
}
