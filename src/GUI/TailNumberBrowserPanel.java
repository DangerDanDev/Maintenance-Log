package GUI;

import data.DatabaseManager;
import data.Discrepancy;
import data.Status;
import data.Tables.AircraftTable;
import data.Tables.DiscrepancyTable;
import data.Tables.StatusTable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class TailNumberBrowserPanel {

    private JComboBox cbTailNumbers;
    private JPanel contentPane;
    private JPanel pnlDiscrepancies;
    private JTextArea tbDiscrepancyNarrative;

    private ArrayList<DiscrepancySnippet> discrepancySnippets = new ArrayList<>();

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

                final String DiscrepancyTableAndStatusTable = DiscrepancyTable.get().getName() + ", " + StatusTable.get().getName();

                final String TailNumberIsSelectedOnComboBox = DiscrepancyTable.COL_TAIL_NUM + " = " + cbTailNumbers.getSelectedItem().toString() + " ";

                final String DiscrepancyStatusEqualsStatusID = DiscrepancyTable.get().getName() + "." + DiscrepancyTable.COL_STATUS + "=" +
                                                                StatusTable.get().getName() + "." + StatusTable.COL_ID + " ";

                //SELECT * FROM discrepancies
                //WHERE tail_number = cbTailNumbers.SelectedItem()
                String discrepanciesQuery = "SELECT * FROM " + DiscrepancyTableAndStatusTable +
                        " WHERE " + TailNumberIsSelectedOnComboBox +
                        " AND " + DiscrepancyStatusEqualsStatusID;

                System.out.println(discrepanciesQuery);
                ResultSet resultSet = statement.executeQuery(discrepanciesQuery);

                pnlDiscrepancies.removeAll();
                pnlDiscrepancies.setLayout(new GridLayout(0, 1));

                while (resultSet.next()) {

                    //pull the discrepancy from the result set
                    Discrepancy discrepancy = DiscrepancyTable.getDiscrepancyFromResultSet(resultSet);

                    //add the new discrepancy to our list
                    pnlDiscrepancies.add(new DiscrepancySnippet(discrepancy).getContentPane());
                }
            }

            pnlDiscrepancies.revalidate();

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
