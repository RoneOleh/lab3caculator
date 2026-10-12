package com.example.lab3;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ExpressionEvaluatorTest {

    private double calc(String expression) {
        return new ExpressionEvaluator(expression, true).evaluate();
    }

    @Test
    public void priorityOfOperations() {
        assertEquals(14.0, calc("2+3×4"), 1e-9);
        assertEquals(20.0, calc("(2+3)×4"), 1e-9);
    }

    @Test
    public void powerIsRightAssociative() {
        assertEquals(512.0, calc("2^3^2"), 1e-9);
    }

    @Test
    public void unaryMinus() {
        assertEquals(-4.0, calc("−2^2"), 1e-9);
        assertEquals(-15.0, calc("5×−3"), 1e-9);
    }

    @Test
    public void factorialAndPercent() {
        assertEquals(120.0, calc("5!"), 1e-9);
        assertEquals(0.5, calc("50%"), 1e-9);
    }

    @Test
    public void functions() {
        assertEquals(4.0, calc("√(16)"), 1e-9);
        assertEquals(0.5, calc("sin(30)"), 1e-9);
        assertEquals(0.0, calc("cos(90)"), 1e-9);
        assertEquals(3.0, calc("log(1000)"), 1e-9);
        assertEquals(1.0, calc("ln(e)"), 1e-9);
    }

    @Test
    public void radiansMode() {
        assertEquals(1.0, new ExpressionEvaluator("sin(π÷2)", false).evaluate(), 1e-9);
    }

    @Test
    public void implicitMultiplication() {
        assertEquals(6.0, calc("2(3)"), 1e-9);
        assertEquals(2 * Math.PI, calc("2π"), 1e-9);
    }

    @Test
    public void missingClosingBracketIsAllowed() {
        assertEquals(3.0, calc("√(9"), 1e-9);
    }

    @Test(expected = ArithmeticException.class)
    public void divisionByZero() {
        calc("1÷0");
    }

    @Test(expected = ArithmeticException.class)
    public void incompleteExpression() {
        calc("2+");
    }

    @Test(expected = ArithmeticException.class)
    public void tooBigFactorial() {
        calc("171!");
    }

    @Test
    public void formatting() {
        assertEquals("0.3", ExpressionEvaluator.format(0.1 + 0.2));
        assertEquals("−5", ExpressionEvaluator.format(-5.0));
        assertEquals("100", ExpressionEvaluator.format(100.0));
    }
}
