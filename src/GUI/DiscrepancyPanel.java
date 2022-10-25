package GUI;

import data.DatabaseManager;
import data.Discrepancy;
import data.Status;
import data.Tables.DiscrepancyTable;
import data.Tables.StatusTable;
import org.sqlite.jdbc4.JDBC4Connection;

import javax.swing.*;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class DiscrepancyPanel {
    private JComboBox cbTail;
    private JTextArea tbTurnover;
    private JTextArea tbPartsOnOrder;
    private JComboBox cbStatus;
    private JPanel contentPane;
    private JTextArea tbNarrative;
    private JButton btnSave;
    private JTextField tbDiscoveredBy;

    private Discrepancy discrepancy;

    public JPanel getContentPane() {
        return this.contentPane;
    }

    public DiscrepancyPanel(Discrepancy discrepancy) {

        try (Connection conn = DatabaseManager.getConnection()) {
            StatusTable.get().populateStatusComboBox(cbStatus, conn);
            setDiscrepancy(discrepancy);

            initEventHandlers();
        } catch(SQLException ex) {
            System.err.println(ex.getMessage());

            JOptionPane.showMessageDialog(getContentPane(), "There was an error connecting to the database",
                    "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initEventHandlers() {
        btnSave.addActionListener(new SaveButtonListener());
        cbStatus.addActionListener(new StatusBoxListener());
    }


    public Discrepancy getDiscrepancy() {
        return discrepancy;
    }

    public void setDiscrepancy(Discrepancy discrepancy) {
        this.discrepancy = discrepancy;

        cbTail.setSelectedItem(discrepancy.getTailNum());
        tbTurnover.setText(discrepancy.getTurnover());
        tbPartsOnOrder.setText(discrepancy.getPartsOnOrder());
        tbNarrative.setText(discrepancy.getNarrative());

        tbDiscoveredBy.setText(getDiscrepancy().getStatus().getTitle());
        cbStatus.setSelectedItem(discrepancy.getStatus());
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try(Connection conn = DatabaseManager.getConnection()) {
                DiscrepancyTable.updateDiscrepancyInDatabase(conn, getDiscrepancy());
                System.out.println("Discrepancy successfully saved.");
            } catch(SQLException ex) {
                System.err.println(ex.getMessage());
                JOptionPane.showMessageDialog(getContentPane(), "Could not save the file, database connection error.",
                        "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class StatusBoxListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            getDiscrepancy().setStatus((Status)cbStatus.getSelectedItem());
            System.out.println("new status: " + getDiscrepancy().getStatus().getTitle());
        }
    }

}
