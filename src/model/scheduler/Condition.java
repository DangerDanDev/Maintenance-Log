package model.scheduler;

import data.DatabaseObject;

import java.sql.SQLException;

/**
 * Represents a condition that must be satisfied before a Task is executed
 */
public abstract class Condition {
    public abstract boolean isMet() throws SQLException;
}
