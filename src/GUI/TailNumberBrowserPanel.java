package GUI;

import data.DatabaseManager;
import data.Tables.AircraftTable;
import data.Tables.DiscrepancyTable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TailNumberBrowserPanel {

    private JComboBox cbTailNumbers;
    private JPanel contentPane;
    private JTextArea tbDiscrepancyNarrative;

    public JPanel getContentPane() {
        return contentPane;
    }

    public TailNumberBrowserPanel() {
        getTailNumbers();

        cbTailNumbers.addActionListener(new TailNumberComboBoxListener());
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

        try (Connection connection = DatabaseManager.getConnection()) {

            try (Statement statement = connection.createStatement()) {

                //SELECT * FROM discrepancies
                //WHERE tail_number = cbTailNumbers.SelectedItem()
                String discrepanciesQuery = "SELECT * FROM " + DatabaseManager.DISCREPANCY_TABLE.getName() +
                        " WHERE " + DiscrepancyTable.COL_TAIL_NUM.NAME + " = " + cbTailNumbers.getSelectedItem().toString() ;
                ResultSet resultSet = statement.executeQuery(discrepanciesQuery);

                System.out.println(discrepanciesQuery);

                tbDiscrepancyNarrative.setText(""
                );
                while(resultSet.next()) {
                    tbDiscrepancyNarrative.setText(tbDiscrepancyNarrative.getText() + "\n" +
                            resultSet.getString(DiscrepancyTable.COL_NARRATIVE.NAME));
                }
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public class TailNumberComboBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            refreshData();
        }
    }
}
