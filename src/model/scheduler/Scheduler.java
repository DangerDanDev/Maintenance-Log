package model.scheduler;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Manages all the open triggers and determines when to execute them
 */
public class Scheduler {
    private ArrayList<Trigger> triggers = new ArrayList<>();

    /**
     * Loops through all the loaded triggers and executes anything with its conditions met
     * then removes all of the executed triggers
     */
    public void checkTriggers() throws SQLException {
        ArrayList<Trigger> executedTriggers = new ArrayList<>();

        for(Trigger trigger : triggers) {
            if (trigger.getCondition().isMet()) {
                trigger.execute();
            }
        }

        removeExecutedTriggers();
    }

    private void removeExecutedTriggers() {
        triggers.removeIf(trigger -> trigger.isExecuted());
    }

    public static void main(String[] args) {

    }
}
