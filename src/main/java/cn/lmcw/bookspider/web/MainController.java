package cn.lmcw.bookspider.web;

import cn.lmcw.bookspider.impl.DefaultCrawListener;
import cn.lmcw.bookspider.model.BookHref;
import cn.lmcw.bookspider.model.Category;
import cn.lmcw.bookspider.model.Project;
import cn.lmcw.bookspider.parse.EngineBridge;
import cn.lmcw.bookspider.spider.CategorySpiderService;
import cn.lmcw.bookspider.spider.SpiderService;
import cn.lmcw.bookspider.spider.TaskRuntime;
import cn.lmcw.bookspider.store.MysqlStoreImpl;
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
import java.util.function.Consumer;

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
                        project.setRun(SpiderService.taskRunning(project.getId()));
                        projects.add(project);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
                model.addAttribute("store", mysqlStore);
                model.addAttribute("projects", projects);
            }

        }
        return "main";
    }

    @ResponseBody
    @GetMapping("/api/crawStop")
    public Object stopCraw(String js) {
        try {
            EngineBridge engineBridge = new EngineBridge(System.getProperty("user.dir") + "/site/" + js);
            SpiderService.removeScheduleTask(engineBridge.project().getId());
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
            if (SpiderService.taskRunning(engineBridge.project().getId())) {
                return "{\"status\":400}";
            }
            TaskRuntime.Conf conf = new TaskRuntime.Conf();
            conf.type = 3;
            conf.period = 20000;
            conf.threads = 15;
            TaskRuntime taskRuntime = new TaskRuntime(conf);
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

}
