package db;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Manages the operations associated with select
 * statements in a query.
 */
class JoinParser {

    /**
     * Recursively joins two tables at a time and
     * eventually returns one completely joined table.
     */
    static Table join(Stack<Table> tables) {
        if (tables.size() == 1) {
            return tables.pop();
        } else {
            Table firstTwo = join(tables.pop(), tables.pop());
            tables.push(firstTwo);
            return join(tables);
        }
    }

    /**
     * Joins the tables t1 and t2
     */
    private static Table join(Table t1, Table t2) {
        ArrayList<String> shared = sharedColumns(t1, t2);
        Table joined = permutationTable(t1, t2);

        if (shared.size() == 0) {
            return joined;
        } else {
            //remove duplicate rows
            Table sharedRowsTable = new Table("", joined.getColumnNames(), joined.getColumnTypes());
            boolean allShared = true;
            for (int row = 0; row < joined.getNumRows(); row++) {
                for (String aShared : shared) {
                    int index1 = t1.indexOfColumn(aShared);
                    int index2 = t1.getNumCols() + t2.indexOfColumn(aShared);

                    if (!joined.getRow(row).getElement(index1)
                            .equalElements(joined.getRow(row).getElement(index2))) {
                        allShared = false;
                    }
                }
                if (allShared) {
                    sharedRowsTable.addRowToTable(joined.getRow(row));
                }
                allShared = true;
            }
            //add shared columns to finalTable
            joined = new Table("", new ArrayList<>(), new ArrayList<>());
            for (String name : shared) {
                Table.copyColumn(sharedRowsTable, joined, name);
            }
            //add non-shared columns to finalTable
            for (int i = 0; i < sharedRowsTable.getNumCols(); i++) {
                String name = sharedRowsTable.getColumnNames().get(i);
                if (shared.indexOf(name) == -1) {
                    Table.copyColumn(sharedRowsTable, joined, name);
                }
            }
            return joined;
        }
    }
    /**
     * Returns all permutations of t1 and t2 tables.
     */
    private static Table permutationTable(Table t1, Table t2) {
        ArrayList<String> colN = new ArrayList<>(t1.getNumCols() + t2.getNumCols());
        ArrayList<String> colT = new ArrayList<>(t1.getNumCols() + t2.getNumCols());
        for (int i = 0; i < t1.getColumnNames().size(); i++) {
            colN.add(t1.getColumnNames().get(i));
            colT.add(t1.getColumnTypes().get(i));
        }
        for (int i = 0; i < t2.getColumnNames().size(); i++) {
            colN.add(t2.getColumnNames().get(i));
            colT.add(t2.getColumnTypes().get(i));
        }
        Table permutate = new Table("", colN, colT);

        //all combinations of rows
        for (int i = 0; i < t1.getNumRows(); i++) {
            for (int j = 0; j < t2.getNumRows(); j++) {
                Row r = t1.getRow(i).combineTwoRows(t2.getRow(j));
                permutate.addRowToTable(r);
            }
        }
        return permutate;
    }
    /**
     * Returns an arrayList of the names of columns that are shared
     * between table t1, and table t2
     */
    private static ArrayList<String> sharedColumns(Table t1, Table t2) {
        ArrayList<String> shared = new ArrayList<>(Math.max(t1.getNumCols(), t2.getNumCols()));
        for (String i : t1.getColumnNames()) {
            for (String j : t2.getColumnNames()) {
                if (i.equals(j)) {
                    shared.add(i);
                }
            }
        }
        return shared;
    }
}


