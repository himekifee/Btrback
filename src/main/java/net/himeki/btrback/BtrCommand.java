package net.himeki.btrback;

import net.himeki.btrback.tasks.BackupTask;
import net.himeki.btrback.tasks.RollbackTask;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BtrCommand implements CommandExecutor {
    Btrback plugin;

    public BtrCommand(Btrback parent) {
        this.plugin = parent;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch (args.length) {                      // Start from 0
            case 1:
                if (args[0].equalsIgnoreCase("backup")) {
                    if (plugin.isServerInSubvol()) {
                        BackupTask aTask = new BackupTask();
                        String timestamp = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date());
                        if (!aTask.doBackup(timestamp, false, plugin))
                            sender.sendMessage("Backup failed. Check console for details.");
                        else sender.sendMessage("Successfully created snapshot " + timestamp);

                    } else sender.sendMessage("Server not in a btrfs subvolume, plugin will not work.");
                }
                if (args[0].equalsIgnoreCase("rollback")) {
                    sender.sendMessage("Usage: /btrback rollback [list/timestamp]");
                }
            case 2:
                if (args[0].equalsIgnoreCase("rollback")) {
                    ArrayList<String> backupsList = new BtrRecord(plugin).listBackups(false);

                    if (backupsList.contains(args[1]))                                                              //Valid timeStamp, do rollback
                    {
                        if (new RollbackTask().doRollbackStageOne(args[1], plugin)) {
                            sender.sendMessage("Successfully done stage one, shutting the server. Please start it to complete stage two.");
                            Bukkit.shutdown();
                        } else sender.sendMessage("Failed to finish stage one. Check console logs for details.");
                    } else {
                        if (args[1].equalsIgnoreCase("list")) {
                            sender.sendMessage("Valid backups are listed below:");
                            for (String a : backupsList) {
                                sender.sendMessage(a);
                            }
                        }
                    }
                }
        }
        return true;
    }
}
