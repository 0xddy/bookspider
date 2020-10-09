package cn.lmcw.bookspider.spider;

import cn.lmcw.bookspider.impl.RequestImpl;
import cn.lmcw.bookspider.listener.CrawListener;
import cn.lmcw.bookspider.listener.RequestInterceptor;
import cn.lmcw.bookspider.parse.EngineBridge;

import java.util.ArrayList;
import java.util.List;

public class SpiderService {

    /**
     * 存放定时任务的
     */
    public static List<SpiderService> scheduleRuntime = new ArrayList<>();

    public static boolean taskRunning(String key) {
        boolean running = false;
        for (SpiderService spiderService : scheduleRuntime) {
            if (spiderService.engineBridge.project().getId().equals(key)) {
                running = true;
            }
        }
        return running;
    }

    public static void removeScheduleTask(String key) {
        SpiderService tempSpiderService = null;
        for (SpiderService spiderService : scheduleRuntime) {
            if (spiderService.engineBridge.project().getId().equals(key)) {
                spiderService.runtime.shutdown();
                tempSpiderService = spiderService;
            }
        }
        if (tempSpiderService != null) {
            scheduleRuntime.remove(tempSpiderService);
        }
    }


    // 任务运行环境
    protected TaskRuntime runtime;
    // 规则数据源
    protected EngineBridge engineBridge;
    // 数据回调
    protected CrawListener crawListener;
    // 网络请求拦截器
    protected RequestInterceptor requestInterceptor = new RequestImpl();

    public void setCrawListener(CrawListener crawListener) {
        this.crawListener = crawListener;
    }

    public void setRuntime(TaskRuntime runtime) {
        this.runtime = runtime;
    }

    public void setEngineBridge(EngineBridge engineBridge) {
        this.engineBridge = engineBridge;
    }


}
