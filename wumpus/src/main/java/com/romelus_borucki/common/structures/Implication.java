package com.romelus_borucki.common.structures;

/**
 * Represents a logical implication structure.
 *
 * @author romelus
 */
public class Implication<T> {
    /**
     *
     */
    private T implies;
    /**
     *
     */
    private Operator operator;
    /**
     * Logical operators.
     */
    public enum Operator {
        AND, OR, NONE
    }

    /**
     * Default constructor.
     *
     * @param operator the logical operator
     * @param implication the implied result
     */
    public Implication(final Operator operator, final T implication) {
        this.operator = operator;
        this.implies = implication;
    }

    /**
     * Getter for implication.
     *
     * @return the implication
     */
    public T getImplies() {
        return implies;
    }

    /**
     * Getter for the operator.
     *
     * @return the operator
     */
    public Operator getOperator() {
        return operator;
    }
}