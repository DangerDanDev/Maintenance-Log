package model.scheduler;

import data.DatabaseObject;

import java.sql.SQLException;

/**
 * Represents a task that is to be completed after a given Condition is met
 */
public abstract class Task {
    public abstract void execute() throws SQLException;
}
