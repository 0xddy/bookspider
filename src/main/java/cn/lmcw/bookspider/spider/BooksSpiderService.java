package cn.lmcw.bookspider.spider;

import cn.lmcw.bookspider.model.Book;
import cn.lmcw.bookspider.model.ChapterContent;
import cn.lmcw.bookspider.model.Project;
import cn.lmcw.bookspider.store.IStore;
import cn.lmcw.bookspider.utils.StringUtils;
import cn.lmcw.bookspider.web.book.entity.SourceBook;
import cn.lmcw.bookspider.web.book.entity.SourceChapter;
import cn.lmcw.bookspider.web.bookurl.entity.Bookurl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.io.FileUtils;
import org.jsoup.Connection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class BooksSpiderService extends SpiderService {

    private static File thumbDir;
    private static File chapterDir;

    static {
        thumbDir = new File(System.getProperty("user.dir"), "thumb");
        chapterDir = new File(System.getProperty("user.dir"), "txt");
        if (!thumbDir.exists()) {
            thumbDir.mkdirs();
        }
        if (!chapterDir.exists()) {
            chapterDir.mkdirs();
        }
    }

    private ExecutorService downloadService = Executors.newFixedThreadPool(30);
    private IStore iStore;
    private volatile boolean isShutdown;

    public void setStore(IStore iStore) {
        this.iStore = iStore;
    }

    /**
     * 采集小说
     * 理论上从数据库里面读取url采集
     */
    public void crawNovels() {

        if (engineBridge == null) {
            throw new RuntimeException("engineBridge is null");
        }
        if (runtime == null) {
            throw new RuntimeException("runtime is null");
        }


        new Thread(() -> {

            Project project = engineBridge.project();

            int currentPage = 1;
            Page<Bookurl> bookurlPage;
            String bodyCharset = engineBridge.getCharset();

            if (runtime.getExecutorService() instanceof ScheduledExecutorService) {
                SpiderService.spiderServices.add(this);
            }

            do {
                if (isShutdown) {
                    break;
                }
                // 查询连载和未知状态的小说查询
                bookurlPage = (Page<Bookurl>) iStore.getUnfinishedBookUrl(project.getId(), currentPage);
                //System.out.println("进度：" + currentPage + "  " + bookurlPage.get);
                if (bookurlPage.getRecords() != null) {

                    for (Bookurl bookurl : bookurlPage.getRecords()) {
                        // 采集小说信息和章节
                        runtime.commit(() -> {
                            try {
                                String body = GET(bookurl.getUrl(), bodyCharset);
                                Book book = engineBridge.getBookDetail(body);
                                // 判断是否已存在小说

                                String thumbFileName = StringUtils.stringToMD5(book.getName() + book.getAuthor()) + ".jpg";

                                SourceBook sourceBook = iStore.findSourceBook(book.getName(), book.getAuthor());
                                if (sourceBook == null) {
                                    // 写入小说
                                    sourceBook = new SourceBook();
                                    sourceBook.setName(book.getName());
                                    sourceBook.setAuthor(book.getAuthor());
                                    // 获取要发布到的分类
                                    sourceBook.setCategory_id(engineBridge.getCategoryMappingId(bookurl.getCategory()));
                                    sourceBook.setStatus(bookurl.getStatus());
                                    if (book.getLastime() > 0) {
                                        sourceBook.setLastime(book.getLastime());
                                    }
                                    sourceBook.setThumb(thumbFileName);
                                    //保存小说
                                    if (!iStore.saveSourceBook(sourceBook)) {
                                        System.out.println("【跳过】写入小说失败 " + bookurl.getName());
                                        return;
                                    }

                                }

                                // 下载封面 并检查本地封面是否完整
                                File outThumbFile = new File(thumbDir, thumbFileName);
                                if (!outThumbFile.exists() || (outThumbFile.exists() &&
                                        outThumbFile.length() < request.getLength(book.getImg()))) {
                                    downloadService.submit(() -> {
                                        try {
                                            request.download(book.getImg(), outThumbFile);
                                            System.out.println("下载封面完成 " + book.getImg() + " To " + thumbFileName);
                                        } catch (Exception e) {
                                            System.out.println("下载封面失败 " + book.getImg() + " To " + thumbFileName);
                                        }
                                    });
                                }


                                // 数据库已保存得章节
                                List<SourceChapter> sourceChapters = iStore.findSourceChaptersByBookId(sourceBook.getId());

                                HashSet<String> hashSet = new HashSet<>();
                                sourceChapters.forEach(chapterContent -> hashSet.add(chapterContent.getName()));
                                // 采集章节
                                List<Book.Chapter> chapters = book.getChapters();
                                chapters.removeIf(chapter -> hashSet.contains(chapter.getTitle()));
                                // 查找章节对比 去重

                                File novelDir = new File(chapterDir, String.valueOf(sourceBook.getId()));
                                if (!novelDir.exists()) novelDir.mkdirs();

                                List<SourceChapter> newInsertChapters = new ArrayList<>();
                                SourceChapter newTempSourceChapter;

                                String chapterHtml;
                                ChapterContent chapterContent;
                                File chapterTxtFile;
                                String chapterFileName;
                                int i = 0;
                                boolean isBreak = false;
                                for (Book.Chapter chapter : chapters) {
                                    i++;
                                    try {
                                        chapterHtml = GET(chapter.getUrl(), bodyCharset);
                                        chapterContent = engineBridge.getChapterContent(chapterHtml);
                                        // 保存章节至硬盘
                                        chapterFileName = i + "_" + chapterContent.getHash() + ".txt";
                                        chapterTxtFile = new File(novelDir, chapterFileName);
                                        if (chapterTxtFile.exists() && chapterTxtFile.length() > 0) {
                                            continue;
                                        }
                                        try {
                                            FileUtils.writeStringToFile(chapterTxtFile, chapterContent.getContent(), bodyCharset);

                                            newTempSourceChapter = new SourceChapter();
                                            newTempSourceChapter.setBookId(sourceBook.getId());
                                            newTempSourceChapter.setName(chapterContent.getTitle());
                                            newTempSourceChapter.setPath(book.getHash() + "/" + chapterFileName);

                                            newInsertChapters.add(newTempSourceChapter);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            newInsertChapters.clear();
                                            isBreak = true;
                                            break;
                                        }

                                    } catch (Exception e) {
                                        //e.printStackTrace();
                                        newInsertChapters.clear();
                                        isBreak = true;
                                        break;
                                    }
                                }
                                if (newInsertChapters.size() > 0) {
                                    iStore.saveChapters(newInsertChapters);
                                    System.out.println("【" + book.getName() + "】 已存储" + sourceChapters.size() +
                                            " 采集到新章节：" + newInsertChapters.size() + " " + bookurl.getUrl());
                                } else {
                                    System.out.println("【" + book.getName() + "】 已存储" + sourceChapters.size() +
                                            " 没有采集到新章节：" + bookurl.getUrl());
                                }
                                if (isBreak) {
                                    System.out.println("采集章节失败 已跳过该书 " + book.getName() + "");
                                }


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                    }
                }
                currentPage++;
            } while (bookurlPage.hasNext() && !isShutdown);

            if (!(runtime.getExecutorService() instanceof ScheduledExecutorService)) {
                // 定时任务只能手动关闭
                runtime.shutdown();
                // 非定时周期任务，执行完已添加的任务后自动停止
            }

            System.out.println("已关闭");

        }).start();

    }

    public void stop() {

        try {
            downloadService.shutdownNow();
            runtime.shutdown();
            isShutdown = true;
            spiderServices.remove(this);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String GET(String url, String charset) {
        String body = null;
        int tryNum = 0;
        do {
            if (tryNum > 5) {
                break;
            }
            try {
                Connection.Response response = request
                        .getConnection(url).execute();
                if (!StringUtils.isEmpty(charset)) {
                    response.charset(charset);
                }
                body = response.body();
                if (body != null)
                    tryNum = 0;
            } catch (Exception e) {
                tryNum++;
            }

        } while (body == null);

        if (body == null) {
            throw new RuntimeException("request get body err!");
        }
        return body;
    }


}
