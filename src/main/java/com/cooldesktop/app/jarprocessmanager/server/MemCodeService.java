package com.cooldesktop.app.jarprocessmanager.server;

import com.cooldesktop.app.jarprocessmanager.utils.BaseMillisecondDelayedTask;
import com.cooldesktop.app.jarprocessmanager.utils.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.DelayQueue;

@Component
public class MemCodeService implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MemCodeService.class);
    private final DelayQueue<BaseMillisecondDelayedTask<String>> delayQueue = new DelayQueue<>();
    private final Map<String, String> codeMap = new HashMap<>();
    private static final String NULL_CODE = "无法获取代码";
    private static final int EXPIRE_TIME = 5 * 1000;

    public MemCodeService() {
        //启动一个线程用来删除过期的code
        SystemUtils.startThread(this);
    }

    @Override
    public void run() {
        SystemUtils.doWhile(o -> {
            try {
                BaseMillisecondDelayedTask<String> removeCodeTask = delayQueue.take();
                logger.info("删除code id={}", removeCodeTask.getData());
                return codeMap.remove(removeCodeTask.getData());
            } catch (InterruptedException e) {
                logger.error(e.getMessage());
            }
            return null;
        });

    }

    public void setCode(String id, String code) {
        codeMap.put(id, code);
        delayQueue.put(new BaseMillisecondDelayedTask<>(System.currentTimeMillis() + EXPIRE_TIME, id));
    }

    public String getCode(String id) {
        String code = codeMap.getOrDefault(id, NULL_CODE);
        codeMap.remove(id);
        return code;
    }
}
