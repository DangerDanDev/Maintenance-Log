package GUI;

import data.DBManager;
import data.tables.DiscrepancyTable;
import data.tables.StatusTable;
import model.Discrepancy;
import model.Status;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class AppFrame extends JFrame {

    private JPanel contentPane;
    private JTabbedPane tabbedPane1;
    private JPanel notesPanel;

    public AppFrame() throws SQLException {
        super("Hawk Logbook");

        notesPanel.setLayout(new BoxLayout(notesPanel, BoxLayout.Y_AXIS));

        loadNotes();

        setSize(800,600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(new MenuManager().menuBar);
        setContentPane(contentPane);
        pack();
        setVisible(true);
    }

    private void showStatusEditor()  {

        try {

            ArrayList<Status> statuses = StatusTable.getInstance().getAllItems();
            ArrayList<EditorPanelDialogBase> panels = new ArrayList<>();
            for (Status s : statuses)
                panels.add(new StatusEditorPanel(s));

            PanelListDialog editorDialog = new PanelListDialog<Status>(panels);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "There was an error trying to open the status editor.");
        }
    }

    private void loadNotes() throws SQLException {
        ArrayList<Discrepancy> discrepancies = DiscrepancyTable.getInstance().getAllItems();

        for(Discrepancy d : discrepancies) {
            notesPanel.add(new DiscrepancyLineItem(d).getCustomContentPane());
        }
    }

    private class MenuManager {
        JMenuBar menuBar = new JMenuBar();

        JMenu file = new JMenu("File");

        JMenu edit = new JMenu("Edit");
        JMenuItem editStatuses = new JMenuItem("Edit Statuses");
        JMenuItem newDiscrepancy = new JMenuItem("New Discrepancy");

        public MenuManager() {
            menuBar.add(file);

            menuBar.add(edit);
            edit.add(editStatuses).addActionListener(listener -> showStatusEditor());
            edit.add(newDiscrepancy);

        }
    }

    public static void main(String[] args) {
        try (Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            AppFrame appFrame = new AppFrame();

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "App crashed with SQL Error");
        }
    }
}
