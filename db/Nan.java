package db;

/**
 * Represents a NaN data type in a
 * SQL database. The result of division by
 * zero. NaN
 */
public class Nan {

    @Override
    public String toString() {
        return "NaN";
    }

    /**
     * Only returns true if compared with another
     * instance of NaN
     */
    @Override
    public boolean equals(Object n) {
        return n instanceof Nan;
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

