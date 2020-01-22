package net.himeki.btrback;

import net.himeki.btrback.tasks.RollbackTask;

public class Rollback {
    public static void main(String[] args) {                //For rollback process
        if (!new RollbackTask().doRollbackStageTwo())
            System.out.println("Rollback failed.");
    }
}
