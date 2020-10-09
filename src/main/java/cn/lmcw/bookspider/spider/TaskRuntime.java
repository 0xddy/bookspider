package cn.lmcw.bookspider.spider;

import java.util.concurrent.*;

/**
 * 任务运行池
 * 一个运行池对应一个任务
 */
public class TaskRuntime {

    private ExecutorService executorService;
    private Conf conf;

    public TaskRuntime(Conf conf) {
        if (conf.type == 1) {
            executorService = Executors.newSingleThreadExecutor();
        } else if (conf.type == 2) {
            executorService = Executors.newFixedThreadPool(conf.threads);
        } else if (conf.type == 3) {
            executorService = Executors.newScheduledThreadPool(conf.threads);
        } else if (conf.type == 4) {
            executorService = Executors.newCachedThreadPool();
        }
        this.conf = conf;
    }


    /**
     * 立刻停止
     */
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }


    /**
     *
     *
     * @param runnable
     * @return
     */
    public Future<?> commit(Runnable runnable) {
        if (executorService != null && executorService instanceof ScheduledExecutorService) {
            return ((ScheduledExecutorService) executorService).scheduleAtFixedRate(runnable, 1, conf.period, TimeUnit.SECONDS);
        } else {
            return executorService.submit(runnable);
        }
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }


    public static class Conf {
        //1 单线程顺序执行，2 定长并发，3 定期/延时，4 灵活弹性复用
        public int type;
        public int threads = Runtime.getRuntime().availableProcessors() + 1;
        public long period = 60;
    }

}
