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
    private boolean saved;

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
    }
}
