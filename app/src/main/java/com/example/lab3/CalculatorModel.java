package com.example.lab3;

import java.util.ArrayList;
import java.util.List;

public class CalculatorModel {

    private static final String[] FUNCTIONS = {"sin", "cos", "tan", "ln", "log", "√"};
    private static final String OPERATORS = "+−×÷^";
    private static final String SEPARATOR = " = ";
    private static final int HISTORY_LIMIT = 50;

    private final List<String> history;
    private String expression = "";
    private String lastExpression = "";
    private boolean degrees = true;
    private boolean justEvaluated = false;
    private boolean error = false;

    public CalculatorModel(List<String> savedHistory) {
        history = new ArrayList<>(savedHistory);
    }

    public String getExpression() {
        return expression;
    }

    public boolean isError() {
        return error;
    }

    public boolean isDegrees() {
        return degrees;
    }

    public List<String> getHistory() {
        return new ArrayList<>(history);
    }

    public String getSecondaryText() {
        if (justEvaluated) {
            return lastExpression + " =";
        }
        return preview();
    }

    public void toggleAngleMode() {
        degrees = !degrees;
    }

    public void press(String key) {
        error = false;
        switch (key) {
            case "AC":
                expression = "";
                justEvaluated = false;
                break;
            case "⌫":
                backspace();
                break;
            case "=":
                calculate();
                break;
            case "( )":
                startNewIfNeeded();
                addBracket();
                break;
            case ",":
                addComma();
                break;
            case "+":
            case "−":
            case "×":
            case "÷":
            case "^":
                addOperator(key);
                break;
            case "%":
            case "!":
                addPostfix(key);
                break;
            case "sin":
            case "cos":
            case "tan":
            case "ln":
            case "log":
            case "√":
                startNewIfNeeded();
                expression += key + "(";
                break;
            default:
                startNewIfNeeded();
                expression += key;
        }
    }

    public void insertText(String text) {
        error = false;
        startNewIfNeeded();
        expression += text;
    }

    public void useHistoryItem(int index) {
        if (index < 0 || index >= history.size()) {
            return;
        }
        String item = history.get(index);
        int position = item.lastIndexOf(SEPARATOR);
        if (position < 0) {
            return;
        }
        error = false;
        expression = item.substring(position + SEPARATOR.length());
        justEvaluated = false;
    }

    public void clearHistory() {
        history.clear();
    }

    private void calculate() {
        if (expression.isEmpty() || justEvaluated) {
            return;
        }
        try {
            double value = new ExpressionEvaluator(expression, degrees).evaluate();
            String text = ExpressionEvaluator.format(value);
            addToHistory(expression + SEPARATOR + text);
            lastExpression = expression;
            expression = text;
            justEvaluated = true;
        } catch (Exception e) {
            error = true;
        }
    }

    private String preview() {
        if (expression.isEmpty()) {
            return "";
        }
        try {
            double value = new ExpressionEvaluator(expression, degrees).evaluate();
            String text = ExpressionEvaluator.format(value);
            return text.equals(expression) ? "" : text;
        } catch (Exception e) {
            return "";
        }
    }

    private void addToHistory(String entry) {
        history.add(0, entry);
        while (history.size() > HISTORY_LIMIT) {
            history.remove(history.size() - 1);
        }
    }

    private void startNewIfNeeded() {
        if (justEvaluated) {
            expression = "";
            justEvaluated = false;
        }
    }

    private void backspace() {
        justEvaluated = false;
        for (String function : FUNCTIONS) {
            if (expression.endsWith(function + "(")) {
                expression = expression.substring(0, expression.length() - function.length() - 1);
                return;
            }
        }
        if (!expression.isEmpty()) {
            expression = expression.substring(0, expression.length() - 1);
        }
    }

    private void addBracket() {
        int open = 0;
        int close = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(') {
                open++;
            } else if (c == ')') {
                close++;
            }
        }
        char last = expression.isEmpty() ? ' ' : expression.charAt(expression.length() - 1);
        boolean canClose = open > close
                && (Character.isLetterOrDigit(last) || last == ')' || last == '!' || last == '%');
        expression += canClose ? ")" : "(";
    }

    private void addComma() {
        startNewIfNeeded();
        int i = expression.length() - 1;
        while (i >= 0 && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == ',')) {
            if (expression.charAt(i) == ',') {
                return;
            }
            i--;
        }
        boolean afterDigit = i < expression.length() - 1;
        expression += afterDigit ? "," : "0,";
    }

    private void addOperator(String operator) {
        justEvaluated = false;
        if (expression.isEmpty()) {
            if (operator.equals("−")) {
                expression = operator;
            }
            return;
        }
        char last = expression.charAt(expression.length() - 1);
        if (last == '(') {
            if (operator.equals("−")) {
                expression += operator;
            }
            return;
        }
        if (OPERATORS.indexOf(last) >= 0) {
            int before = expression.length() - 2;
            if (before < 0 || expression.charAt(before) == '(') {
                return;
            }
            expression = expression.substring(0, expression.length() - 1) + operator;
            return;
        }
        expression += operator;
    }

    private void addPostfix(String key) {
        justEvaluated = false;
        if (expression.isEmpty()) {
            return;
        }
        char last = expression.charAt(expression.length() - 1);
        if (Character.isLetterOrDigit(last) || last == ')' || last == '!' || last == '%') {
            expression += key;
        }
    }
}
