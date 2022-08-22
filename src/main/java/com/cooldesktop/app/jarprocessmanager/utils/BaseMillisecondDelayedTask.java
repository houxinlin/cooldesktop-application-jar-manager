package com.cooldesktop.app.jarprocessmanager.utils;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class BaseMillisecondDelayedTask<T> implements Delayed {
    private  long exprixTime;
    private final T data;

    public BaseMillisecondDelayedTask(long exprixTime, T data) {
        this.exprixTime = exprixTime;
        this.data = data;
    }


    @Override
    public long getDelay(TimeUnit unit) {
        return this.exprixTime-System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        return 0;
    }
    public long getExprixTime() {
        return exprixTime;
    }

    public void setExprixTime(long exprixTime) {
        this.exprixTime = exprixTime;
    }

    public T getData() {
        return data;
    }
}
