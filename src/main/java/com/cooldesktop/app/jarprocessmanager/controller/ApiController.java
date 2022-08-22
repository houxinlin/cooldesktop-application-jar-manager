package com.cooldesktop.app.jarprocessmanager.controller;

import com.cooldesktop.app.jarprocessmanager.bean.JarProcess;
import com.cooldesktop.app.jarprocessmanager.server.MemCodeService;
import com.cooldesktop.app.jarprocessmanager.utils.RMIDelayTask;
import com.cooldesktop.app.jarprocessmanager.utils.SystemUtils;
import com.cooldesktop.app.jarprocessmanager.utils.UrlArgBuilder;
import com.cooldesktop.app.jarprocessmanager.utils.VMUtils;
import com.cooldesktop.rmi.info.ClassInfo;
import com.cooldesktop.rmi.info.CoolThreadInfo;
import com.cooldesktop.rmi.info.JvmInfos;
import com.cooldesktop.rmi.server.CoolDesktopRMIClient;
import com.cooldesktop.rmi.server.IJvmCall;
import org.springframework.http.ResponseEntity;
import org.springframework.util.SocketUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.rmi.RemoteException;
import java.util.*;
import java.util.function.Function;

@RestController
public class ApiController {
    private static final int MIN_PORT = 4956;
    private final Map<Integer, IJvmCall> jvmCallHashMap = new HashMap<>();
    private final RMIDelayTask rmiDelayTask = new RMIDelayTask();
    private final MemCodeService codeService;

    public ApiController(MemCodeService codeService) {
        this.codeService = codeService;
    }

    /**
     * @description: 列举JVM进程
     * @date: 2022/8/21 下午4:49
     */
    @GetMapping("list")
    public ResponseEntity<List<JarProcess>> list() {
        return ResponseEntity.ok(SystemUtils.listProcess());
    }

    /**
     * @description: 停止JVM进程
     * @date: 2022/8/21 下午4:50
     */

    @PostMapping("stop")
    public ResponseEntity<String> stop(@RequestParam("id") int id) {
        return ResponseEntity.ok(SystemUtils.killProcess(id));
    }

    /**
     * @description: 热替换class
     * @date: 2022/8/21 上午8:54
     */

    @PostMapping("upload")
    public String upload(@RequestParam("jid") int jId, @RequestParam MultipartFile file) {
//        if (!processExist(jId)) return "加载代理失败，可能是此进程不存在";
//        //回调文件，最后需要删除
//        String callbackFile = null;
//        Path localClassPath = null;
//        WatchService watchService = null;
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            IOUtils.copy(file.getInputStream(), outputStream);
//            byte[] classBytes = outputStream.toByteArray();
//
//            //读取Class信息并且保存到本地
//            ClassFile classFile = ClassFile.read(new ByteArrayInputStream(classBytes));
//
//            //这句执行玩可能回调文件已经发生改变，所以实现先创建一个观察对象
//            VMUtils.loadAgent(jId, urlArgs);

        return "";
    }

    @GetMapping("infos")
    private ResponseEntity<JvmInfos> getInfoById(@RequestParam("jid") int jId) {
        //如果没有加载agent
        loadAgent(jId);
        if (jvmCallHashMap.containsKey(jId)) {
            try {
                rmiDelayTask.reset(jId);
                return ResponseEntity.ok(jvmCallHashMap.get(jId).getJvmInfos());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("threadInfos")
    private ResponseEntity<CoolThreadInfo> getThreadInfos(@RequestParam("jid") int jId, @RequestParam("threadId") long threadId) {
        try {
            return ResponseEntity.ok(jvmCallHashMap.get(jId).getThreadInfo(threadId));
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("getLoadedClass")
    private ResponseEntity<Map<String, List<ClassInfo>>> getLoadedClass(@RequestParam("jid") int jId) throws RemoteException {
        loadAgent(jId);
        return ResponseEntity.ok(jvmCallHashMap.get(jId).getAllLoadedClass());
    }


    /**
     * @description: 类自动建议
     * @date: 2022/8/22 上午11:08
     */

    @GetMapping("suggestClassName")
    private ResponseEntity< List<String>> classNameSuggest(@RequestParam("jid") int jId, @RequestParam("str") String str) throws RemoteException {
        loadAgent(jId);
        if (!StringUtils.hasText(str)) return ResponseEntity.ok(Collections.emptyList());
        List<String> result = new ArrayList<>();
        Map<String, List<ClassInfo>> allLoadedClass = jvmCallHashMap.get(jId).getAllLoadedClass();
        allLoadedClass.forEach((key, classInfos) -> {
            for (ClassInfo classInfo : classInfos) {
                if (classInfo.getSimpName().startsWith(str)) result.add(classInfo.getClassName());
            }
        });
        if (result.size()>10)  return ResponseEntity.ok(result.subList(0,10));
        return ResponseEntity.ok(result);
    }

    @GetMapping("dumpClass")
    private ResponseEntity<String> dumpClass(@RequestParam("jid") int jId, @RequestParam("className") String className) throws RemoteException {
        loadAgent(jId);
        String uuid = UUID.randomUUID().toString();
        codeService.setCode(uuid, jvmCallHashMap.get(jId).dumpClass("", className));
        return ResponseEntity.ok(uuid);
    }

    private void loadAgent(int jId) {
        final String bindName = "cooldesktop" + jId;
        final int bindPort = SocketUtils.findAvailableTcpPort(MIN_PORT);
        if (jvmCallHashMap.getOrDefault(jId, null) == null) {
            VMUtils.loadAgent(jId, new UrlArgBuilder()
                    .set("rmi-port", String.valueOf(bindPort))
                    .set("rmi-name", bindName));

            jvmCallHashMap.put(jId, new CoolDesktopRMIClient().getClient(bindPort, bindName));
            //关闭rmi
            rmiDelayTask.addTask(jId, rmiExpirxTask -> {
                VMUtils.loadAgent(jId, new UrlArgBuilder().set("action", "close-rmi"));
                return jvmCallHashMap.remove(jId);
            });
        }
    }
}
