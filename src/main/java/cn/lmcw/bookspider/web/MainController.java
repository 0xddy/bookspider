package cn.lmcw.bookspider.web;

import cn.lmcw.bookspider.impl.DefaultCrawListener;
import cn.lmcw.bookspider.model.BookHref;
import cn.lmcw.bookspider.model.Category;
import cn.lmcw.bookspider.model.Project;
import cn.lmcw.bookspider.parse.EngineBridge;
import cn.lmcw.bookspider.spider.BooksSpiderService;
import cn.lmcw.bookspider.spider.CategorySpiderService;
import cn.lmcw.bookspider.spider.SpiderService;
import cn.lmcw.bookspider.spider.TaskRuntime;
import cn.lmcw.bookspider.store.MysqlStoreImpl;
import cn.lmcw.bookspider.web.bookurl.service.IBookurlService;
import cn.lmcw.bookspider.web.model.Alert;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    private MysqlStoreImpl mysqlStore;


    @RequestMapping("/")
    public String main(Model model) {

        File siteDir = new File(System.getProperty("user.dir"), "site");
        if (!siteDir.exists()) {
            model.addAttribute("alert", new Alert("warning", "站点规则目录不存在"));
        } else {
            String[] jss = siteDir.list((dir, name) -> !dir.isFile());
            if (jss == null || jss.length == 0) {
                model.addAttribute("alert", new Alert("warning", "请先添加采集规则"));
            } else {
                List<Project> projects = new ArrayList<>();

                Arrays.asList(jss).forEach(fileName -> {
                    File siteJs = new File(siteDir, fileName);
                    try {
                        EngineBridge engineBridge = new EngineBridge(siteJs.getPath());
                        Project project = engineBridge.project();
                        projects.add(project);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // SpiderService.taskRunning(project.getId())
                });
                model.addAttribute("store", mysqlStore);
                model.addAttribute("projects", projects);
            }

        }
        return "main";
    }

    @ResponseBody
    @GetMapping("/api/crawPagesStop")
    public Object stopCrawPages(String js) {
        try {
            EngineBridge engineBridge = new EngineBridge(System.getProperty("user.dir") + "/site/" + js);
            SpiderService.removeScheduleTask(engineBridge.project().getId(), "page");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "{\"status\":200}";
    }

    @ResponseBody
    @GetMapping("/api/crawNovelsStop")
    public Object stopCrawNovels(String js) {
        try {
            EngineBridge engineBridge = new EngineBridge(System.getProperty("user.dir") + "/site/" + js);
            SpiderService.removeScheduleTask(engineBridge.project().getId(), "novels");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "{\"status\":200}";
    }

    @ResponseBody
    @GetMapping("/api/crawPages")
    public Object crawPages(String js) {
        try {
            EngineBridge engineBridge = new EngineBridge(System.getProperty("user.dir") + "/site/" + js);
            if (SpiderService.taskRunning(engineBridge.project().getId(), "page")) {
                return "{\"status\":400}";
            }
            TaskRuntime.Conf conf = new TaskRuntime.Conf();
            conf.type = 3;
            conf.period = 86400;
            conf.threads = 15;
            TaskRuntime taskRuntime = new TaskRuntime(conf);
            taskRuntime.setTag("page");
            CategorySpiderService spiderService = new CategorySpiderService();
            spiderService.setEngineBridge(engineBridge);
            spiderService.setRuntime(taskRuntime);
            spiderService.setCrawListener(new DefaultCrawListener() {
                @Override
                public void onCrawPageListener(int currentPage, List<BookHref> bookHrefs, Category category) {
                    System.out.println("loading " + category.getTitle() + " Page：" + currentPage + " size：" + new Gson().toJson(bookHrefs));
                    String projectId = engineBridge.project().getId();
                    bookHrefs.forEach(bookHref -> {
                        mysqlStore.saveBookUrl(bookHref, category, projectId);
                    });
                }

                @Override
                public void onError(Exception e) {

                }
            });
            spiderService.crawPages();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "{\"status\":200}";
    }


    @ResponseBody
    @GetMapping("/api/crawNovels")
    public Object crawNovels(String js) {

        try {
            EngineBridge engineBridge = new EngineBridge(System.getProperty("user.dir") + "/site/" + js);
            //EngineBridge engineBridge = new EngineBridge(System.getProperty("user.dir") + "/site/全本小说.js");
            if (SpiderService.taskRunning(engineBridge.project().getId(), "novels")) {
                return "{\"status\":400}";
            }
            TaskRuntime.Conf conf = new TaskRuntime.Conf();
            conf.type = 3;
            conf.period = 86400;
            conf.threads = 40;
            TaskRuntime taskRuntime = new TaskRuntime(conf);
            taskRuntime.setTag("novels");
            BooksSpiderService spiderService = new BooksSpiderService();
            spiderService.setEngineBridge(engineBridge);
            spiderService.setRuntime(taskRuntime);
            spiderService.setStore(mysqlStore);
            spiderService.crawNovels();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return "{\"status\":200}";
    }


}
