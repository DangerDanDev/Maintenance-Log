package data.queries;

import data.DBManager;
import data.QueryIndexer;
import data.tables.StatusTable;
import data.tables.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Query {

    /**
     * The table we are pulling from
     */
    private final Table TABLE;

    /**
     * A list of all my separate where clauses
     */
    private ArrayList<WhereClause> whereClauses = new ArrayList<>();

    /**
     * The indexer that keeps track of jdbc's "?" markers in a PerparedStatement
     */
    private QueryIndexer indexer;

    /**
     * A list of all the other tables we want to join into our query
     */
    private ArrayList<JoinClause> joinClauses = new ArrayList<>();

    /**
     * The final form, this entire query converted to an SQL text string
     */
    private String queryString;

    public Query(Table table) {
        TABLE = table;
    }

    public void build() {
        indexer = new QueryIndexer();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM " + TABLE + " ");

        if(joinClauses.size() != 0)
            for(JoinClause joinClause : joinClauses)
                sql.append(joinClause.toString());

        if(whereClauses.size() != 0) {
            sql.append(" WHERE " );

            for(WhereClause whereClause : whereClauses)
                sql.append(whereClause.toString());
        }

        queryString = sql.toString();
        System.out.println(queryString);
    }

    @Override
    public String toString() {
        if(queryString == null)
            build();

        return queryString;
    }

    public void setValues(PreparedStatement ps) throws SQLException {
        if(whereClauses.size() != 0) {
            for(WhereClause whereClause : whereClauses)
                whereClause.setValues(ps);
        }
    }

    public QueryIndexer getIndexer() {
        return this.indexer;
    }

    /**
     * Adds a where clause to the query
     * @param whereClause
     */
    public void addWhereClause(WhereClause whereClause) {
        this.whereClauses.add(whereClause);
    }

    /**
     * Wraps a single Criterion in a where clause and adds that where clause to the query
     * @param c
     * @param andOr
     */
    public void addWhereCriterion(Criterion c, AndOr andOr) {
        WhereClause whereClause = new WhereClause(this, andOr);

        whereClause.addCriterion(c, AndOr.NONE);

        addWhereClause(whereClause);
    }

    /**
     *
     * @param clause
     */
    public void addJoinClause(JoinClause clause) {
        joinClauses.add(clause);
    }

    public static void main(String[] args) {


        try(Connection c = DBManager.getConnection()) {

            DBManager.initialize();

            Table table = StatusTable.getInstance();

            Query query = new Query(table);
            System.out.println("Query: " + query.toString());

            WhereClause whereClause = new WhereClause(query);
            whereClause.addCriterion(new Criterion(StatusTable.getInstance().COL_ID, "1"), AndOr.AND);
            whereClause.addCriterion(new Criterion(StatusTable.getInstance().COL_SHOW_ON_NOTES, "SEIFJ"), AndOr.NONE);
            query.addWhereClause(whereClause);

            query.build();
            System.out.println("Query with where clause: " + query);

            try(PreparedStatement ps = DBManager.getConnection().prepareStatement(query.toString())) {

                query.setValues(ps);

            } catch (SQLException ex) {
                throw ex;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
