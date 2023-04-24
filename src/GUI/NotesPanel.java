package GUI;

import data.queries.AndOr;
import data.queries.Criterion;
import data.queries.Query;
import data.queries.WhereClause;
import data.tables.AircraftTable;
import data.tables.Table;
import model.Aircraft;

import javax.swing.*;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class NotesPanel extends JPanel {

    private AircraftTable aircraftTable = AircraftTable.getInstance();

    private HashMap<Aircraft, AircraftHeader> aircraftHeaders = new HashMap<>();

    private JFrame appFrame;

    public NotesPanel(JFrame appFrame) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    public void refresh() throws SQLException {

            removeAll();
            aircraftHeaders.clear();

            Query notesQuery = new Query(aircraftTable, null,
                    new Criterion(aircraftTable.COL_ENABLED, Table.TRUE + ""), AndOr.NONE);

            for (Aircraft aircraft : aircraftTable.query(notesQuery))
                addAircraft(aircraft);

    }

    private void addAircraft(Aircraft aircraft) {
        AircraftHeader header = new AircraftHeader(appFrame, null, aircraft);
        aircraftHeaders.put(aircraft, header);
        this.add(header.getContentPane());
    }
}
