

package db;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Database class stores tables in a hash map. supports querying
 * for drop table, create table
 */
public class Database {

    private HashMap<String, Table> tables;
    private Parser parser;

    /**
     * Constructs a database object with no tables inside
     */
    public Database() {
        tables = new HashMap<>();
        parser = new Parser(this);
    }

    /**
     * Performs a query by passing string to parser
     */
    public String transact(String query) {
        return parser.eval(query.trim());
    }

    /**
     * Returns true if table exists in database, false
     * otherwise
     */
    boolean hasTable(String tableName) {
        return tables.containsKey(tableName);
    }

    /**
     * if table exists, returns table. else null
     */
    Table getTable(String tableName) throws ParsingException {
        if (hasTable(tableName)) {
            return tables.get(tableName);
        } else {
            throw new ParsingException("ERROR: No such table " + tableName);
        }
    }

    /**
     * Adds a table to the database.
     */
    void addTable(Table table) throws ParsingException {
        if (hasTable(table.getName())) {
            throw new ParsingException("ERROR: Table already exists: " + table.getName());
        }
        tables.put(table.getName(), table);
    }

    /**
     * Creates a new table if it doesn't already exist
     * and adds it to the hash map.
     */
    void createNewTable(String name, ArrayList<String> columnNames,
                                       ArrayList<String> columnTypes) throws ParsingException {
        if (hasTable(name)) {
            throw new ParsingException("ERROR:  Table " + name + " already exists");
        }
        Table t = new Table(name, columnNames, columnTypes);
        tables.put(name, t);
    }

    /**
     * Deletes the table from the database
     */
    void dropTable(String tableName) throws ParsingException {
        if (hasTable(tableName)) {
            tables.remove(tableName);
        } else {
            throw new ParsingException("ERROR:  No such table " + tableName);
        }
    }

    /**
     * adds row of values to tableName
     */
    void addRow(String tableName, String[] values) throws ParsingException {
        Table t = getTable(tableName);
        t.addRowToTable(values);
    }
}

/*

* @startuml

* testdot

* @enduml

*/
