package db;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * Parser handles all database related string parsing
 * including all DSL keywords and splitting up  parameters.
 * Evaluates query and calls the corresponding database
 * method.
 */
class Parser {

    private Database db;

    // Various common constructs, simplifies parsing.
    private static final String REST = "\\s*(.*)\\s*",
            COMMA = "\\s*,\\s*";

    // Stage 1 syntax, contains the command name.
    private static final Pattern CREATE_CMD = Pattern.compile("create table " + REST),
            LOAD_CMD = Pattern.compile("load " + REST),
            STORE_CMD = Pattern.compile("store " + REST),
            DROP_CMD = Pattern.compile("drop table " + REST),
            INSERT_CMD = Pattern.compile("insert into " + REST),
            PRINT_CMD = Pattern.compile("print " + REST),
            SELECT_CMD = Pattern.compile("select " + REST);

    // Stage 2 syntax, contains the clauses of commands.
    private static final Pattern CREATE_NEW = Pattern.compile("(\\S+)\\s+\\(\\s*(\\S+\\s+\\S+\\s*"
            + "(?:,\\s*\\S+\\s+\\S+\\s*)*)\\)"),
            SELECT_CLS = Pattern.compile("([^,]+?(?:,[^,]+?)*)\\s+from\\s+"
                    + "(\\S+\\s*(?:,\\s*\\S+\\s*)*)(?:\\s+where\\s+"
                    + "([\\w\\s+\\-*/'<>=!]+?(?:\\s+and\\s+"
                    + "[\\w\\s+\\-*/'<>=!]+?)*))?"),
            CREATE_SEL = Pattern.compile("(\\S+)\\s+as select\\s+"
                    + SELECT_CLS.pattern()),
            INSERT_CLS = Pattern.compile("(\\S+)\\s+values\\s+(.+?"
                    + "\\s*(?:,\\s*.+?\\s*)*)");


    /**
     * Constructs a parser that is linked the database db.
     */
    Parser(Database db) {
        this.db = db;
    }

    /**
     * Evaluates a query and passes it to a different method
     */
    String eval(String query) {

        query = query.replaceAll("\\s+", " ");
        Matcher m;
        if ((m = CREATE_CMD.matcher(query)).matches()) {
            return createTable(m.group(1));
        } else if ((m = LOAD_CMD.matcher(query)).matches()) {
            return loadTable(m.group(1));
        } else if ((m = STORE_CMD.matcher(query)).matches()) {
            return storeTable(m.group(1));
        } else if ((m = DROP_CMD.matcher(query)).matches()) {
            return dropTable(m.group(1));
        } else if ((m = INSERT_CMD.matcher(query)).matches()) {
            return insertRow(m.group(1));
        } else if ((m = PRINT_CMD.matcher(query)).matches()) {
            return printTable(m.group(1));
        } else if ((m = SELECT_CMD.matcher(query)).matches()) {
            return select(m.group(1));
        } else {
            return "Malformed query: " + query;
        }
    }

    /**
     * Parses an expression and passes it to another parser
     * method
     */
    private String createTable(String expr) {
        Matcher m;
        if ((m = CREATE_NEW.matcher(expr)).matches()) {
            return createNewTable(m.group(1), m.group(2).split(COMMA));
        } else if ((m = CREATE_SEL.matcher(expr)).matches()) {
            return createSelectedTable(m.group(1), m.group(2), m.group(3), m.group(4));
        } else {
            return "Malformed create: " + expr;
        }
    }

    /**
     * Creates new table with name and cols (x int, y int..etc)
     */
    private String createNewTable(String name, String[] cols) {
        ArrayList<String> colN = new ArrayList<>(cols.length);
        ArrayList<String> colT = new ArrayList<>(cols.length);
        String possibleTypes = "intstringfloat";


        for (String col : cols) {
            col = col.trim().replaceAll("\\s+"," ");
            String[] pieces = col.trim().split("\\s");
            if (pieces.length != 2) {
                return "ERROR: Malformed column definition";
            } else if (!possibleTypes.contains(pieces[1])) {
                return "ERROR: Invalid type " + pieces[1];
            }
            colN.add(pieces[0]);
            colT.add(pieces[1]);
        }

        try {
            db.createNewTable(name, colN, colT);
            return "";
        } catch (ParsingException e) {
            return e.getMessage();
        }
    }

