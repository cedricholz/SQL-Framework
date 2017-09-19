package db;

import java.util.ArrayList;

/**
 * Statically handles column expressions and where
 * condition expressions.
 */
class ExpressionParser {

    /**
     * Takes in a table and returns the columns in colExprs
     * and rows that match the predicate conds.
     */
    static Table parse(Table table, String colExprs, String conds) throws ParsingException {

        Table result;

        /* column expressions */
        if (!colExprs.equals("*")) {
            result = new Table("", new ArrayList<>(), new ArrayList<>());
            for (String expr : colExprs.split(",")) {
                evaluateExpression(table, result, expr);
            }
        } else {
            result = table;
        }

        /* where clause */
        if (conds == null) {
            return result;
        } else {
            for (String cond : conds.split("and")) {
                result = evaluateWhere(result, cond);
            }
            return result;
        }
    }

    /**
     * evaluates an expression on src and returns dest.
     */
    private static void evaluateExpression(Table src, Table dest, String expr)
            throws ParsingException {

        if (!expr.contains(" as ")) {
            String name = expr.trim();
            if (src.hasColumn(name)) {
                Table.copyColumn(src, dest, name);
            } else {
                throw new ParsingException("ERROR: Cannot find column " + name);
            }
        } else {
            String colName = expr.split(" as ")[1].trim();
            expr = expr.split(" as ")[0];


            String operator;
            if (expr.contains("*")) {
                operator = "\\*";
            } else if (expr.contains("+")) {
                operator = "\\+";
            } else if (expr.contains("-")) {
                operator = "-";
            } else if (expr.contains("/")) {
                operator = "/";
            } else {
                throw new ParsingException("ERROR: Invalid column Expression " + expr);
            }

            String leftOperand = expr.split(operator)[0].trim();
            String rightOperand = expr.split(operator)[1].trim();

            if (!src.hasColumn(leftOperand)) {
                throw new ParsingException("ERROR: Cannot find column " + leftOperand);
            }
            String colType = src.getTypeOfColumn(leftOperand);
            ArrayList<Element> elements = new ArrayList<>();

            if (src.hasColumn(rightOperand)) {
                //x + y
                int leftIndex = src.indexOfColumn(leftOperand);
                int rightIndex = src.indexOfColumn(rightOperand);
                for (int i = 0; i < src.getNumRows(); i++) {
                    Element e1 = src.getRow(i).getElement(leftIndex);
                    Element e2 = src.getRow(i).getElement(rightIndex);
                    Element e = Element.arithmetic(e1, e2, operator);

                    if (e.type()!= colType && e.type() != "NaN" && e.type() != "NOVALUE"){
                        colType = e.type();
                    }
                    elements.add(e);
                }
            } else {
                //x + 2
                Element literal = Element.createFromLiteral(rightOperand);
                int index = src.indexOfColumn(leftOperand);
                for (int i = 0; i < src.getNumRows(); i++) {
                    Element e1 = src.getRow(i).getElement(index);
                    Element e = Element.arithmetic(e1, literal, operator);
                    if (e.type()!= colType && e.type() != "NaN" && e.type() != "NOVALUE"){
                        colType = e.type();
                    }
                    elements.add(e);
                }
            }
            dest.addColumn(colName, colType, elements);
        }
    }

    /**
     * evaluates where expressions on src table, returns a new
     * table that meets conds.
     */
    private static Table evaluateWhere(Table src, String conds) throws ParsingException {

        Table result = new Table("", src.getColumnNames(), src.getColumnTypes());

        String operator;
        if (conds.contains(">=") || conds.contains("=>")) {
            operator = ">=";
        } else if (conds.contains("<=") || conds.contains("=<")) {
            operator = "<=";
        } else if (conds.contains(">")) {
            operator = ">";
        } else if (conds.contains("<")) {
            operator = "<";
        } else if (conds.contains("==")) {
            operator = "==";
        } else if (conds.contains("!=")) {
            operator = "!=";
        } else {
            throw new ParsingException("ERROR: Invalid where clause " + conds);
        }

        String leftOperand = conds.split(operator)[0].trim();
        String rightOperand = conds.split(operator)[1].trim();

        if (!src.hasColumn(leftOperand)) {
            throw new ParsingException("ERROR: Cannot find column " + leftOperand);
        }

        if (src.hasColumn(rightOperand)) {
            //x > y
            int leftIndex = src.indexOfColumn(leftOperand);
            int rightIndex = src.indexOfColumn(rightOperand);
            for (int i = 0; i < src.getNumRows(); i++) {
                Element e1 = src.getRow(i).getElement(leftIndex);
                Element e2 = src.getRow(i).getElement(rightIndex);
                if (Element.compare(e1, e2, operator)) {
                    result.addRowToTable(src.getRow(i));
                }
            }
        } else {
            //x > 2
            Element literal = Element.createFromLiteral(rightOperand);
            int index = src.indexOfColumn(leftOperand);
            for (int i = 0; i < src.getNumRows(); i++) {
                Element e1 = src.getRow(i).getElement(index);
                if (Element.compare(e1, literal, operator)) {
                    result.addRowToTable(src.getRow(i));
                }
            }
        }
        return result;
    }
}

