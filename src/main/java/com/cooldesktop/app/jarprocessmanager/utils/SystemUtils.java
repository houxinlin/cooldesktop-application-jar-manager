package com.cooldesktop.app.jarprocessmanager.utils;

import com.cooldesktop.app.jarprocessmanager.bean.JarProcess;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SystemUtils {
    public static void startThread(Runnable runnable) {
        createThread(runnable).start();
    }

    public static void doWhile(Function<Object, Object> function) {
        for (; ; ) function.apply(null);
    }

    private static Thread createThread(Runnable runnable) {
        return new Thread(runnable);
    }

    public static List<JarProcess> listProcess() {
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        return list.stream()
                .map((e) -> new JarProcess(e.displayName(), Integer.parseInt(e.id())))
                .collect(Collectors.toList());
    }

    public static String killProcess(int id) {
        try {
            if (!hasProcess(id)) return "进程不存在";
            Process kill = Runtime.getRuntime().exec("kill -9 " + id);
            kill.waitFor(4, TimeUnit.SECONDS);
            return "终止成功";
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "终止失败";
    }

    public static boolean hasProcess(int jid) {
        List<JarProcess> list = listProcess();
        for (JarProcess jarProcess : list) {
            if (jarProcess.getId() == jid) return true;
        }
        return false;
    }
}
