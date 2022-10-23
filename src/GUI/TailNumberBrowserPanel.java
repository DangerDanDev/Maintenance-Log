package GUI;

import data.DatabaseManager;
import data.Tables.AircraftTable;

import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TailNumberBrowserPanel {

    private JComboBox cbTailNumbers;
    private JPanel contentPane;
    private JButton button1;

    public JPanel getContentPane() {
        return contentPane;
    }

    public TailNumberBrowserPanel() {
        getTailNumbers();
    }

    private void getTailNumbers() {
        try (Connection connection = DatabaseManager.getConnection()){

            try(Statement statement = connection.createStatement()) {

                String allTailNumbersQuery = "SELECT * FROM " + AircraftTable._NAME +" ORDER BY " + AircraftTable.COL_TAIL_NUM.NAME + " ASC ";
                ResultSet resultSet = statement.executeQuery(allTailNumbersQuery);

                while(resultSet.next()) {
                    String tailNum = resultSet.getString(AircraftTable.COL_TAIL_NUM.NAME);
                    System.out.println(tailNum);
                    cbTailNumbers.addItem(tailNum);
                }
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void refreshData() {

    }
}
