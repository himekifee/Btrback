package net.himeki.btrback;

import net.himeki.btrback.tasks.RollbackTask;

public class Rollback {
    public static void main(String[] args) {                //For rollback process
        if (!new RollbackTask(null).doRollbackStageTwo())
            System.out.println("Rollback failed.");
        System.out.println("Stage two done.");
    }
}
