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
import java.sql.*;
import java.util.ArrayList;

public class TailNumberBrowserPanel {

    private JTextField tbTailNumbers;
    private JPanel contentPane;
    private JPanel pnlDiscrepancies;
    private JTextArea tbDiscrepancyNarrative;

    private ArrayList<DiscrepancySnippet> discrepancySnippets = new ArrayList<>();

    public JPanel getContentPane() {
        return contentPane;
    }

    public TailNumberBrowserPanel() {
        tbTailNumbers.addActionListener(new TailNumberComboBoxListener());
    }

    public void refreshData() {

        try (Connection connection = DatabaseManager.getConnection()) {

            final String SELECT_ALL = " SELECT * ";

            final String FROM_DISCREPANCY_TABLE = " FROM " + DiscrepancyTable.get().getName() + ", " + StatusTable.get().getName();

            final String WHERE_DISCREPANCY_STATUS_MATCHES_STATUS_ID = " WHERE " + DiscrepancyTable.get().getName() + "." +
                    DiscrepancyTable.COL_STATUS + "=" + StatusTable.get().getName() + "." + StatusTable.COL_ID;

            final String TAIL_NUM_MATCHES_SEARCH = DiscrepancyTable.COL_TAIL_NUM + " LIKE '%' || ? || '%' "; //1 should be equal to the value in the tail number search box

            final String fullSQL = SELECT_ALL + FROM_DISCREPANCY_TABLE + WHERE_DISCREPANCY_STATUS_MATCHES_STATUS_ID
                    + " AND " + TAIL_NUM_MATCHES_SEARCH;
            System.out.println(fullSQL);

            try (PreparedStatement statement = connection.prepareStatement(fullSQL)) {

                statement.setString(1, tbTailNumbers.getText() + "");

                try (ResultSet resultSet = statement.executeQuery()) {

                    pnlDiscrepancies.removeAll();
                    pnlDiscrepancies.setLayout(new GridLayout(0, 1));

                    while (resultSet.next()) {

                        //pull the discrepancy from the result set
                        Discrepancy discrepancy = DiscrepancyTable.getDiscrepancyFromResultSet(resultSet);

                        //add the new discrepancy to our list
                        pnlDiscrepancies.add(new DiscrepancySnippet(discrepancy).getContentPane());
                    }
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }

            /*try (Statement statement = connection.createStatement()) {

                final String DiscrepancyTableAndStatusTable = DiscrepancyTable.get().getName() + ", " + StatusTable.get().getName();

                final String TailNumCol_ContainsTailNumText = DiscrepancyTable.COL_TAIL_NUM + " LIKE '%" + tbTailNumbers.getText() + "%' ";

                final String DiscrepancyStatusEqualsStatusID = DiscrepancyTable.get().getName() + "." + DiscrepancyTable.COL_STATUS + "=" +
                                                                StatusTable.get().getName() + "." + StatusTable.COL_ID + " ";


                String discrepanciesQuery = "SELECT * FROM " + DiscrepancyTableAndStatusTable +
                        " WHERE " + TailNumCol_ContainsTailNumText +
                        " AND " + DiscrepancyStatusEqualsStatusID;

                System.out.println(discrepanciesQuery);
                try (ResultSet resultSet = statement.executeQuery(discrepanciesQuery)) {

                    pnlDiscrepancies.removeAll();
                    pnlDiscrepancies.setLayout(new GridLayout(0, 1));

                    while (resultSet.next()) {

                        //pull the discrepancy from the result set
                        Discrepancy discrepancy = DiscrepancyTable.getDiscrepancyFromResultSet(resultSet);

                        //add the new discrepancy to our list
                        pnlDiscrepancies.add(new DiscrepancySnippet(discrepancy).getContentPane());
                    }
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                }
            }*/

            pnlDiscrepancies.revalidate();

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public class TailNumberComboBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            refreshData();
            System.out.println("Searching for " + tbTailNumbers.getText());
        }
    }
}
