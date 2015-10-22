package com.romelus_borucki.common.structures;

/**
 * Represents a logical implication structure.
 *
 * @author romelus
 */
public class Implication<T> {
    /**
     * Logical operators.
     */
    public enum Operator {
        AND, OR
    }

    /**
     * Default constructor.
     *
     * @param operator the logical operator
     * @param implication the implied result
     */
    public Implication(final Operator operator, final T implication) {

    }
}