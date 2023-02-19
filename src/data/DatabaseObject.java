package data;

import javax.swing.*;
import java.time.Instant;

/**
 * Base class for everythinhg database; it encapsulates
 */
public class DatabaseObject {

    /**
     * Constant for an id field indicating that this item has not
     * yet been saved to the database.
     */
    public static final long INVALID_ID = -1;

    /**
     * My ID in the SQLite database
     */
    private long id = INVALID_ID;

    /**
     * The date I was created
     */
    private Instant dateCreated = Instant.now();

    /**
     * The date I was last edited
     */
    private Instant dateLastEdited = Instant.from(dateCreated);

    public DatabaseObject() {}

    public DatabaseObject(long id, Instant dateCreated, Instant dateLastEdited) {
        setId(id);
        setDateCreated(dateCreated);
        setDateLastEdited(dateLastEdited);
    }

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

    public void selectInComboBox(JComboBox cb) {

        boolean foundMatch = false;

        for(int i = 0; i < cb.getItemCount(); i++) {
            boolean selectedItemIsDatabaseObject = cb.getItemAt(i) instanceof DatabaseObject;
            boolean bothIDsMatch = selectedItemIsDatabaseObject && ((DatabaseObject)cb.getItemAt(i)).getId() == getId();
            if (selectedItemIsDatabaseObject && bothIDsMatch) {
                cb.setSelectedIndex(i);
                foundMatch = true;
                break;
            }
        }
    }
}
