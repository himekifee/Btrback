package net.himeki.btrback.tasks;

import net.himeki.btrback.BtrOperation;
import net.himeki.btrback.BtrRecord;
import net.himeki.btrback.Btrback;
import org.bukkit.Bukkit;

public class BackupTask {
    public boolean doBackup(String timeStamp, Boolean isBeforeRollback, Btrback plugin) {
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "save-all");
        BtrOperation operation = new BtrOperation();
        if (!operation.isSubvol(plugin.getRootDir())) {
            Bukkit.getLogger().warning("The server root is not in a btrfs subvolume. Backup canceled.");
            return false;
        }
        if (!operation.createSnapshot(plugin.getRootDir(), plugin.getBackupsDir() + "/" + timeStamp)) {
            Bukkit.getLogger().warning("Cannot create snapshot " + timeStamp);
            return false;
        }
        if (!isBeforeRollback) {
            if (!new BtrRecord(plugin).addToBackups(timeStamp)) {
                operation.deleteSubvol(plugin.getBackupsDir() + timeStamp);
                Bukkit.getLogger().warning("Cannot write the record to json file, backup canceled.");
                return false;
            }
        } else {
            if (!new BtrRecord(plugin).addToRollbacks(timeStamp)) {
                operation.deleteSubvol(plugin.getBackupsDir() + timeStamp);
                Bukkit.getLogger().warning("Cannot write the record to json file, backup canceled.");
                return false;
            }

        }
        return true;
    }
}
