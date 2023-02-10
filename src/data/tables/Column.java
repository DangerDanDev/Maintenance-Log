package data.tables;

public class Column {
    public final String NAME;
    public final String TYPE;
    public final String CONSTRAINTS;
    public final String DEFAULTS;
    public final Table PARENT_TABLE;

    public Column(Table parent, String name, String type, String constraints, String defaults) {
        PARENT_TABLE = parent;
        NAME = PARENT_TABLE.NAME + "_" + name;
        TYPE = type;
        CONSTRAINTS = constraints;
        DEFAULTS = defaults;
    }

    public Column(Table parent, String name, String type, String constraints) {
        this(parent, name, type, constraints, "");
    }

    public Column(Table parent, String name, String type) {
        this(parent, name,type, "");
    }

    /**
     *
     * @return An SQL string with this column's definition, ie: "NAME TYPE"
     */
    public String getDefinitionSQL() {
        StringBuilder str = new StringBuilder();

        str.append(NAME + " ");
        str.append(TYPE + " ");

        if(CONSTRAINTS != "")
            str.append(CONSTRAINTS);

        if(DEFAULTS != "")
            str.append( " DEFAULTS " + DEFAULTS);

        return str.toString();
    }

    public String toString() {
        return NAME;
    }
}
