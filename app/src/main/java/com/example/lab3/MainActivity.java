package com.example.lab3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView resultTextView;
    private EditText number_field_1, number_field_2;
    private Button multiply_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultTextView = findViewById(R.id.resultTextView);
        number_field_1 = findViewById(R.id.number_field_1);
        number_field_2 = findViewById(R.id.number_field_2);
        multiply_button = findViewById(R.id.multiply_button);

        multiply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s1 = number_field_1.getText().toString();
                String s2 = number_field_2.getText().toString();

                if (s1.isEmpty() || s1.equals("-") || s1.equals(".")) {
                    number_field_1.setError(getString(R.string.empty_error));
                    return;
                }
                if (s2.isEmpty() || s2.equals("-") || s2.equals(".")) {
                    number_field_2.setError(getString(R.string.empty_error));
                    return;
                }

                float num1 = Float.parseFloat(s1);
                float num2 = Float.parseFloat(s2);
                float res = num1 * num2;

                if (res == (long) res) {
                    resultTextView.setText(String.valueOf((long) res));
                } else {
                    resultTextView.setText(String.valueOf(res));
                }
            }
        });
    }
}
