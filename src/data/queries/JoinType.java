package data.queries;

import data.tables.Table;

public enum JoinType {
    InnerJoin {
        @Override
        public String toString() {
            return Table.INNER_JOIN;
        }
    }
}
