package com.cooldesktop.app.jarprocessmanager.bean;

import com.sun.tools.attach.AttachNotSupportedException;
import com.sun.tools.attach.VirtualMachine;

import java.io.IOException;
import java.util.Properties;

public class JarProcess {
    private String name;
    private int id;
    private Properties properties;

    public JarProcess(String name, int id) {
        this.name = name;
        this.id = id;
        try {
            VirtualMachine attach = VirtualMachine.attach(id + "");
            properties = attach.getSystemProperties();
            attach.detach();
        } catch (AttachNotSupportedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JarProcess() {
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
