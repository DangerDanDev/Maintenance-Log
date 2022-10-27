package data.Tables;

import data.Column;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class AircraftTable extends Table {

    public static final String TEXT = Table.TEXT;
    public static final String INTEGER = Table.INTEGER;
    public static final String REAL = Table.REAL;

    public static final String _NAME = "Aircraft";

    public static final Column COL_TAIL_NUM = new Column("tail_number", TEXT, NOT_NULL + UNIQUE);
    public static final Column COL_KEY_TYPE_1 = new Column("key_type_1", TEXT, "");

    private static final AircraftTable aircraftTable = new AircraftTable();
    public static AircraftTable get() { return aircraftTable; }

    protected AircraftTable() {
        super();

        addColumn(COL_TAIL_NUM);
        addColumn(COL_KEY_TYPE_1);
    }

    @Override
    public String getName() {
        return "Aircraft";
    }

    public static void populateComboBoxWithAllTailNums(JComboBox cb, Connection conn) throws  SQLException{
        ArrayList<String> tailNums = get().getAllTailNumbers(conn);

        cb.removeAllItems();

        for(String tailNum : tailNums)
            cb.addItem(tailNum);

        cb.revalidate();
    }


    public ArrayList<String> getAllTailNumbers(Connection conn) throws SQLException {
        ArrayList<String> tailNums = new ArrayList<>();

        try (PreparedStatement statement = conn.prepareStatement("SELECT * FROM " + getName())) {
            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    String tailNum = rs.getString(COL_TAIL_NUM.NAME);
                    tailNums.add(tailNum);
                }
                System.out.println("Tail numbers returned from database: " + tailNums.size());
                return tailNums;

            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
                throw ex;
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
            throw ex;
        }
    }

}
