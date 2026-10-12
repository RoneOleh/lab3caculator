package com.example.lab3;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView expressionTextView;
    private TextView resultTextView;
    private TextView modeTextView;
    private View extraRow;

    private CalculatorModel model;
    private HistoryStorage historyStorage;
    private HandwritingDialog handwritingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        expressionTextView = findViewById(R.id.expressionTextView);
        resultTextView = findViewById(R.id.resultTextView);
        modeTextView = findViewById(R.id.modeTextView);
        extraRow = findViewById(R.id.extraRow);

        historyStorage = new HistoryStorage(this);
        model = new CalculatorModel(historyStorage.load());
        handwritingDialog = new HandwritingDialog(this, text -> {
            model.insertText(text);
            updateScreen();
        });

        updateScreen();
    }

    public void onButtonClick(View view) {
        Button button = (Button) view;
        String key = button.getText().toString();

        switch (key) {
            case "▾":
            case "▴":
                toggleExtraRow(button);
                return;
            case "Deg":
            case "Rad":
                model.toggleAngleMode();
                button.setText(model.isDegrees() ? "Deg" : "Rad");
                break;
            default:
                model.press(key);
        }

        if (key.equals("=")) {
            historyStorage.save(model.getHistory());
        }
        updateScreen();
    }

    public void onHistoryClick(View view) {
        List<String> history = model.getHistory();
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setTitle(R.string.history);

        if (history.isEmpty()) {
            builder.setMessage(R.string.history_empty);
        } else {
            builder.setItems(history.toArray(new String[0]), (dialog, which) -> {
                model.useHistoryItem(which);
                updateScreen();
            });
            builder.setNeutralButton(R.string.clear, (dialog, which) -> {
                model.clearHistory();
                historyStorage.save(model.getHistory());
            });
        }

        builder.setPositiveButton(R.string.close, null);
        builder.show();
    }

    public void onHandwritingClick(View view) {
        handwritingDialog.show();
    }

    private void toggleExtraRow(Button button) {
        boolean show = extraRow.getVisibility() != View.VISIBLE;
        extraRow.setVisibility(show ? View.VISIBLE : View.GONE);
        button.setText(show ? "▴" : "▾");
    }

    private void updateScreen() {
        expressionTextView.setText(model.getExpression());
        modeTextView.setText(model.isDegrees() ? R.string.angle_degrees : R.string.angle_radians);

        if (model.isError()) {
            resultTextView.setText(R.string.error);
            resultTextView.setTextColor(getColor(R.color.tg_error));
        } else {
            resultTextView.setText(model.getSecondaryText());
            resultTextView.setTextColor(getColor(R.color.tg_secondary));
        }
    }
}
