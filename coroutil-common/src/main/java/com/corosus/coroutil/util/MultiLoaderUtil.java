package com.corosus.coroutil.util;

import com.corosus.modconfig.IConfigCategory;
import com.corosus.modconfig.ModConfigData;

public class MultiLoaderUtil {

    // thread safe eager initialization
    private static final MultiLoaderUtil instance = new MultiLoaderUtil();

    private boolean checkPaper = true;
    private boolean isPaper = false;

    private boolean checkForge = true;
    private boolean isForge = false;

    private boolean checkNeoForge = true;
    private boolean isNeoForge = false;

    private boolean checkFabric = true;
    private boolean isFabric = false;

    private MultiLoaderUtil() {

    }

    public static synchronized MultiLoaderUtil instance() {
        return instance;
    }

    public synchronized boolean isPaper() {
        if (checkPaper) {
            try {
                checkPaper = false;
                isPaper = Class.forName("com.corosus.coroutil.loader.paper.ConfigModPaper") != null
                       || Class.forName("org.bukkit.plugin.java.JavaPlugin") != null;
                if (isPaper) {
                    CULog.log("paper / purpur server environment detected");
                }
            } catch (ClassNotFoundException ex) {
                // not paper
            }
        }
        return isPaper;
    }

    public synchronized boolean isForge() {
        if (checkForge) {
            try {
                checkForge = false;
                isForge = Class.forName("com.corosus.coroutil.loader.forge.ConfigModForge") != null;
                if (isForge) {
                    CULog.log("forge loader environment detected");
                }
            } catch (ClassNotFoundException ex) {
                // loader not detected
            }
        }
        return isForge;
    }

    public synchronized boolean isNeoForge() {
        if (checkNeoForge) {
            try {
                checkNeoForge = false;
                isNeoForge = Class.forName("com.corosus.coroutil.loader.neoforge.ConfigModNeoForge") != null;
                if (isNeoForge) {
                    CULog.log("neo forge loader environment detected");
                }
            } catch (ClassNotFoundException ex) {
                // loader not detected
            }
        }
        return isNeoForge;
    }

    public synchronized boolean isFabric() {
        if (checkFabric) {
            try {
                checkFabric = false;
                isFabric = Class.forName("com.corosus.coroutil.loader.fabric.ConfigModFabric") != null;
                if (isFabric) {
                    CULog.log("fabric loader environment detected");
                }
            } catch (ClassNotFoundException ex) {
                // loader not detected
            }
        }
        return isFabric;
    }

    public synchronized ModConfigData makeLoaderSpecificConfigData(String savePath, String parStr, Class parClass, IConfigCategory parConfig) {
        if (isPaper()) {
            return constructLoaderSpecificConfigData("com.corosus.coroutil.loader.paper.ModConfigDataPaper", savePath, parStr, parClass, parConfig);
        } else if (isFabric()) {
            return constructLoaderSpecificConfigData("com.corosus.coroutil.loader.fabric.ModConfigDataFabric", savePath, parStr, parClass, parConfig);
        } else if (isForge()) {
            return constructLoaderSpecificConfigData("com.corosus.coroutil.loader.forge.ModConfigDataForge", savePath, parStr, parClass, parConfig);
        } else if (isNeoForge()) {
            return constructLoaderSpecificConfigData("com.corosus.coroutil.loader.neoforge.ModConfigDataNeoForge", savePath, parStr, parClass, parConfig);
        }
        // Fallback for standalone/common testing
        return constructLoaderSpecificConfigData("com.corosus.coroutil.loader.paper.ModConfigDataPaper", savePath, parStr, parClass, parConfig);
    }

    private ModConfigData constructLoaderSpecificConfigData(String clazz, String savePath, String parStr, Class parClass, IConfigCategory parConfig) {
        try {
            Class classToLoad = Class.forName(clazz);

            Class[] cArg = new Class[4];
            cArg[0] = String.class;
            cArg[1] = String.class;
            cArg[2] = Class.class;
            cArg[3] = IConfigCategory.class;

            return (ModConfigData) classToLoad.getDeclaredConstructor(cArg).newInstance(savePath, parStr, parClass, parConfig);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
