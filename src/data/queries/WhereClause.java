package data.queries;

import data.DatabaseObject;
import data.QueryIndexer;
import data.tables.Column;
import data.tables.DiscrepancyTable;
import data.tables.StatusTable;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @param <T>
 */
public class WhereClause<T extends DatabaseObject>{

    /**
     * The list of boolean criteria that this where clause checks
     */
    private ArrayList<Criterion> criteria = new ArrayList<>();

    /**
     * Returns the query object at a specific index
     * @param index
     * @return
     */
    public Criterion getCriterion(int index) { return criteria.get(index);}

    /**
     * The query that encapsulates me and the other query data
     */
    public final Query QUERY;

    /**
     * The And/Or (if applicable)
     */
    private ArrayList<AndOr> andOrs = new ArrayList<>();

    private String sqlString;

    public WhereClause(Query query) {
        this.QUERY = query;
    }


    public void addCriterion(Criterion c, AndOr andOr) {
        criteria.add(c);
        andOrs.add(andOr);
    }

    /**
     * Builds the Where clause, surrounds it with parentheses, and adds "AND" or "OR" if applicable
     */
    public void build() {
        StringBuilder str = new StringBuilder();

        str.append(" ( ");

        for(int i = 0; i < criteria.size(); i++) {

            Criterion criterion = criteria.get(i);
            Column column = criterion.COLUMN;
            AndOr andOr = andOrs.get(i);
            getIndexer().index(column);

            str.append(column + "=?"  + andOr);
        }

        str.append(" ) ");

        sqlString = str.toString();
        System.out.println(sqlString);
    }

    public void setValues(PreparedStatement ps) throws SQLException {
        for(int i = 0; i < criteria.size(); i++ ) {
            Criterion criterion = criteria.get(i);

            //TODO: Convert this to use real data types instead of just converting everything to text
            ps.setString(getIndexer().indexOf(criterion), criterion.VALUE);
        }
    }

    @Override
    public String toString() {
        if(sqlString == null)
            build();

        return sqlString;
    }

    public QueryIndexer getIndexer() {
        return QUERY.getIndexer();
    }

    public static void main(String[] args) {
        Criterion ID_EQUALS_ID = new Criterion(DiscrepancyTable.getInstance().COL_ID, "7");
        Criterion DISCREPANCY_CONTAINS_POOP = new Criterion(DiscrepancyTable.getInstance().COL_TEXT, "POOP");

        Query query = new Query(StatusTable.getInstance());

        WhereClause ID_MATCHES_CONTAINS_POOP = new WhereClause(query);
        ID_MATCHES_CONTAINS_POOP.addCriterion(ID_EQUALS_ID, AndOr.AND);
        ID_MATCHES_CONTAINS_POOP.addCriterion(DISCREPANCY_CONTAINS_POOP, AndOr.NONE);

        System.out.println(ID_MATCHES_CONTAINS_POOP.toString());
        System.out.println(ID_MATCHES_CONTAINS_POOP.getCriterion(0).VALUE);
        System.out.println(ID_MATCHES_CONTAINS_POOP.getCriterion(1).VALUE);
    }
}
