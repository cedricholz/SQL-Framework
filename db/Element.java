package db;

/**
 * Element class acts as wrapper object around database primitive types
 * string, float, integer, NaN, NOVALUE.
 *
 * @param <Item> (generic)
 */
class Element<Item> {
    private Item value;
    private String type;

    /**
     * Constructs an element object with value and type.
     */
    private Element(Item value, String type) {
        this.value = value;
        this.type = type;
    }

    Item value() {
        return value;
    }

    String type() {
        return type;
    }

    private boolean type(String s) {
        return type().equals(s);
    }

    /**
     * Used to see if two elements are the same type and thus
     * can be compared or evaluated in an expression.
     */
    private static boolean sameType(Element e1, Element e2) {
        return e1.type.equals(e2.type);
    }

    boolean equalElements(Element e2) {
        return this.type.equals(e2.type) && this.value.equals(e2.value);
    }

    static boolean compare(Element e1, Element e2, String operator) throws ParsingException {

        if (e1.type.equals("NaN")) {
            return !e2.type.equals("Nan");
        }

        String type1 = e1.type;
        String type2 = e2.type;
        if (type1.equals("NOVALUE") || type2.equals("NOVALUE")) {
            return false;
        }

        /*String check1 = "NaN,int,float";
        String check2 = "string";

        if (!((check1.contains(type1) && check1.contains(type2))
                || (check2.contains(type1) && check2.contains(type2)))) {
            throw new ParsingException("ERROR: Incomparable " + type1 + operator + type2);
        }*/

        if ((e1.type("string") && !e2.type("string"))
                || (!e1.type("string") && e2.type("string"))) {
            throw new ParsingException("ERROR: Incomparable " + type1 + operator + type2);
        }

        switch (operator) {
            case ">=":
                return greaterEqual(e1, e2);
            case "<=":
                return lessEqual(e1, e2);
            case "==":
                return e1.equalElements(e2);
            case "!=":
                return !e1.equalElements(e2);
            case ">":
                return greater(e1, e2);
            case "<":
                return less(e1, e2);
            default:
                throw new ParsingException("ERROR: Cannot determine operator " + operator);
        }
    }

    static Element arithmetic(Element e1, Element e2, String operator) throws ParsingException {
        if ((e1.type.equals("NaN") || e2.type.equals("NaN"))) {
            return new Element<>(new Nan(), "NaN");
        } else if (e1.type.equals("NOVALUE") && e2.type.equals("NOVALUE")) {
            return new Element<>(new Novalue(), "NOVALUE");
            //} else if (e2.type.equals("NOVALUE")) {
            //    return e1;
        } else if ((e1.type().equals("string") && !e2.type().equals("string")) || (e2.type().equals("string") && !e1.type().equals("string"))) {
            throw new ParsingException("ERROR: Invalid type comparison "
                    + e1.type + " and " + e2.type);
        } else if (!sameType(e1, e2) && !e1.type.equals("int") && !e1.type.equals("float") && !e2.type.equals("int") && !e2.type.equals("float")) {
            throw new ParsingException("ERROR: Invalid type comparison "
                    + e1.type + " and " + e2.type);
        }
        switch (operator) {
            case "\\+":
                return add(e1, e2);
            case "-":
                return sub(e1, e2);
            case "\\*":
                return mult(e1, e2);
            case "/":
                return div(e1, e2);
            default:
                throw new ParsingException("ERROR: Cannot determine operator " + operator);
        }
    }

