package com.cooldesktop.app.jarprocessmanager.utils;

import java.io.IOException;
import java.nio.file.*;

import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

public class FileWatchModify {
    private String dir;
    private String targetName;

    public FileWatchModify(String dir, String targetName) {
        this.dir = dir;
        this.targetName = targetName;
    }

    /**
     * 创建观察对象
     *
     * @return
     * @throws IOException
     */
    public WatchService createWatchService() throws IOException {
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path path = Paths.get(dir);
        path.register(watchService, ENTRY_MODIFY);
        return watchService;
    }

    /**
     * 等待
     *
     * @param watchService
     * @throws InterruptedException
     */
    public void waitModify(WatchService watchService) throws InterruptedException {
        int tryCount = 0;
        while (tryCount < 3) {
            //3秒没有得到任何响应，则终止
            WatchKey take = watchService.take();
            if (take == null) return;
            for (WatchEvent<?> watchEvent : take.pollEvents()) {
                WatchEvent<Path> event = (WatchEvent<Path>) watchEvent;
                if (targetName.equals(event.context().getFileName().toString())) {
                    return;
                }
            }
            take.reset();
            tryCount++;
        }
    }


}
