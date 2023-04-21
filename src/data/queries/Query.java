package data.queries;

import GUI.AppFrame;
import data.DBManager;
import data.QueryIndexer;
import data.tables.DiscrepancyTable;
import data.tables.StatusTable;
import data.tables.Table;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Query {

    private final Table TABLE;
    private WhereClause whereClause;
    private QueryIndexer indexer;

    private String sqlStr;

    public Query(Table table) {
        TABLE = table;
    }

    public void build() {
        indexer = new QueryIndexer();
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT * FROM " + TABLE + " ");

        if(whereClause != null)
            sql.append(" WHERE " + whereClause + " ");

        sqlStr = sql.toString();
    }

    @Override
    public String toString() {
        if(sqlStr == null)
            build();

        return sqlStr;
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
