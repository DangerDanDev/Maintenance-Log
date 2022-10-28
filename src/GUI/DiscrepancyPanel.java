package GUI;

import data.DatabaseManager;
import data.Discrepancy;
import data.Status;
import data.Tables.AircraftTable;
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
    private JTextField tbDateDiscovered;

    private Discrepancy discrepancy;

    public JPanel getContentPane() {
        return this.contentPane;
    }

    public DiscrepancyPanel(Discrepancy discrepancy) {

        try (Connection conn = DatabaseManager.getConnection()) {
            AircraftTable.populateComboBoxWithAllTailNums(cbTail, conn);
            StatusTable.get().populateStatusComboBox(cbStatus, conn);
            setDiscrepancy(discrepancy);

            initEventHandlers();
        } catch(SQLException ex) {
            System.err.println(ex.getMessage());

            JOptionPane.showMessageDialog(getContentPane(), "There was an error connecting to the database",
                    "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showDialog(JFrame parent) {
        JDialog dialog = new JDialog(parent, Dialog.ModalityType.DOCUMENT_MODAL);
        System.out.println("ModalityType: " + dialog.getModalityType().toString());

        dialog.setSize(800,600);

        dialog.setContentPane(getContentPane());
        dialog.setVisible(true);
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
        tbDateDiscovered.setText(discrepancy.getDateCreated().toString());

        if(discrepancy.getStatus() != null)
            setCBStatusSelection(discrepancy.getStatus());
    }

    /**
     * Because JComboBoxes are a little wonky with overridden .equals() functions,
     * we iterate through the combo box and check the Abbreviation value (it has the "unique" constraint
     * in the database, so there will never be two)
     * @param status
     */
    private void setCBStatusSelection(Status status) {
        if(status == null)
            return;

        for(int i = 0; i < cbStatus.getItemCount(); i++) {
            if(((Status)cbStatus.getItemAt(i)).getAbbreviation().equals(status.getAbbreviation()))
                cbStatus.setSelectedIndex(i);
        }
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            getDiscrepancy().setStatus((Status)cbStatus.getSelectedItem());
            getDiscrepancy().setTailNum(cbTail.getSelectedItem().toString());
            getDiscrepancy().setNarrative(tbNarrative.getText());
            getDiscrepancy().setTurnover(tbNarrative.getText());
            getDiscrepancy().setPartsOnOrder(tbPartsOnOrder.getText());

            try(Connection conn = DatabaseManager.getConnection()) {

                //insert or update as applicable
                if(getDiscrepancy().getId() == Discrepancy.INVALID_ID) {
                    System.out.println("Inserting discrepancy into database");
                    DiscrepancyTable.insertDiscrepancyIntoDatabase(conn, discrepancy);
                }
                else {
                    System.out.println("Updating discrepancy in database");
                    DiscrepancyTable.updateDiscrepancyInDatabase(conn, getDiscrepancy());
                }

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
