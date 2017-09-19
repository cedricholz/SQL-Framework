package db;

import java.util.ArrayList;

/**
 * A row object holds Element data types in
 * an arrayList
 */
class Row {
    private ArrayList<Element> elements;

    /**
     * Constructs a row object
     */
    Row() {
        elements = new ArrayList<>();
    }

    private Row(ArrayList<Element> elements) {
        this.elements = elements;
    }

    /**
     * Return element in row at index
     */
    Element getElement(int index) {
        return elements.get(index);
    }

    /**
     * Appends all other elements of a row object
     * to this.row
     */
    Row combineTwoRows(Row r) {
        Row toReturn = new Row(new ArrayList<>(this.elements));
        toReturn.elements.addAll(r.elements);
        return toReturn;
    }

    /**
     * Adds and element object to the row object.
     */
    void addElement(Element e) {
        elements.add(e);
    }
}
