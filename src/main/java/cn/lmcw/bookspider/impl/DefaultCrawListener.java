package cn.lmcw.bookspider.impl;

import cn.lmcw.bookspider.listener.CrawListener;
import cn.lmcw.bookspider.model.BookHref;
import cn.lmcw.bookspider.model.Category;

import java.util.List;

public class DefaultCrawListener implements CrawListener {
    @Override
    public boolean onBeforeCrawPage(int currentPage, Category category) {
        return false;
    }

    @Override
    public void onCrawPageListener(int currentPage, List<BookHref> bookHrefs, Category category) {

    }

    @Override
    public void onError(Exception e) {

    }
}
