package net.himeki.btrback;

import com.google.gson.stream.JsonWriter;
import net.himeki.btrback.tasks.RollbackTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public final class Btrback extends JavaPlugin {
    private boolean serverInSubvol = false;
    private String rootDir;
    private String parentDir;
    private String backupsDir;
    private String recordsJsonPath;


    @Override
    public void onEnable() {
        rootDir = Bukkit.getServer().getWorldContainer().getAbsolutePath();
        parentDir = new File(rootDir).getParentFile().getParentFile().getAbsolutePath();
        backupsDir = parentDir + "/btrbackups";
        recordsJsonPath = parentDir + "/btrbackups/backups.json";
        if (!new File(this.getDataFolder().getAbsolutePath() + "/config.yml").exists())
            this.saveDefaultConfig();                       //Save config file on first startup
        File btrbackFolder = new File(backupsDir);
        if (!btrbackFolder.exists())
            btrbackFolder.mkdir();                          //Create backup folders if not present
        File jsonFile = new File(recordsJsonPath);
        if (!jsonFile.exists())                             //Create records json file
        {
            try {
                JsonWriter writer = new JsonWriter(new FileWriter(jsonFile));
                writer.beginObject();
                writer.name("backupSnapshots");
                writer.beginArray();
                writer.endArray();
                writer.name("rollbackSnapshots");
                writer.beginArray();
                writer.endArray();
                writer.endObject();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
                Bukkit.getLogger().warning("Failed to create backups json file.");
            }

        }
        BtrOperation ops = new BtrOperation();
        if (!ops.isSubvol(Bukkit.getServer().getWorldContainer().getAbsolutePath())) {
            Bukkit.getLogger().warning("Server is not in a btrfs subvolume, plugin will not work properly.");
        }
        serverInSubvol = true;
        this.getCommand("btrback").setExecutor(new BtrCommand(this));
        this.getCommand("btrback").setTabCompleter(new BtrCmdCompleter(this));

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean isServerInSubvol() {
        return serverInSubvol;
    }

    public String getRootDir() {
        return rootDir;
    }

    public String getParentDir() {
        return parentDir;
    }

    public String getRecordsJsonPath() {
        return recordsJsonPath;
    }

    public String getBackupsDir() {
        return backupsDir;
    }
}
