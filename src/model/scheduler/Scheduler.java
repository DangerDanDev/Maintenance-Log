package model.scheduler;

import data.tables.DiscrepancyTable;
import data.tables.Table;
import model.Discrepancy;
import model.scheduler.conditions.StatusChangedCondition;
import model.scheduler.tasks.StatusChangeTask;

import javax.swing.*;
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

        //unitTest();
    }

    /**
     * Adds a trigger that monitors one discrepancy for a change to a specific status and marks another as IFOC
     * when that happens
     */
    private void unitTest() {
        StatusChangeTask statusChangeTask =  new StatusChangeTask(131, 5);
        StatusChangedCondition statusChangeCondition = new StatusChangedCondition(130, 1);

        Trigger trigger = new Trigger();
        trigger.setCondition(statusChangeCondition);
        trigger.setTask(statusChangeTask);

        addTrigger(trigger);

        try {
            checkTriggers();

        } catch (SQLException ex) {

        }
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
