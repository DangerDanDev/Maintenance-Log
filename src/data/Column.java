package data;

public class Column {
    public final String NAME;
    public final String TYPE;
    public final String CONSTRAINTS;

    public Column(String name, String type, String constraints) {
        this.NAME = name;
        this.TYPE = type;
        this.CONSTRAINTS = constraints;
    }


    /**
     *
     * @return The name of the column
     */
    @Override
    public String toString() {
        return NAME;
    }

    /**
     *
     * @return My column name, data type, and constraints as they would be put into a Create Table query;
     * IE: "NAME TYPE INTEGER NOT NULL"
     */
    public String getCreateString() {
        return NAME + " " + TYPE + " " + CONSTRAINTS;
    }
}
