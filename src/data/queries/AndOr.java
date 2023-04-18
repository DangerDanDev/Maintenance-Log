package data.queries;

public enum AndOr {
    AND {
        @Override
        public String toString() {
            return " AND ";
        }
    },
    OR {
        @Override
        public String toString() {
            return " OR ";
        }
    },
    NONE {
        @Override
        public String toString() {
            return " ";
        }
    }
}
