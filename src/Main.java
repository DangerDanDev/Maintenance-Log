import data.DBManager;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String args[]) {
        try(Connection c = DBManager.getConnection()) {

            DBManager.initialize();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
