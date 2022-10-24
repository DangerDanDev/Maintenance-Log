package GUI;

import data.DatabaseManager;
import data.Discrepancy;
import data.Tables.AircraftTable;
import data.Tables.DiscrepancyTable;

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

                //SELECT * FROM discrepancies
                //WHERE tail_number = cbTailNumbers.SelectedItem()
                String discrepanciesQuery = "SELECT * FROM " + DiscrepancyTable.get().getName() +
                        " WHERE " + DiscrepancyTable.COL_TAIL_NUM.NAME + " = " + cbTailNumbers.getSelectedItem().toString();
                ResultSet resultSet = statement.executeQuery(discrepanciesQuery);

                System.out.println(discrepanciesQuery);

                pnlDiscrepancies.removeAll();
                pnlDiscrepancies.setLayout(new GridLayout(0, 1));

                while (resultSet.next()) {

                    //pull the discrepancy from the result set
                    Discrepancy discrepancy = new Discrepancy(
                            resultSet.getLong(DiscrepancyTable.COL_ID.NAME),
                            resultSet.getString(DiscrepancyTable.COL_TAIL_NUM.NAME),
                            resultSet.getString(DiscrepancyTable.COL_NARRATIVE.NAME),
                            resultSet.getDate(DiscrepancyTable.COL_DATE_CREATED.NAME),
                            resultSet.getString(DiscrepancyTable.COL_TURNOVER.NAME),
                            resultSet.getString(DiscrepancyTable.COL_PARTS_ON_ORDER.NAME)

                    );

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
