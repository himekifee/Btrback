package net.himeki.btrback.tasks;

import net.himeki.btrback.BtrOperation;
import net.himeki.btrback.BtrRecord;
import net.himeki.btrback.Btrback;

import java.util.ArrayList;
import java.util.Comparator;

public class PurgeTask {
    Btrback plugin;

    public PurgeTask(Btrback plugin) {
        this.plugin = plugin;
    }

    public boolean doPurge(String timestamp) {
        BtrOperation operation = new BtrOperation();
        ArrayList<String> snapshotList = new BtrRecord(plugin).listBackups(false);
        snapshotList.sort(Comparator.naturalOrder());
        int index = snapshotList.indexOf(timestamp);
        for (int i = 0; i < index; i++) {                               //remove snapshots in list by order(timestamp)
            if (!operation.deleteSubvol(plugin.getBackupsDir() + "/" + snapshotList.get(i)))
                return false;
            if (!new BtrRecord(plugin).removeRecord(snapshotList.get(i)))
                return false;
        }
        return true;
    }
}
