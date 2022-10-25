package GUI;

import data.DatabaseManager;
import data.Discrepancy;
import data.Status;
import data.Tables.StatusTable;
import org.sqlite.jdbc4.JDBC4Connection;

import javax.swing.*;
import javax.xml.crypto.Data;
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

    private Discrepancy discrepancy;

    public JPanel getContentPane() {
        return this.contentPane;
    }

    public DiscrepancyPanel(Discrepancy discrepancy) {

        try (Connection conn = DatabaseManager.getConnection()) {
            StatusTable.get().populateStatusComboBox(cbStatus, conn);
        } catch(SQLException ex) {
            System.err.println(ex.getMessage());
        }

        setDiscrepancy(discrepancy);
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
        cbStatus.setSelectedItem(discrepancy);

        cbStatus.setSelectedItem(discrepancy.getStatus());
    }

}
