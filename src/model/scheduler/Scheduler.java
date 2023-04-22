package model.scheduler;

import data.DatabaseObject;
import data.queries.JoinClause;
import data.tables.DiscrepancyTable;
import data.tables.Table;
import model.Discrepancy;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Manages all the open triggers and determines when to execute them
 */
public class Scheduler {

    private static Scheduler scheduler = new Scheduler();
    public static Scheduler getInstance() { if(scheduler == null) init(); return scheduler; }
    public static void init() { scheduler = new Scheduler();}

    private ArrayList<Trigger> triggers = new ArrayList<>();

    private Scheduler() {
        DiscrepancyTable.getInstance().addListener(new DiscrepancyTableListener());
    }

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

    public void addTrigger(Trigger t) {
        triggers.add(t);
    }

    /**
     * Listens for updated discrepancies
     */
    private class DiscrepancyTableListener implements Table.TableListener<Discrepancy> {
        @Override
        public void onItemAdded(Discrepancy addedItem) {

        }

        @Override
        public void onItemUpdated(Discrepancy editedItem) {
            try {
                checkTriggers();
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }

        @Override
        public void onItemDeleted(Discrepancy deletedItem) {

        }
    }
}
