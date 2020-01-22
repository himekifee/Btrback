package net.himeki.btrback.tasks;

import net.himeki.btrback.BtrOperation;
import net.himeki.btrback.Btrback;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RollbackTask {
    Btrback plugin;

    public RollbackTask(Btrback plugin) {
        this.plugin = plugin;
    }

    public boolean doRollbackStageOne(String snapshotName) {
        String serverJarPath = plugin.getConfig().getString("rollback.startupJar");
        if (!new File(plugin.getRootDir() + "/" + serverJarPath).exists()) {
            Bukkit.getLogger().warning("Cannot find proper server jar file in root directory with the name from config.yml.");
            return false;
        }
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date());
        if (!new BackupTask(plugin).doBackup(timeStamp, true)) {
            Bukkit.getLogger().warning("Cannot back up the latest server state. Rollback canceled.");
            return false;
        }
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(plugin.getRootDir() + "/rollback.tmp"));
            writer.write(snapshotName);
            writer.close();
            Bukkit.getLogger().info("Saved rollback.tmp for rollback.");
        } catch (IOException e) {
            e.printStackTrace();
            new BtrOperation().deleteSubvol(plugin.getBackupsDir() + timeStamp);
            Bukkit.getLogger().warning("Cannot save temp rollback file. Rollback canceled.");
            return false;
        }

        try {
            Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
            getFileMethod.setAccessible(true);
            File pluginJarFile = (File) getFileMethod.invoke(plugin);
            Files.copy(pluginJarFile.toPath(), Paths.get(serverJarPath), StandardCopyOption.REPLACE_EXISTING);
            Bukkit.getLogger().info("Replaced server jar file.");
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | IOException e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Cannot replace server jar with plugin jar.");
            return false;
        }
        return true;
    }

    public boolean doRollbackStageTwo() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("rollback.tmp"));
            String timeStamp = reader.readLine();
            String PWD = Paths.get(".").toAbsolutePath().normalize().toString();
            String parentDir = Paths.get("..").toAbsolutePath().normalize().toString();
            BtrOperation operation = new BtrOperation();
            if (!operation.deleteSubvol(PWD)) {
                System.out.println("Cannot delete current server subvolume.");
                return false;
            }
            if (!operation.createSnapshot(parentDir + "/btrbackups/" + timeStamp, PWD)) {
                System.out.println("Cannot restore snapshot " + timeStamp + ".");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
