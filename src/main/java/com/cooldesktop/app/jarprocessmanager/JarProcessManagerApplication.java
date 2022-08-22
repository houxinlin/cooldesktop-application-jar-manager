package com.cooldesktop.app.jarprocessmanager;

import com.cooldesktop.app.jarprocessmanager.bean.JarProcess;
import com.cooldesktop.app.jarprocessmanager.utils.FileUtils;
import com.cooldesktop.app.jarprocessmanager.utils.UrlArgBuilder;
import com.cooldesktop.app.jarprocessmanager.utils.VMUtils;
import com.cooldesktop.rmi.server.CoolDesktopRMIClient;
import com.cooldesktop.rmi.server.CoolDesktopRMIServer;
import com.cooldesktop.rmi.server.IJvmCall;
import com.sun.tools.attach.VirtualMachine;
import com.sun.tools.attach.VirtualMachineDescriptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.rmi.RemoteException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class JarProcessManagerApplication {

    public static void main(String[] args) {


//        for (VirtualMachineDescriptor virtualMachineDescriptor : list) {
//            if (virtualMachineDescriptor.displayName().equals("MainKt")){
//                VMUtils.loadAgent(Integer.parseInt(virtualMachineDescriptor.id()),new UrlArgBuilder()
//                        .set("rmi-port",String.valueOf(49525))
//                        .set("rmi-name",String.valueOf("jId")).toString());
//
//                CoolDesktopRMIClient coolDesktopRMIClient = new CoolDesktopRMIClient();
//                IJvmCall jvmCall = coolDesktopRMIClient.getClient(49525, "jId");
//
//               while (true){
//                   try {
//                       System.out.println(jvmCall.getJvmInfos().getGcCount());
//                       Thread.sleep(1000);
//                   } catch (RemoteException e) {
//                       throw new RuntimeException(e);
//                   } catch (InterruptedException e) {
//                       throw new RuntimeException(e);
//                   }
//               }
//            }
//        }

        SpringApplication.run(JarProcessManagerApplication.class, args);
    }

}