    /**
     * Creates a new table and adds it to the database from
     * a select statement.
     */
    private String createSelectedTable(String name, String exprs, String tables, String conds) {
        try {
            Table t = selectReturnTable(exprs, tables, conds);
            t.setName(name);
            db.addTable(t);
            return "";
        } catch (ParsingException e) {
            return e.getMessage();
        }
    }

    /**
     * Drops table from database
     */
    private String dropTable(String name) {
        try {
            db.dropTable(name);
            return "";
        } catch (ParsingException e) {
            return e.getMessage();
        }
    }

    /**
     * Inserts a row into a table by expr
     */
    private String insertRow(String expr) {
        Matcher m = INSERT_CLS.matcher(expr);
        if (!m.matches()) {
            return "Malformed insert: " + expr;
        }

        String tableName = m.group(1);
        String[] values = m.group(2).split(COMMA);
        try {
            db.addRow(tableName, values);
            return "";
        } catch (ParsingException e) {
            return e.getMessage();
        }
    }

    /**
     * returns the string representation of
     * a table object.
     */
    private String printTable(String name) {
        try {
            return db.getTable(name).toString();
        } catch (ParsingException e) {
            return e.getMessage();
        }
    }

    /**
     * checks for select query correctness than passes it
     * to further helper select methods.
     */
    private String select(String expr) {
        Matcher m = SELECT_CLS.matcher(expr);
        if (!m.matches()) {
            return "Malformed select: " + expr;
        }
        return selectReturnString(m.group(1), m.group(2), m.group(3));
    }

    /**
     * handles a select query and catches exception
     */
    private String selectReturnString(String exprs, String tables, String conds) {
        try {
            return selectReturnTable(exprs, tables, conds).toString();
        } catch (ParsingException e) {
            return e.getMessage();
        }
    }

    /**
     * Returns a table based of the column expressions, joined
     * tables, and where clause conditions. Throws exceptions.
     */
    private Table selectReturnTable(String exprs, String tables, String conds)
            throws ParsingException {

        String[] names = tables.split(",");
        Stack<Table> stack = new Stack<>();
        for (int i = names.length - 1; i >= 0; i--) {
            Table t = db.getTable(names[i].trim());
            if (t == null) {
                throw new ParsingException("ERROR: No such table " + names[i].trim());
            }
            stack.push(t);
        }

        //joins - joins a stack of tables together
        Table result = JoinParser.join(stack);

        //evaluate - column expressions and where conditions
        result = ExpressionParser.parse(result, exprs, conds);
        return result;
    }

    /**
     * Stores table to file
     */
    private String storeTable(String name) {
        try {
            String data = db.getTable(name).toString();
            PrintWriter writer = new PrintWriter(name + ".tbl", "UTF-8");
            writer.print(data);
            writer.close();
            return "";
        } catch (IOException e) {
            return "ERROR: writing table to file: " + name;
        } catch (ParsingException e) {
            return e.getMessage();
        }
    }

    /**
     * Loads tableName.tbl from file, creates table and adds
     * rows to it.
     */
    private String loadTable(String tableName) {
        String filename = tableName + ".tbl";
        BufferedReader br;
        String line;

        try {
            br = new BufferedReader(new FileReader(filename));
            line = br.readLine();

            //drop table if already exists
            if (db.hasTable(tableName)) {
                db.dropTable(tableName);
            }

            //create table
            String[] cols = line.split(",");
            createNewTable(tableName, cols);

            //inserting rows
            line = br.readLine();
            while (line != null) {
                String[] values = line.split(",");
                db.addRow(tableName, values);
                line = br.readLine();
            }
            br.close();
            return "";
        } catch (FileNotFoundException e) {
            return "ERROR: Cannot open file " + filename;
        } catch (IOException e) {
            return "ERROR: Unable to parse file " + filename;
        } catch (ParsingException e) {
            return e.getMessage();
        }
    }
}