    private static Element add(Element e1, Element e2) throws ParsingException {

        switch (e1.type) {
            case "string":
                String s1 = e1.value.toString().replace("\'", "");
                String s2 = e2.value.toString().replace("\'", "");
                String s = "'" + s1 + s2 + "'";
                return new Element<>(s, "string");
            case "int":
                if (e2.type.equals("NOVALUE")) {
                    return e1;
                }
                if (e2.type.equals("int")) {
                    int v = ((Integer) e1.value) + ((Integer) e2.value);
                    return new Element<>(v, "int");
                } else {
                    int i = (Integer) e1.value;
                    double d = (Double) e2.value;
                    return new Element<>(d + i, "float");
                }
            case "float":
                if (e2.type.equals("NOVALUE")) {
                    return e1;
                }
                if (e2.type().equals("float")) {
                    Double d = ((Double) e1.value) + ((Double) e2.value);
                    return new Element<>(d, "float");
                } else {
                    Double d = (Double) e1.value;
                    int i = (Integer) e2.value;
                    return new Element<>(d + i, "float");
                }
            case "NOVALUE":
                return e2;
            default:
                throw new ParsingException("ERROR: Arithmetic Error: Cannot add elements");
        }
    }

    private static Element sub(Element e1, Element e2) throws ParsingException {
        switch (e1.type) {
            case "string":
                throw new ParsingException("ERROR: Arithmetic Error: Cannot subtract strings");
            case "int":
                if (e2.type.equals("int")) {
                    int v = ((Integer) e1.value) - ((Integer) e2.value);
                    return new Element<>(v, "int");
                } else {
                    if (e2.type.equals("NOVALUE")) {
                        return e1;
                    }
                    int i = (Integer) e1.value;
                    double d = (Double) e2.value;
                    return new Element<>(i - d, "float");
                }
            case "float":
                if (e2.type().equals("float")) {
                    Double d = ((Double) e1.value) - ((Double) e2.value);
                    return new Element<>(d, "float");
                } else {
                    if (e2.type().equals("NOVALUE")) {
                        return e1;
                    }
                    Double d = (Double) e1.value;
                    int i = (Integer) e2.value;
                    return new Element<>(d - i, "float");
                }
            case "NOVALUE":
                if (e2.type().equals("float")) {
                    return new Element<>(0.0 - (Double) e2.value, "float");
                }
                if (e2.type().equals("int")) {
                    return new Element<>(0 - (Integer) e2.value, "int");
                }
            default:
                throw new ParsingException("ERROR: Arithmetic Error: Cannot subtract elements");
        }
    }

    private static Element mult(Element e1, Element e2) throws ParsingException {
        switch (e1.type) {
            case "string":
                throw new ParsingException("ERROR: Arithmetic Error: Cannot multiply strings");
            case "int":
                if (e2.type().equals("NOVALUE")) {
                    return new Element<>(0, "int");
                }
                if (e2.type.equals("int")) {
                    int v = ((Integer) e1.value) * ((Integer) e2.value);
                    return new Element<>(v, "int");
                } else {
                    int i = (Integer) e1.value;
                    double d = (Double) e2.value;
                    return new Element<>(d * i, "float");
                }
            case "float":
                if (e2.type().equals("NOVALUE")) {
                    return new Element<>(0.0, "float");
                }
                if (e2.type().equals("float")) {
                    Double d = ((Double) e1.value) * ((Double) e2.value);
                    return new Element<>(d, "float");
                } else {
                    Double d = (Double) e1.value;
                    int i = (Integer) e2.value;
                    return new Element<>(d * i, "float");
                }
            case "NOVALUE":
                if (e2.type().equals("float")) {
                    return new Element<>(0.0, "float");
                }
                if (e2.type().equals("int")) {
                    return new Element<>(0, "int");
                }
            default:
                throw new ParsingException("ERROR: Arithmetic Error: Cannot multiply elements");
        }
    }

