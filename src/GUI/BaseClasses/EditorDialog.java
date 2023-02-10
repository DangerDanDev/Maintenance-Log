package GUI.BaseClasses;

import GUI.DiscrepancyEditor;
import GUI.StatusEditorPanel;
import data.DBManager;
import data.DatabaseObject;
import data.tables.DiscrepancyTable;
import data.tables.StatusTable;
import data.tables.Table;
import model.Status;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class EditorDialog<T extends DatabaseObject> extends JDialog implements EditorPanel.EditorPanelHost<T> {

    private String editorTitle;
    private JPanel contentPane = new JPanel();

    public String getEditorTitle() {
        return editorTitle;
    }

    public void setEditorTitle(String TITLE) {
        this.editorTitle = TITLE;
    }

    private ArrayList<EditorPanel<T>> panels = new ArrayList<>();

    public EditorDialog(String windowTitle) {
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        setEditorTitle(windowTitle);
        setTitle(getEditorTitle());
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setContentPane(contentPane);
    }

    public EditorDialog(String windowTitle, EditorPanel<T> editorPanel) {
        this(windowTitle);

        addEditorPanel(editorPanel);
    }

    public EditorDialog(String windowTitle, ArrayList<EditorPanel<T>> panels) {
        this(windowTitle);

        for(EditorPanel<T> panel : panels)
            addEditorPanel(panel);
    }

    public void close() {
        dispose();
    }

    public void addEditorPanel(EditorPanel<T> panel) {
        panels.add(panel);
        getContentPane().add(panel.getContentPane());
        panel.setEditorPanelHost(this);
        pack();
    }

    public void removeEditorPanel(EditorPanel<T> panel) {
        panels.remove(panel);
        getContentPane().remove(panel.getContentPane());
        panel.setEditorPanelHost(null);
    }

    @Override
    public void onItemEdited(T item) {
        setTitle(getEditorTitle() + "*");
    }

    @Override
    public void onItemSaved(T item) {
        //only mark our title as saved if ALL of the
        //data we host is saved
        for(EditorPanel panel : panels) {
            if(!panel.getItem().isSaved())
                return;
        }

        setTitle(getEditorTitle());
    }

    @Override
    public void onItemSaveFailed(T item) {

    }

    public static void main(String[] args) {

        try (Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            ArrayList<EditorPanel<Status>> statusPanels = new ArrayList<>();
            for(Status s : StatusTable.getInstance().getAllItems()) {
                statusPanels.add(new StatusEditorPanel(s));
            }

            EditorDialog dialog = new EditorDialog("Statuses", statusPanels);
            dialog.setVisible(true);

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }
}
