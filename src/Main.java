import data.DBManager;
import data.DatabaseObject;
import data.tables.DiscrepancyTable;
import data.tables.Table;
import model.Discrepancy;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;

public class Main {
    public static void main(String args[]) {
        try(Connection c = DBManager.getConnection()) {

            DBManager.initialize();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
