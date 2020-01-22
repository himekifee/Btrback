package net.himeki.btrback;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BtrCmdCompleter implements TabCompleter {
    Btrback plugin;

    public BtrCmdCompleter(Btrback plugin) {
        this.plugin = plugin;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        switch (args.length) {
            case 1: {
                return Arrays.asList("backup", "rollback");
            }
            case 2: {
                if (args[0].equalsIgnoreCase("rollback")) {
                    ArrayList<String> rollbackList = new ArrayList<String>();
                    rollbackList.add("list");
                    for (String timeStamp : new BtrRecord(plugin).listBackups(false)) {
                        rollbackList.add(timeStamp);
                    }
                    return rollbackList;
                }
            }
        }
        return null;
    }
}
