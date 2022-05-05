package com.cooldesktop.app.jarprocessmanager.controller;

import com.cooldesktop.app.jarprocessmanager.bean.JarProcess;
import com.cooldesktop.app.jarprocessmanager.utils.FileUtils;
import com.cooldesktop.app.jarprocessmanager.utils.FileWatchModify;
import com.cooldesktop.app.jarprocessmanager.utils.UrlArgBuilder;
import com.cooldesktop.app.jarprocessmanager.utils.VMUtils;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import com.sun.tools.classfile.ClassFile;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchService;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ApiController {
    private static final String ARG_CLASS_NAME = "className";
    private static final String ARG_CLASS_FILE_PATH = "class";
    private static final String ARG_NOTIFY_FILE_PATH = "callback";

    @GetMapping("list")
    public List<JarProcess> list() {
        List<VirtualMachineDescriptor> list = VirtualMachine.list();
        return list.stream()
                .map((e) -> new JarProcess(e.displayName(), Integer.valueOf(e.id())))
                .collect(Collectors.toList());
    }

    @PostMapping("stop")
    public String stop(@RequestParam("id") int id) {
        try {
            Process kill = Runtime.getRuntime().exec("kill -9 " + id);
            kill.waitFor();
            return "终止成功";
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "终止失败";
    }


    @PostMapping("upload")
    public String upload(@RequestParam("jid") int jId, @RequestParam MultipartFile file) {
        if (!processExist(jId)) return "加载代理失败，可能是此进程不存在";
        //回调文件，最后需要删除
        String callbackFile = null;
        Path localClassPath = null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            IOUtils.copy(file.getInputStream(), outputStream);
            byte[] classBytes = outputStream.toByteArray();

            //读取Class信息并且保存到本地
            ClassFile classFile = ClassFile.read(new ByteArrayInputStream(classBytes));
            String className = classFile.getName().replace("/", ".");
            localClassPath = Paths.get(FileUtils.getWorkPath(), className + ".class");
            Files.write(localClassPath, classBytes);

            callbackFile = FileUtils.createTempFile(jId, className);
            String urlArgs = new UrlArgBuilder()
                    .set(ARG_CLASS_NAME, className)
                    .set(ARG_NOTIFY_FILE_PATH, callbackFile)
                    .set(ARG_CLASS_FILE_PATH, localClassPath.toString()).toString();
            FileWatchModify fileWatchModify = new FileWatchModify(localClassPath.getParent().toString(), new File(callbackFile).getName());

            WatchService watchService = fileWatchModify.createWatchService();
            //这句执行玩可能回调文件已经发生改变，所以实现先创建一个观察对象
            VMUtils.loadAgent(jId, urlArgs);
            fileWatchModify.waitModify(watchService);
            String result = new String(Files.readAllBytes(Paths.get(callbackFile)));
            if (result.length() == 0) result = "未检测到替换状态，请手动检查";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (callbackFile != null && Files.exists(Paths.get(callbackFile))) {
                    Files.delete(Paths.get(callbackFile));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "替换失败";
    }

    private boolean processExist(int jid) {
        List<JarProcess> list = list();
        for (JarProcess jarProcess : list) {
            if (jarProcess.getId() == jid) return true;
        }
        return false;
    }

}
