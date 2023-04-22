package model.scheduler;

import data.DatabaseObject;

public class Trigger extends DatabaseObject {
    private Condition condition;
    private Task task;

    /**
     * Flag that represents whether or not this trigger has been executed;
     * set to true once execute is called.
     */
    private boolean executed = false;

    public void execute() {
        getTask().execute();
        setExecuted(true);
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public boolean isExecuted() {
        return executed;
    }

    public void setExecuted(boolean executed) {
        this.executed = executed;
    }
}
