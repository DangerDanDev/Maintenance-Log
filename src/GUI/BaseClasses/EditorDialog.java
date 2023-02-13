package GUI.BaseClasses;

import GUI.DiscrepancyEditor;
import GUI.LogEntryEditor;
import GUI.StatusEditorPanel;
import data.DBManager;
import data.DatabaseObject;
import data.tables.LogEntryTable;
import data.tables.StatusTable;
import model.Discrepancy;
import model.LogEntry;
import model.Status;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class EditorDialog<T extends DatabaseObject> extends JDialog implements EditorPanel.EditorPanelHost<T> {

    private String editorTitle;
    private JPanel borderLayout = new JPanel();

    private JPanel northPanel = new JPanel();
    private JPanel centerPanel = new JPanel();
    private JPanel southPanel = new JPanel();
    private JPanel eastPanel = new JPanel();
    private JPanel westPanel = new JPanel();

    private JButton bSave = new JButton("Save");
    private JButton bCancel = new JButton("Cancel");

    private final Window OWNER;

    public String getEditorTitle() {
        return editorTitle;
    }

    public void setEditorTitle(String TITLE) {
        this.editorTitle = TITLE;
    }

    private ArrayList<EditorPanel<T>> editorPanels = new ArrayList<>();

    public EditorDialog(Window owner, String windowTitle) {
        super(owner);
        setModal(true);

        OWNER = owner;

        setEditorTitle(windowTitle);
        setTitle(getEditorTitle());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));


        initBorderLayout();
        initSouthPanel();

        setContentPane(borderLayout);
    }

    private void initBorderLayout() {
        borderLayout.setLayout(new BorderLayout());

        borderLayout.add(northPanel, BorderLayout.NORTH);

        JScrollPane centerScrollPane = new JScrollPane(centerPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        borderLayout.add(centerScrollPane, BorderLayout.CENTER);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        borderLayout.add(southPanel, BorderLayout.SOUTH);
        borderLayout.add(eastPanel, BorderLayout.EAST);
        borderLayout.add(westPanel, BorderLayout.WEST);
    }

    private void initSouthPanel() {
        southPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        bSave.addActionListener(e -> saveAll());
        southPanel.add(bSave);

        bCancel.addActionListener(e -> dispose());
        southPanel.add(bCancel);
    }

    public EditorDialog(Window owner,  String windowTitle, EditorPanel<T> editorPanel) {
        this(owner, windowTitle);

        addEditorPanel(editorPanel);
    }

    public EditorDialog(Window owner, String windowTitle, ArrayList<EditorPanel<T>> panels) {
        this(owner, windowTitle);

        for(EditorPanel<T> panel : panels)
            addEditorPanel(panel);
    }

    public void addEditorPanel(EditorPanel<T> panel, String borderLayoutPosition) {
        JPanel panelToAddTo = null;

        //select which panel we want to add to
        switch (borderLayoutPosition) {
            case BorderLayout.NORTH:
                panelToAddTo = northPanel;
                break;
            case BorderLayout.SOUTH:
                panelToAddTo = southPanel;
                break;
            case BorderLayout.CENTER:
                panelToAddTo = centerPanel;
                break;
            case BorderLayout.WEST:
                panelToAddTo = westPanel;
                break;
            case BorderLayout.EAST:
                panelToAddTo = eastPanel;
                break;
        }

        editorPanels.add(panel);
        panelToAddTo.add(panel.getContentPane());
        panel.setEditorPanelHost(this);
        pack();
    }

    public void addEditorPanel(EditorPanel<T> panel) {
        addEditorPanel(panel, BorderLayout.CENTER);
    }

    public void removeEditorPanel(EditorPanel<T> panel) {
        editorPanels.remove(panel);

        northPanel.remove(panel.getContentPane());
        centerPanel.remove(panel.getContentPane());
        southPanel.remove(panel.getContentPane());
        eastPanel.remove(panel.getContentPane());
        westPanel.remove(panel.getContentPane());

        panel.setEditorPanelHost(null);
    }

    /**
     * Attempts
     */
    private void saveAll() {

        //attempt to save each editor panel's info
        //if any of them fail to save, keep track of that fail
        boolean failDetected = false;
        for(EditorPanel<T> panel : editorPanels) {
            if (!panel.save()) {
                failDetected = true;
            }
        }

        //if everything went according to plan
        if(!failDetected) {

            //then close the window
            super.dispose();
        } else {
            //TODO: Handle the fail if applicable
        }

    }

    @Override
    public void dispose() {
        boolean unsavedData = false;

        //check for any unsaved data
        for(EditorPanel<T> editorPanel : editorPanels) {
            if (!editorPanel.getItem().isSaved()) {
                unsavedData = true;
            }
        }

        //if all of our data is saved, unsubscribe the editors from their tables and close the dialog
        if(!unsavedData) {
            unsubscribeEditorPanelsFromTable();
            super.dispose();
        }
        //if we DO have unsaved changes, prompt the user for what to do
        else {
            //we have unsaved data: prompt the user about closing it:
            String options[] = {"Save and close", "Close anyways", "Cancel"};
            final int SAVE_AND_CLOSE = 0, CLOSE_WITHOUT_SAVING = 1, CANCEL = 2;

            int result = JOptionPane.showOptionDialog(null, "You have unsaved data on this page, what would you like to do?", "Save?",
                    0, JOptionPane.ERROR_MESSAGE,null, options, 0);

            switch (result) {
                case SAVE_AND_CLOSE:
                    saveAll();
                    break;

                case CLOSE_WITHOUT_SAVING:
                    unsubscribeEditorPanelsFromTable();
                    super.dispose();
                    break;

                case CANCEL:
                    //return to the editor pane
                    break;
            }
        }
    }

    /**
     * Called when we are closing the dialog; Removes all of my editor panels
     * from their applicable table's subscription list. The panels should no longer exist
     * so they don't need updates.
     */
    private void unsubscribeEditorPanelsFromTable() {
        for(EditorPanel<T> panel : editorPanels) {
            panel.getTable().removeListener(panel);
        }
    }

    @Override
    public void onItemEdited(T item) {
        setTitle(getEditorTitle() + "*");
    }

    @Override
    public void onItemSaved(T item) {
        //only mark our title as saved if ALL of the
        //data we host is saved
        for(EditorPanel panel : editorPanels) {
            if(!panel.getItem().isSaved())
                return;
        }

        setTitle(getEditorTitle());
    }

    @Override
    public void onItemSaveFailed(T item) {

    }

    public static void showDiscrepancy(Discrepancy d, Window parent) {
        EditorDialog<Discrepancy> discrepancyEditorDialog = new EditorDialog<Discrepancy>(parent,"Discrepancy Editor");
        discrepancyEditorDialog.addEditorPanel(new DiscrepancyEditor(discrepancyEditorDialog, d, discrepancyEditorDialog),
                BorderLayout.WEST);

        for(LogEntry logEntry : LogEntryTable.getInstance().getLogEntriesAgainstDiscrepancy(d)) {

            EditorPanel editor = new LogEntryEditor(logEntry, discrepancyEditorDialog, null);
            discrepancyEditorDialog.addEditorPanel(editor, BorderLayout.CENTER);
        }

        discrepancyEditorDialog.setSize(1024, 768);
        discrepancyEditorDialog.setVisible(true);
    }

    public static void main(String[] args) {

        try (Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            EditorDialog dialog = new EditorDialog(null,"Statuses");

            ArrayList<EditorPanel<Status>> statusPanels = new ArrayList<>();
            for(Status s : StatusTable.getInstance().getAllItems()) {
                dialog.addEditorPanel(new StatusEditorPanel(dialog, s));
            }

            dialog.setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}
