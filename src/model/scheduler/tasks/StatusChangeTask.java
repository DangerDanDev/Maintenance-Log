package model.scheduler.tasks;

import data.DatabaseObject;
import data.tables.DiscrepancyTable;
import data.tables.StatusTable;
import model.Discrepancy;
import model.scheduler.Task;

import java.sql.SQLException;

public class StatusChangeTask extends Task {
    private long discrepancyId = DatabaseObject.INVALID_ID;
    private long statusId = DatabaseObject.INVALID_ID;

    private final DiscrepancyTable discrepancyTable = DiscrepancyTable.getInstance();

    public StatusChangeTask(long discrepancyId, long statusId) {
        this.discrepancyId = discrepancyId;
        this.statusId = statusId;
    }

    @Override
    public void execute() throws SQLException {
        Discrepancy d = discrepancyTable.getItemById(discrepancyId);

        d.setStatus(StatusTable.getInstance().getItemById(statusId));

        discrepancyTable.updateItem(d, this);
    }
}
