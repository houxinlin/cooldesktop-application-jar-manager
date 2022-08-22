package com.cooldesktop.app.jarprocessmanager.utils;

import java.util.concurrent.DelayQueue;
import java.util.function.Function;

public class RMIDelayTask implements Runnable {
    private final DelayQueue<RMIExpirxTask> delayQueue = new DelayQueue<>();

    public RMIDelayTask() {
        SystemUtils.startThread(this);
    }

    @Override
    public void run() {
        SystemUtils.doWhile(o -> {
            try {
                RMIExpirxTask take = delayQueue.take();
                take.getData().apply(take);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        });

    }

    private long getExpireTime() {
        return System.currentTimeMillis() + 10000;
    }

    public void addTask(int jId, Function<RMIExpirxTask, Object> function) {
        delayQueue.add(new RMIExpirxTask(getExpireTime(), function,jId));
    }

    public void reset(int jId) {
        for (RMIExpirxTask rmiExpirxTask : delayQueue) {
            if (rmiExpirxTask.getjId() == jId) rmiExpirxTask.setExprixTime(getExpireTime());
        }
    }
    public static class  RMIExpirxTask extends BaseMillisecondDelayedTask<Function<RMIExpirxTask, Object>>{
        private final int jId;

        public RMIExpirxTask(long exprixTime, Function<RMIExpirxTask, Object> data, int  jId) {
            super(exprixTime, data);
            this.jId = jId;
        }
        public int getjId() {
            return jId;
        }
    }

}
