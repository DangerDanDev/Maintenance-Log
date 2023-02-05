package data;

import java.time.Instant;

/**
 * Base class for everythinhg database; it encapsulates
 */
public class DatabaseObject {

    /**
     * My ID in the SQLite database
     */
    private long id = -1;

    /**
     * The date I was created
     */
    private Instant dateCreated = Instant.now();

    /**
     * The date I was last edited
     */
    private Instant dateLastEdited = Instant.from(dateCreated);

    /**
     *
     */
    private boolean saved = false;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Instant getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Instant dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Instant getDateLastEdited() {
        return dateLastEdited;
    }

    public void setDateLastEdited(Instant dateLastEdited) {
        this.dateLastEdited = dateLastEdited;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;

        if(saved && listener != null)
            listener.onItemSaved();
    }

    @Override
    public boolean equals(Object obj) {
        //if we are the same type and have the same ID, we are
        //the same object
        if(getClass() == obj.getClass()) {
            DatabaseObject dbObj = (DatabaseObject) obj;
            return getId() == dbObj.getId();
        }

        //otherwise do the normal java checks
        return super.equals(obj);
    }

    /**
     * Interface for an EditorDialog to be nofified when I am edited (meaning I am now "unsaved") and
     * when I am saved
     */
    public interface ChangeListener {
        void onItemSaved();
    }

    /**
     * The editor dialog listening for changes to me
     */
    private ChangeListener listener;

    public ChangeListener getListener() {
        return listener;
    }

    public void setListener(ChangeListener listener) {
        this.listener = listener;
    }
}
