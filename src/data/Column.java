package data;

public class Column {
    public final String NAME;
    public final String TYPE;

    public Column(String name, String type) {
        this.NAME = name;
        this.TYPE = type;
    }

    /**
     *
     * @return My column name and data type as they would be put into a Create Table query
     */
    @Override
    public String toString() {
        return NAME + " " + TYPE;
    }
}
