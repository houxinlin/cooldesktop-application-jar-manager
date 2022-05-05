package com.cooldesktop.app.jarprocessmanager.utils;

import com.sun.tools.attach.VirtualMachine;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class VMUtils {
    private static void unAgent() throws IOException {
        InputStream resourceAsStream = VMUtils.class.getResourceAsStream("/static/hotswap-agent.jar");
        if (resourceAsStream != null) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            IOUtils.copy(resourceAsStream, outputStream);
            Files.write(Paths.get(getHotswapAgentPath()), outputStream.toByteArray());
        }
    }

    private static String getHotswapAgentPath() {
        String workPath = FileUtils.getWorkPath();
        return Paths.get(workPath, "hotswap-agent.jar").toString();
    }

    public static boolean loadAgent(int jId, String urlArgs) {
        try {
            if (!Files.exists(Paths.get(getHotswapAgentPath()))) unAgent();

            VirtualMachine attach = VirtualMachine.attach(String.valueOf(jId));
            attach.loadAgent(getHotswapAgentPath(), urlArgs);
            return true;
        } catch (Exception e) {

        }
        return false;
    }
}
