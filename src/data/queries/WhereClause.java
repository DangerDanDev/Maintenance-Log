package data.queries;

import data.DatabaseObject;
import data.QueryIndexer;
import data.tables.Column;
import data.tables.DiscrepancyTable;
import data.tables.Table;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * @param <T>
 */
public class WhereClause<T extends DatabaseObject>{
    public final QueryIndexer indexer = new QueryIndexer();

    /**
     * The list of boolean criteria that this where clause checks
     */
    private ArrayList<Criterion> criteria = new ArrayList<>();

    /**
     * The And/Or (if applicable)
     */
    private ArrayList<AndOr> andOrs = new ArrayList<>();

    private String sqlString;

    public WhereClause() {

    }

    public void addCriterion(Criterion c, AndOr andOr) {
        criteria.add(c);
        andOrs.add(andOr);
    }

    public void build() {
        StringBuilder str = new StringBuilder();

        //there is no where clause if there are no criteria
        if(criteria.size() > 0) {
            for(int i = 0; i < criteria.size(); i++) {

                Criterion criterion = criteria.get(i);
                Column column = criterion.COLUMN;
                AndOr andOr = andOrs.get(i);
                indexer.index(column);

                str.append(column + "=?"  + andOr);
            }
        }

        sqlString = str.toString();
    }

    public void setValues(PreparedStatement ps) throws SQLException {
        for(int i = 0; i < criteria.size(); i++ ) {
            Criterion c = criteria.get(i);

            //TODO: Convert this to use real data types instead of just converting everything to text
            ps.setString(indexer.indexOf(c), c.VALUE);
        }
    }

    @Override
    public String toString() {
        if(sqlString == null)
            build();

        return sqlString;
    }

    public static void main(String[] args) {
        Criterion ID_EQUALS_ID = new Criterion(DiscrepancyTable.getInstance().COL_ID, "7");
        Criterion DISCREPANCY_CONTAINS_POOP = new Criterion(DiscrepancyTable.getInstance().COL_TEXT, "POOP");

        WhereClause ID_MATCHES_CONTAINS_POOP = new WhereClause();
        ID_MATCHES_CONTAINS_POOP.addCriterion(ID_EQUALS_ID, AndOr.AND);
        ID_MATCHES_CONTAINS_POOP.addCriterion(DISCREPANCY_CONTAINS_POOP, AndOr.NONE);

        System.out.println(ID_MATCHES_CONTAINS_POOP.toString());
    }
}
