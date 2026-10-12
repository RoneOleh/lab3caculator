package com.example.lab3;

import java.math.BigDecimal;
import java.math.MathContext;

public class ExpressionEvaluator {

    private final String input;
    private final boolean degrees;
    private int pos;

    public ExpressionEvaluator(String input, boolean degrees) {
        this.input = input;
        this.degrees = degrees;
    }

    public double evaluate() {
        pos = 0;
        double value = parseExpression();
        if (pos < input.length() || Double.isNaN(value) || Double.isInfinite(value)) {
            throw new ArithmeticException();
        }
        return value;
    }

    public static String format(double value) {
        BigDecimal number = new BigDecimal(value).round(new MathContext(12)).stripTrailingZeros();
        BigDecimal abs = number.abs();
        String text;
        if (number.signum() != 0 && (abs.compareTo(new BigDecimal("1E15")) >= 0
                || abs.compareTo(new BigDecimal("1E-9")) < 0)) {
            text = number.toString();
        } else {
            text = number.toPlainString();
        }
        return text.replace('-', '−').replace('.', ',');
    }

    private double parseExpression() {
        double value = parseTerm();
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (c == '+') {
                pos++;
                value += parseTerm();
            } else if (c == '−') {
                pos++;
                value -= parseTerm();
            } else {
                break;
            }
        }
        return value;
    }

    private double parseTerm() {
        double value = parseUnary();
        while (pos < input.length()) {
            char c = input.charAt(pos);
            if (c == '×') {
                pos++;
                value *= parseUnary();
            } else if (c == '÷') {
                pos++;
                value /= parseUnary();
            } else if (startsFactor(c)) {
                value *= parseUnary();
            } else {
                break;
            }
        }
        return value;
    }

    private double parseUnary() {
        if (peek('−')) {
            pos++;
            return -parseUnary();
        }
        if (peek('+')) {
            pos++;
            return parseUnary();
        }
        return parsePower();
    }

    private double parsePower() {
        double base = parsePostfix();
        if (peek('^')) {
            pos++;
            return Math.pow(base, parseUnary());
        }
        return base;
    }

    private double parsePostfix() {
        double value = parsePrimary();
        while (true) {
            if (peek('!')) {
                pos++;
                value = factorial(value);
            } else if (peek('%')) {
                pos++;
                value /= 100;
            } else {
                return value;
            }
        }
    }

    private double parsePrimary() {
        if (pos >= input.length()) {
            throw new ArithmeticException();
        }
        char c = input.charAt(pos);
        if (c == '(') {
            return parseArgument();
        }
        if (c == 'π') {
            pos++;
            return Math.PI;
        }
        if (c == 'e') {
            pos++;
            return Math.E;
        }
        if (c == '√') {
            pos++;
            return Math.sqrt(parseArgument());
        }
        if (Character.isDigit(c) || c == ',' || c == '.') {
            return parseNumber();
        }
        return parseFunction();
    }

    private double parseFunction() {
        int start = pos;
        while (pos < input.length() && Character.isLetter(input.charAt(pos))) {
            pos++;
        }
        String name = input.substring(start, pos);
        if (name.isEmpty()) {
            throw new ArithmeticException();
        }
        double arg = parseArgument();
        switch (name) {
            case "sin":
                return clean(Math.sin(angle(arg)));
            case "cos":
                return clean(Math.cos(angle(arg)));
            case "tan":
                if (degrees && Math.abs(arg % 180) == 90) {
                    throw new ArithmeticException();
                }
                return clean(Math.tan(angle(arg)));
            case "ln":
                return Math.log(arg);
            case "log":
                return Math.log10(arg);
            default:
                throw new ArithmeticException();
        }
    }

    private double parseArgument() {
        if (peek('(')) {
            pos++;
            double value = parseExpression();
            if (peek(')')) {
                pos++;
            }
            return value;
        }
        return parsePostfix();
    }

    private double parseNumber() {
        int start = pos;
        while (pos < input.length()
                && (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == ',' || input.charAt(pos) == '.')) {
            pos++;
        }
        return Double.parseDouble(input.substring(start, pos).replace(',', '.'));
    }

    private double factorial(double n) {
        if (n < 0 || n != Math.floor(n) || n > 170) {
            throw new ArithmeticException();
        }
        double result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }

    private double angle(double value) {
        return degrees ? Math.toRadians(value) : value;
    }

    private double clean(double value) {
        return Math.abs(value) < 1e-12 ? 0 : value;
    }

    private boolean peek(char c) {
        return pos < input.length() && input.charAt(pos) == c;
    }

    private boolean startsFactor(char c) {
        return Character.isLetterOrDigit(c) || c == ',' || c == '.' || c == '(' || c == '√';
    }
}
