package GUI;

import data.DBManager;
import data.DatabaseObject;
import data.tables.StatusTable;
import model.Status;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * A base class for dialogs of multiple editable items;
 * IE: Edit all the statuses from the same screen,
 * or the aircraft profiles, etc
 */
public class PanelListDialog<T extends DatabaseObject> extends JDialog {

    /**
     * List of all the panels I have domain over. Typically, these will be
     * custom editor panels (ie: StatusEditorPanel, AircraftProfileEditorPanel, etc)
     */
    private ArrayList<EditorDialogAbstract<T>> panels = new ArrayList<>();

    private JPanel contentPane = new JPanel();

    public PanelListDialog(ArrayList<EditorDialogAbstract> customPanels) {

        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(contentPane, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        populate(customPanels);

        setContentPane(scrollPane);
        setLocation(3000, 600);
        setModal(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
        setVisible(true);
    }

    /**
     * Clears all of my children and populates the new editor list with the
     * EditorPanels passed in
     * @param panelsToAdd
     */
    public void populate(ArrayList<EditorDialogAbstract> panelsToAdd) {
        panels.clear();
        contentPane.removeAll();

        for(EditorDialogAbstract<T> panel : panelsToAdd) {
            addPanel(panel);
        }
    }

    /**
     * Adds an EditorPanel to my current content window
     * @param panel
     */
    public void addPanel(EditorDialogAbstract panel) {
        panels.add(panel);
        contentPane.add(panel.getCustomContentPane());
    }

    public void removePanel(EditorDialogAbstract panel) {
        panels.remove(panel);
        contentPane.remove(panel.getCustomContentPane());
    }

    public static void main(String[] args) throws SQLException{
        try (Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            ArrayList<EditorDialogAbstract> customPanels = new ArrayList<>();
            ArrayList<Status> statuses = StatusTable.getInstance().getAllItems();
            for(Status s : statuses){
                customPanels.add(new StatusEditorPanel(s));
            }

            PanelListDialog listDialog = new PanelListDialog(customPanels);

        } catch (
                SQLException ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
}
