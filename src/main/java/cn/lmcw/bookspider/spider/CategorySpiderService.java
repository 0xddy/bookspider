package cn.lmcw.bookspider.spider;

import cn.lmcw.bookspider.model.BookHref;
import cn.lmcw.bookspider.model.Category;
import org.jsoup.Connection;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

public class CategorySpiderService extends SpiderService {

    /**
     * 采集分类列表的小说url至数据库
     */
    public TaskRuntime crawPages() {
        // 按分类顺序一页一页采集
        try {
            List<Category> categories = engineBridge.getCategory();
            for (Category category : categories) {
                Category.PageDTO pageDTO = category.getPage();
                for (int page = pageDTO.getStart(); page < pageDTO.getEnd(); page++) {
                    int currentPage = page;
                    if (crawListener.onBeforeCrawPage(currentPage, category)) {
                        continue;
                    }
                    runtime.commit(() -> {
                        try {
                            String pageUrl = engineBridge.getPageUrl(category.getId(), currentPage);
                            String body = null;
                            int tryNum = 0;
                            do {
                                if (tryNum > 3) {
                                    break;
                                }
                                try {
                                    Connection.Response response = requestInterceptor.getConnection(pageUrl).execute();
                                    body = response.body();
                                    if (body != null)
                                        tryNum = 0;
                                } catch (IOException e) {
                                    tryNum++;
                                }

                            } while (body == null);

                            if (body == null) {
                                throw new RuntimeException("request get body err!");
                            }

                            List<BookHref> bookHrefs = engineBridge.getBooks(body);
                            if (crawListener != null) {
                                crawListener.onCrawPageListener(currentPage, bookHrefs, category);
                            }
                        } catch (Exception e) {
                            if (crawListener != null) {
                                crawListener.onError(e);
                            }
                        }
                    });
                }
            }

        } catch (Exception e) {
            if (crawListener != null) {
                crawListener.onError(e);
            }
        }

        if (!(runtime.getExecutorService() instanceof ScheduledExecutorService)) {
            // 定时任务只能手动关闭
            runtime.shutdown();
        } else {
            SpiderService.scheduleRuntime.add(this);
        }

        return runtime;
    }
}
