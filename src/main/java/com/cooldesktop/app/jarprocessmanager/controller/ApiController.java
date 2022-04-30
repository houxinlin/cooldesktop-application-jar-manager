package com.cooldesktop.app.jarprocessmanager.controller;

import com.cooldesktop.app.jarprocessmanager.bean.JarProcess;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class ApiController {
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
}
