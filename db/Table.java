package db;

import java.util.ArrayList;

/**
 * Table object stores and manipulates row objects
 */
public class Table {

    private ArrayList<Row> rows = new ArrayList<>();
    private String tableName;
    private ArrayList<String> columnNames;
    private ArrayList<String> columnTypes;

    /**
     * Creates a table object and initializes it.
     */
    Table(String tableName, ArrayList<String> columnNames, ArrayList<String> columnTypes) {
        this.tableName = tableName;
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
    }

    /**
     * Adds a column of data by adding a new name to the columnNames
     * adding a type to the ColumnTypes, then adding a new element
     * object to every row.
     */
    void addColumn(String name, String type, ArrayList<Element> values) {
        columnNames.add(name);
        columnTypes.add(type);
        for (int i = 0; i < values.size(); i++) {
            Row r;
            if (i < getNumRows()) {
                r = rows.get(i);
                r.addElement(values.get(i));
            } else {
                r = new Row();
                r.addElement(values.get(i));
                rows.add(r);
            }
        }
    }

    /**
     * Adds a row to the table by creating elements and
     * appending them to a row object.
     */
    void addRowToTable(String[] values) throws ParsingException {
        if (values.length != columnNames.size()) {
            throw new ParsingException("ERROR: Incorrect amount of values to insert into row");
        }

        Row r = new Row();
        for (int i = 0; i < values.length; i++) {
            Element e = Element.createFromLiteral(values[i]);
            String type = e.type();
            if (type.equals("NOVALUE") || type.equals("NaN")
                    || type.equals(columnTypes.get(i))) {
                r.addElement(e);
            } else {
                throw new ParsingException("ERROR: Invalid type insertion " + values[i]
                + " expecting " + columnTypes.get(i));
            }
        }
        rows.add(r);
    }

    /**
     * Adds a row object r to the table data structure.
     */
    void addRowToTable(Row r) {
        this.rows.add(r);
    }

    /**
     * Returns the table name.
     */
    String getName() {
        return tableName;
    }

    /**
     * Sets the table's string name
     */
    void setName(String name) {
        tableName = name;
    }

    /**
     * returns true if column exists in table
     */
    boolean hasColumn(String colName) {
        for (String columnName : columnNames) {
            if (columnName.equals(colName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * returns index of column by column name
     *
     * @param colName - string
     * @return index or -1 if DNE
     */
    int indexOfColumn(String colName) {
        for (int i = 0; i < columnNames.size(); i++) {
            if (columnNames.get(i).equals(colName)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * gets the type string of the column which
     * is named colName
     */
    String getTypeOfColumn(String colName) {
        return columnTypes.get(indexOfColumn(colName));
    }

    /**
     * gets row object at index in table.
     */
    Row getRow(int index) {
        return rows.get(index);
    }

    /**
     * Returns the number of elements in each row
     */
    int getNumCols() {
        return columnNames.size();
    }

    /**
     * Returns the number of rows in the table
     */
    int getNumRows() {
        return rows.size();
    }

    /**
     * returns a string array of column names
     */
    ArrayList<String> getColumnNames() {
        return columnNames;
    }

    /**
     * returns a string array of column types
     */
    ArrayList<String> getColumnTypes() {
        return columnTypes;
    }

    /**
     * Overrides toString and returns string representation of
     * table object.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(400);
        String sep = "";

        ArrayList<String> names = getColumnNames();
        ArrayList<String> types = getColumnTypes();

        for (int i = 0; i < getNumCols(); i++) {
            sb.append(sep);
            sb.append(names.get(i));
            sb.append(" ");
            sb.append(types.get(i));
            sep = ",";
        }

        for (int i = 0; i < getNumRows(); i++) {
            if (i == 0) {
                sb.append("\n");
            }
            sep = "";
            Row r = getRow(i);
            for (int j = 0; j < getNumCols(); j++) {
                sb.append(sep);
                Element e = r.getElement(j);
                if (e.type().equals("float")) {
                    sb.append(String.format("%.3f", (Double) e.value()));
                } else {
                    sb.append(e.value());
                }
                sep = ",";
            }
            if (i != getNumRows() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }


    /**
     * Copies all the column data from src.columnName to
     * dest.
     */
    static void copyColumn(Table src, Table dest, String columnName) {
        int index = src.indexOfColumn(columnName);
        ArrayList<Element> elements = new ArrayList<>();
        for (int r = 0; r < src.getNumRows(); r++) {
            elements.add(src.getRow(r).getElement(index));
        }
        dest.addColumn(src.getColumnNames().get(index), src.getColumnTypes().get(index), elements);
    }
}