    private static Element div(Element e1, Element e2) throws ParsingException {
        switch (e1.type) {
            case "string":
                throw new ParsingException("ERROR: Arithmetic Error: Cannot divide strings");
            case "int":
                try {
                    if (e2.type == "NOVALUE") {
                        return new Element<>(new Nan(), "NaN");
                    }
                    if (e2.type.equals("int")) {
                        int v = ((Integer) e1.value) / ((Integer) e2.value);
                        return new Element<>(v, "int");
                    } else {
                        Double d = (Integer) e1.value / (Double) e2.value;
                        if (d.isInfinite()) {
                            return new Element<>(new Nan(), "NaN");
                        } else {
                            return new Element<>(d, "float");
                        }
                    }
                } catch (ArithmeticException e) {
                    return new Element<>(new Nan(), "NaN");
                }
            case "float":
                Double d = 0.0;
                if (e2.type().equals("float")) {
                    d = ((Double) e1.value) / ((Double) e2.value);
                }
                if (e2.type == "NOVALUE") {
                    return new Element<>(new Nan(), "NaN");
                }
                if (e2.type == "NaN") {
                    return e2;
                }
                if (e2.type().equals("int")) {
                    d = (Double) e1.value / (Integer) e2.value;
                }
                if (d.isInfinite()) {
                    return new Element<>(new Nan(), "NaN");
                } else {
                    return new Element<>(d, "float");
                }
            case "NOVALUE":
                if (e2.type() == "float") {
                    return new Element<>(0.0, "float");
                }
                if (e2.type() == "int") {
                    return new Element<>(0, "int");
                }
                if (e2.type() == "NaN") {
                    return new Element<>(new Nan(), "NaN");

                }
            default:
                throw new ParsingException("ERROR: Arithmetic Error: Cannot divide elements");
        }
    }

    static Element createFromLiteral(String s) throws ParsingException {
        String type;
        if (s.contains("'")) {
            type = "string";
        } else if (s.contains(".")) {
            type = "float";
        } else if (s.contains("NOVALUE")) {
            type = "NOVALUE";
        } else if (s.contains("NaN")) {
            type = "NaN";
        } else if (isInt(s)) {
            type = "int";
        } else {
            throw new ParsingException("ERROR: Cannot determine type " + s);
        }

        switch (type) {
            case "int":
                return new Element<>(Integer.parseInt(s), type);
            case "string":
                return new Element<>(s, type);
            case "float":
                return new Element<>(Double.parseDouble(s), type);
            case "NaN":
                return new Element<>(new Nan(), type);
            case "NOVALUE":
                return new Element<>(new Novalue(), type);
            default:
                throw new ParsingException("ERROR: Cannot determine type " + s);
        }
    }

    /**
     * Helper function to determine if a
     * string is an integer.
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static boolean isInt(String str) {
        try {
            Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    private static boolean greater(Element e1, Element e2) throws ParsingException {
        switch (e1.type) {
            case "string":
                return e1.value.toString().compareTo(e2.value.toString()) > 0;
            case "int":
                return ((Integer) e1.value) > ((Integer) e2.value);
            case "float":
                return ((Double) e1.value) > ((Double) e2.value);
            default:
                throw new ParsingException("ERROR: Comparison error");
        }
    }

    private static boolean less(Element e1, Element e2) throws ParsingException {
        switch (e1.type) {
            case "string":
                return e1.value.toString().compareTo(e2.value.toString()) < 0;
            case "int":
                return ((Integer) e1.value) < ((Integer) e2.value);
            case "float":
                if (e2.type("int")) {
                    return ((Double) e1.value) < ((Integer) e2.value);
                } else {
                    return ((Double) e1.value) < ((Double) e2.value);
                }
            default:
                throw new ParsingException("ERROR: Comparison error");
        }
    }

    private static boolean greaterEqual(Element e1, Element e2) throws ParsingException {
        switch (e1.type) {
            case "string":
                return e1.value.toString().compareTo(e2.value.toString()) >= 0;
            case "int":
                return ((Integer) e1.value) >= ((Integer) e2.value);
            case "float":
                return ((Double) e1.value) >= ((Double) e2.value);
            default:
                throw new ParsingException("ERROR: Comparison error");
        }
    }

    private static boolean lessEqual(Element e1, Element e2) throws ParsingException {
        switch (e1.type) {
            case "string":
                return e1.value.toString().compareTo(e2.value.toString()) <= 0;
            case "int":
                return ((Integer) e1.value) <= ((Integer) e2.value);
            case "float":
                return ((Double) e1.value) <= ((Double) e2.value);
            default:
                throw new ParsingException("ERROR: Comparison error");
        }
    }
}
