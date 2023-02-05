import data.DBManager;
import data.DatabaseObject;
import org.sqlite.SQLiteConfig;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String args[]) {
        try(Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            for(int i = 0; i < 3; i++)
                DBManager.TEST_TABLE.addItem(new DatabaseObject());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
