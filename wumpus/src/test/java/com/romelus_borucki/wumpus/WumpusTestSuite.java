package com.romelus_borucki.wumpus;

import com.romelus_borucki.common.structures.Stack;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for wumpus app.
 */
public class WumpusTestSuite {

    @Test
    public void testDefaultStack() {
        final Integer one = new Integer(1);
        final Stack<Integer> s = new Stack<Integer>();

        Assert.assertEquals(s.size(), 0);
        Assert.assertNull(s.pop());
        s.push(one);
        Assert.assertEquals(s.size(), 1);
        Assert.assertEquals(s.pop(), one);
        Assert.assertEquals(s.size(), 0);
    }

    @Test
    public void testFixedStack() {
        final Integer one = new Integer(1);
        final Integer two = new Integer(2);
        final Stack<Integer> s = new Stack<Integer>(2);

        Assert.assertEquals(s.size(), 0);
        s.push(one);
        Assert.assertEquals(s.size(), 1);
        s.push(two);

        s.push(new Integer(3));
        Assert.assertEquals(s.size(), 2);

        Assert.assertEquals(s.pop(), two);
        Assert.assertEquals(s.size(), 1);
        Assert.assertEquals(s.pop(), one);
        Assert.assertEquals(s.size(), 0);
    }
}