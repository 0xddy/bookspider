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
    public static List<SpiderService> spiderServices = new ArrayList<>();

    public static boolean taskRunning(String projectId, String tag) {
        boolean running = false;
        List<SpiderService> removeStopObject = new ArrayList<>();
        for (SpiderService spiderService : spiderServices) {
            if (spiderService.engineBridge.project().getId().equals(projectId)
                    && spiderService.runtime.getTag().equals(tag)) {
                running = true;
            }
            if (spiderService.runtime.getExecutorService().isTerminated()) {
                removeStopObject.add(spiderService);
            }
        }
        spiderServices.removeAll(removeStopObject);
        return running;
    }

    public static void removeScheduleTask(String projectId, String tag) {
//        SpiderService tempSpiderService = null;
//        for (SpiderService spiderService : spiderServices) {
//            if (projectId.equals(spiderService.engineBridge.project().getId())
//                    && spiderService.runtime.getTag().equals(tag)) {
//                spiderService.runtime.shutdown();
//                tempSpiderService = spiderService;
//            }
//        }
//        if (tempSpiderService != null) {
//            spiderServices.remove(tempSpiderService);
//        }

        for (SpiderService spiderService : spiderServices) {
            if (projectId.equals(spiderService.engineBridge.project().getId())
                    && spiderService.runtime.getTag().equals(tag)) {
                spiderService.stop();
                break;
            }
        }

    }


    // 任务运行环境
    protected TaskRuntime runtime;
    // 规则数据源
    protected EngineBridge engineBridge;
    // 数据回调
    protected CrawListener crawListener;
    // 网络请求拦截器
    protected RequestInterceptor request = new RequestImpl();

    public void setCrawListener(CrawListener crawListener) {
        this.crawListener = crawListener;
    }

    public void setRuntime(TaskRuntime runtime) {
        this.runtime = runtime;
    }

    public void setEngineBridge(EngineBridge engineBridge) {
        this.engineBridge = engineBridge;
    }

    public void stop(){
        runtime.shutdown();
        spiderServices.remove(this);
    }

}
