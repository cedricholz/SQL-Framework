package db;

import java.util.Objects;

/**
 * Represents a Novalue data type in SQL.
 * Novalue is only equal to 0, 0.0, "" and
 * itself.
 */
public class Novalue {

    @Override
    public String toString() {
        return "NOVALUE";
    }

    /**
     * Only returns true if n is 0, 0.0, empty
     * string or another instance of NOVALUE.
     */
    @Override
    public boolean equals(Object n) {
        return n instanceof Integer && n.equals(0)
                || n instanceof Double && n.equals(0.0)
                || n instanceof String && n.equals("")
                || n instanceof Novalue;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + Objects.hashCode(this);
        return hash;
    }
}
