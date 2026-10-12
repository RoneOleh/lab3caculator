package com.example.lab3;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class CalculatorModelTest {

    private CalculatorModel model;

    @Before
    public void setUp() {
        model = new CalculatorModel(new ArrayList<>());
    }

    private void press(String... keys) {
        for (String key : keys) {
            model.press(key);
        }
    }

    @Test
    public void previewAndEquals() {
        press("2", "+", "3");
        assertEquals("5", model.getSecondaryText());
        press("=");
        assertEquals("5", model.getExpression());
        assertEquals("2+3 =", model.getSecondaryText());
        assertEquals("2+3 = 5", model.getHistory().get(0));
    }

    @Test
    public void digitAfterResultStartsNewExpression() {
        press("2", "+", "3", "=", "7");
        assertEquals("7", model.getExpression());
    }

    @Test
    public void operatorAfterResultContinuesExpression() {
        press("2", "+", "3", "=", "×", "2");
        assertEquals("5×2", model.getExpression());
    }

    @Test
    public void lastOperatorIsReplaced() {
        press("5", "×", "+");
        assertEquals("5+", model.getExpression());
    }

    @Test
    public void bracketsOpenAndClose() {
        press("( )", "2", "+", "3", "( )");
        assertEquals("(2+3)", model.getExpression());
    }

    @Test
    public void onlyOneDotInNumber() {
        press("1", ".", "5", ".", "2");
        assertEquals("1.52", model.getExpression());
    }

    @Test
    public void dotAtStartAddsZero() {
        press(".");
        assertEquals("0.", model.getExpression());
    }

    @Test
    public void backspaceRemovesWholeFunction() {
        press("sin", "3", "0");
        assertEquals("sin(30", model.getExpression());
        press("⌫", "⌫", "⌫");
        assertEquals("", model.getExpression());
    }

    @Test
    public void clearResetsExpression() {
        press("1", "2", "AC");
        assertEquals("", model.getExpression());
    }

    @Test
    public void divisionByZeroSetsError() {
        press("1", "÷", "0", "=");
        assertTrue(model.isError());
        assertEquals("1÷0", model.getExpression());
        press("⌫");
        assertFalse(model.isError());
    }

    @Test
    public void historyItemReplacesExpression() {
        press("6", "×", "7", "=", "AC");
        model.useHistoryItem(0);
        assertEquals("42", model.getExpression());
    }

    @Test
    public void historyCanBeCleared() {
        press("1", "+", "1", "=");
        model.clearHistory();
        assertTrue(model.getHistory().isEmpty());
    }

    @Test
    public void angleModeToggle() {
        assertTrue(model.isDegrees());
        model.toggleAngleMode();
        assertFalse(model.isDegrees());
    }
}
