package data.tables;

import data.DBManager;
import data.DatabaseObject;
import data.QueryIndexer;
import model.Status;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StatusTable extends Table<Status> {

    public final Column COL_TITLE = new Column(this, "title", TEXT, NOT_NULL);
    public final Column COL_COLOR = new Column(this, "color", TEXT, NOT_NULL);

    private static final StatusTable instance = new StatusTable();
    public static StatusTable getInstance() { return instance;}

    public StatusTable() {
        super("status");

        addColumn(COL_TITLE);
        addColumn(COL_COLOR);
    }

    @Override
    public Status inflateItemFromResultSet(ResultSet rs) throws SQLException {
        Status status = new Status();

        status.setId(rs.getLong(COL_ID.NAME));
        status.setTitle(rs.getString(COL_TITLE.NAME));

        String rawColor = rs.getString(COL_COLOR.NAME);
        String rgb[] = rawColor.split(",");
        status.setColor(new Color(Integer.parseInt(rgb[0].trim()), Integer.parseInt(rgb[1].trim()), Integer.parseInt(rgb[2].trim())));
        status.setSaved(true);

        return status;
    }

    @Override
    public void setStatementValues(PreparedStatement statement, QueryIndexer indexer, Status item) throws SQLException {
        super.setStatementValues(statement, indexer, item);

        String rgb = item.getColor().getRed() + "," + item.getColor().getGreen() + "," + item.getColor().getBlue();

        statement.setString(indexer.indexOf(COL_TITLE), item.getTitle());
        statement.setString(indexer.indexOf(COL_COLOR), rgb);
    }

    public static void main(String[] args) {
        try(Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            Status status = null;
            for(int i = 0; i < 5; i++) {
                status = new Status();
                status.setTitle("status " + (i+1));
                status.setColor(Color.WHITE);
                getInstance().addItem(status);
            }

            status.setColor(Color.GREEN);
            getInstance().updateItem(status);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
