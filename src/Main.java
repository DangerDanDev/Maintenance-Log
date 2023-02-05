import data.DBManager;
import data.DatabaseObject;
import data.tables.DiscrepancyTable;
import data.tables.Table;
import model.Discrepancy;
import org.sqlite.SQLiteConfig;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;

public class Main {
    public static void main(String args[]) {
        try(Connection c = DBManager.getConnection()) {

            DBManager.initialize();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
