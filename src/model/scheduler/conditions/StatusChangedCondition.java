package model.scheduler.conditions;

import data.DBManager;
import data.DatabaseObject;
import data.queries.*;
import data.tables.DiscrepancyTable;
import data.tables.StatusTable;
import model.Discrepancy;
import model.Status;
import model.scheduler.Condition;

import java.sql.SQLException;

/**
 * A condition that monitors a given discrepancy for when it's status is updated to a specific status
 */
public class StatusChangedCondition extends Condition {

    private final static StatusTable STATUS_TABLE = StatusTable.getInstance();
    private final static DiscrepancyTable DISCREPANCY_TABLE = DiscrepancyTable.getInstance();

    /**
     * The discrepancy that I am monitoring for status changes
     */
    private long discrepancyId = DatabaseObject.INVALID_ID;

    /**
     * This condition is met when the given discrepancy's status is changed to this status
     */
    private long statusId = DatabaseObject.INVALID_ID;

    public StatusChangedCondition(long discrepancyId, long statusId) {
        this.discrepancyId = discrepancyId;
        this.statusId = statusId;
    }

    public StatusChangedCondition(Discrepancy d, Status s) {
        this(d.getId(), s.getId());
    }

    @Override
    public boolean isMet() throws SQLException {
        return checkStatus();
    }

    private boolean checkStatus() throws SQLException  {
        Query discrepancyStatusQuery = new Query(DISCREPANCY_TABLE);

        Criterion my_discrepancy = new Criterion(DISCREPANCY_TABLE.COL_ID, "" + discrepancyId);
        discrepancyStatusQuery.addWhereCriterion(my_discrepancy, AndOr.AND);

        Criterion statusIdMatches = new Criterion(STATUS_TABLE.COL_ID, "" + statusId);
        discrepancyStatusQuery.addWhereCriterion(statusIdMatches, AndOr.NONE);

        discrepancyStatusQuery.addJoinClause(
                new JoinClause(JoinType.InnerJoin, STATUS_TABLE, DISCREPANCY_TABLE.COL_STATUS_ID, STATUS_TABLE.COL_ID));

        return DISCREPANCY_TABLE.query(discrepancyStatusQuery).size() > 0;
    }

    public static void main(String[] args) throws SQLException {

        DBManager.initialize();

        Discrepancy d = DISCREPANCY_TABLE.getAllItems().get(0);

        System.out.println(d.getStatus().getTitle());
        StatusChangedCondition condition = new StatusChangedCondition(d.getId(), 1);
        System.out.println("Condition is met: " + condition.isMet());
    }
}
