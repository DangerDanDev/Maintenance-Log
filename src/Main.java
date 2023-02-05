import data.DBManager;
import data.DatabaseObject;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;

public class Main {
    public static void main(String args[]) {
        try(Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            DatabaseObject obj = new DatabaseObject();
            obj.setDateCreated(Instant.parse("2000-02-03T10:37:30.00Z"));
            obj.setId(13);
            DBManager.TEST_TABLE.updateItem(obj);

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
