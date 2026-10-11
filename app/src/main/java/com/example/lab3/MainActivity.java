package com.example.lab3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String[] FUNCTIONS = {"sin", "cos", "tan", "ln", "log", "√"};
    private static final String OPERATORS = "+−×÷^";

    private TextView expressionTextView;
    private TextView resultTextView;
    private View extraRow;

    private String expression = "";
    private boolean degrees = true;
    private boolean justEvaluated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expressionTextView = findViewById(R.id.expressionTextView);
        resultTextView = findViewById(R.id.resultTextView);
        extraRow = findViewById(R.id.extraRow);
    }

    public void onButtonClick(View view) {
        String key = ((Button) view).getText().toString();
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
                return;
            case "▾":
            case "▴":
                toggleExtraRow((Button) view);
                return;
            case "Deg":
            case "Rad":
                degrees = !degrees;
                ((Button) view).setText(degrees ? "Deg" : "Rad");
                break;
            case "( )":
                startNewIfNeeded();
                addBracket();
                break;
            case ".":
                addDot();
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
        updateDisplay();
    }

    private void calculate() {
        if (expression.isEmpty()) {
            return;
        }
        try {
            double value = new ExpressionEvaluator(expression, degrees).evaluate();
            String text = ExpressionEvaluator.format(value);
            String previous = expression;
            expression = text.contains("E") ? "" : text;
            justEvaluated = true;
            expressionTextView.setText(text);
            resultTextView.setText(previous + " =");
        } catch (Exception e) {
            resultTextView.setText(R.string.error);
        }
    }

    private void updateDisplay() {
        expressionTextView.setText(expression);
        resultTextView.setText(preview());
    }

    private String preview() {
        if (expression.isEmpty() || justEvaluated) {
            return "";
        }
        try {
            String text = ExpressionEvaluator.format(
                    new ExpressionEvaluator(expression, degrees).evaluate());
            return text.equals(expression) ? "" : text;
        } catch (Exception e) {
            return "";
        }
    }

    private void startNewIfNeeded() {
        if (justEvaluated) {
            expression = "";
            justEvaluated = false;
        }
    }

    private void toggleExtraRow(Button button) {
        boolean show = extraRow.getVisibility() != View.VISIBLE;
        extraRow.setVisibility(show ? View.VISIBLE : View.GONE);
        button.setText(show ? "▴" : "▾");
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

    private void addDot() {
        startNewIfNeeded();
        int i = expression.length() - 1;
        while (i >= 0 && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
            if (expression.charAt(i) == '.') {
                return;
            }
            i--;
        }
        boolean afterDigit = i < expression.length() - 1;
        expression += afterDigit ? "." : "0.";
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
