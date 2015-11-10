package com.romelus_borucki.common.structures;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Represents a logical implication structure.
 *
 * @author romelus
 */
public class Implication<T> {
    /**
     * The suggested truths.
     */
    private List<T> implies = new ArrayList<>();
    /**
     * The operation to run on the imply.
     */
    private Operator operator;
    /**
     * Logical operators.
     */
    public enum Operator {
        AND, NONE
    }

    /**
     * Default constructor.
     *
     * @param operator the logical operator
     * @param implication the implied results
     */
    public Implication(final Operator operator, final T ...implication) {
        this.operator = operator;
        this.implies = Arrays.asList(implication);
    }

    /**
     * Getter for implications.
     *
     * @return the implications
     */
    public List<T> getImplies() {
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