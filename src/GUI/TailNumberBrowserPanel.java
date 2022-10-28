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
    private JPanel westPanel;
    private JButton btnNewDiscrepancy;
    private JTextArea tbDiscrepancyNarrative;

    private ArrayList<DiscrepancySnippet> discrepancySnippets = new ArrayList<>();

    public JPanel getContentPane() {
        return contentPane;
    }

    public JFrame frame;

    public TailNumberBrowserPanel() {
        tbTailNumbers.addActionListener(new TailNumberComboBoxListener());
        btnNewDiscrepancy.addActionListener(new NewDiscrepancyListener());

        refreshData();

        frame = new JFrame();
        frame.setSize(800,600);
        frame.setContentPane(getContentPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public void show() {
        frame.setVisible(true);
    }

    public class QueryDiscrepancyHelper {
        public ArrayList<Discrepancy> QueryDiscrepancies(Connection connection) throws SQLException {

            ArrayList<Discrepancy> discrepancies = new ArrayList<>();

            final String SELECT_ALL = " SELECT * ";

            final String FROM_DISCREPANCY_TABLE_AND_STATUS_TABLE = " FROM " + DiscrepancyTable.get().getName() + ", " + StatusTable.get().getName();

            final String WHERE_DISCREPANCY_STATUS_MATCHES_STATUS_ID = " WHERE " + DiscrepancyTable.get().getName() + "." +
                    DiscrepancyTable.COL_STATUS + "=" + StatusTable.get().getName() + "." + StatusTable.COL_ID;

            final String TAIL_NUM_MATCHES_SEARCH = DiscrepancyTable.COL_TAIL_NUM + " LIKE '%' || ? || '%' "; //1 should be equal to the value in the tail number search box

            final String fullSQL = SELECT_ALL + FROM_DISCREPANCY_TABLE_AND_STATUS_TABLE + WHERE_DISCREPANCY_STATUS_MATCHES_STATUS_ID
                    + " AND " + TAIL_NUM_MATCHES_SEARCH;
            System.out.println(fullSQL);

            try (PreparedStatement statement = connection.prepareStatement(fullSQL)) {

                statement.setString(1, tbTailNumbers.getText() + "");

                try (ResultSet resultSet = statement.executeQuery()) {

                    while (resultSet.next()) {

                        //pull the discrepancy from the result set
                        Discrepancy discrepancy = DiscrepancyTable.getDiscrepancyFromResultSet(resultSet);
                        discrepancies.add(discrepancy);
                    }

                    return discrepancies;
                } catch (SQLException ex) {
                    System.err.println(ex.getMessage());
                    throw ex;
                }
            } catch(SQLException ex) {
                System.err.println(ex.getMessage());
                throw  ex;
            }
        }
    }

    public void refreshData() {

        try (Connection connection = DatabaseManager.getConnection()) {

            try {
                ArrayList<Discrepancy> discrepancies = new QueryDiscrepancyHelper().QueryDiscrepancies(connection);

                pnlDiscrepancies.removeAll();
                pnlDiscrepancies.setLayout(new GridLayout(0, 1));

                for (Discrepancy disc : discrepancies) {
                    //add the new discrepancy to our list
                    pnlDiscrepancies.add(new DiscrepancySnippet(disc).getContentPane());
                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
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
            System.out.println("Searching for " + tbTailNumbers.getText());
        }
    }

    public class NewDiscrepancyListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            DiscrepancyPanel discrepancyPanel = new DiscrepancyPanel(new Discrepancy());
            discrepancyPanel.showDialog(frame);

        }
    }
}
