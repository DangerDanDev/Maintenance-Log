package data.queries;

import data.DBManager;
import data.QueryIndexer;
import data.tables.StatusTable;
import data.tables.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Query {

    private final Table TABLE;
    private WhereClause whereClause;
    private QueryIndexer indexer;

    private JoinClause joinClause;

    private String queryString;

    public Query(Table table) {
        TABLE = table;
    }

    public void build() {
        indexer = new QueryIndexer();

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM " + TABLE + " ");

        if(joinClause != null)
            sql.append(joinClause.toString());

        if(whereClause != null)
            sql.append(" " + whereClause + " ");

        queryString = sql.toString();
    }

    @Override
    public String toString() {
        if(queryString == null)
            build();

        return queryString;
    }

    public void setValues(PreparedStatement ps) throws SQLException {
        if(getWhereClause() != null ){
            getWhereClause().setValues(ps);
        }
    }

    public QueryIndexer getIndexer() {
        return this.indexer;
    }

    public WhereClause getWhereClause() {
        return whereClause;
    }

    public void setWhereClause(WhereClause whereClause) {
        this.whereClause = whereClause;
    }

    public void addWhereCriterion(Criterion c, AndOr andOr) {
        if(getWhereClause() == null)
            setWhereClause(new WhereClause(this));

        getWhereClause().addCriterion(c, andOr);
    }

    public void addJoinClause(JoinClause clause) {
        this.joinClause = clause;
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
            query.setWhereClause(whereClause);

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
