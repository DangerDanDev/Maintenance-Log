package GUI.actions;

import GUI.BaseClasses.EditorDialog;
import GUI.BaseClasses.EditorPanel;
import model.Discrepancy;

import javax.swing.*;
import java.awt.*;

public abstract class BaseAction extends AbstractAction {

    public final Window OWNER;
    public final EditorPanel.EditorPanelHost EDITOR_PANEL_HOST;

    public BaseAction(Window owner, EditorPanel.EditorPanelHost editorPanelHost, String title) {
        super(title);

        OWNER = owner;
        EDITOR_PANEL_HOST = editorPanelHost;
    }

    public Window getOwner() { return OWNER; }
    public EditorPanel.EditorPanelHost getEditorPanelHost() { return EDITOR_PANEL_HOST; }
}
