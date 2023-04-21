package data.queries;

import data.tables.Column;
import data.tables.Table;

public class JoinClause {
    private final JoinType JOIN_TYPE;
    private final Table OTHER_TABLE;
    private final Column LEFT_COLUMN;
    private final Column RIGHT_COLUMN;

    public JoinClause(JoinType joinType, Table otherTable, Column leftCol, Column rightCol) {
        this.JOIN_TYPE = joinType;
        this.OTHER_TABLE = otherTable;
        this.LEFT_COLUMN = leftCol;
        this.RIGHT_COLUMN = rightCol;
    }

    @Override
    public String toString() {
        return " " + JOIN_TYPE + " " + OTHER_TABLE + " ON " + LEFT_COLUMN + "=" + RIGHT_COLUMN + " ";
    }
}
