package com.example.lab3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.google.mlkit.common.MlKitException;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.vision.digitalink.DigitalInkRecognition;
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel;
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier;
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer;
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions;

public class HandwritingDialog {

    public interface OnTextRecognizedListener {
        void onTextRecognized(String text);
    }

    private final Context context;
    private final OnTextRecognizedListener listener;
    private DigitalInkRecognizer recognizer;

    public HandwritingDialog(Context context, OnTextRecognizedListener listener) {
        this.context = context;
        this.listener = listener;
        createRecognizer();
    }

    public void show() {
        View content = LayoutInflater.from(context).inflate(R.layout.dialog_handwriting, null);
        DrawingView drawingView = content.findViewById(R.id.drawingView);
        TextView statusTextView = content.findViewById(R.id.statusTextView);

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.handwriting)
                .setView(content)
                .setPositiveButton(R.string.insert, null)
                .setNegativeButton(R.string.clear, null)
                .setNeutralButton(R.string.cancel, null)
                .create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
            drawingView.clear();
            statusTextView.setText(R.string.handwriting_hint);
        });
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(
                v -> recognize(drawingView, statusTextView, dialog));
    }

    private void createRecognizer() {
        try {
            DigitalInkRecognitionModelIdentifier identifier =
                    DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US");
            if (identifier == null) {
                return;
            }
            DigitalInkRecognitionModel model = DigitalInkRecognitionModel.builder(identifier).build();
            RemoteModelManager.getInstance().download(model, new DownloadConditions.Builder().build());
            recognizer = DigitalInkRecognition.getClient(
                    DigitalInkRecognizerOptions.builder(model).build());
        } catch (MlKitException e) {
            recognizer = null;
        }
    }

    private void recognize(DrawingView drawingView, TextView statusTextView, AlertDialog dialog) {
        if (drawingView.isEmpty()) {
            statusTextView.setText(R.string.handwriting_empty);
            return;
        }
        if (recognizer == null) {
            statusTextView.setText(R.string.handwriting_unavailable);
            return;
        }
        statusTextView.setText(R.string.handwriting_recognizing);
        recognizer.recognize(drawingView.getInk())
                .addOnSuccessListener(result -> {
                    if (result.getCandidates().isEmpty()) {
                        statusTextView.setText(R.string.handwriting_not_recognized);
                        return;
                    }
                    String text = normalize(result.getCandidates().get(0).getText());
                    if (text.isEmpty()) {
                        statusTextView.setText(R.string.handwriting_not_recognized);
                        return;
                    }
                    listener.onTextRecognized(text);
                    dialog.dismiss();
                })
                .addOnFailureListener(e -> statusTextView.setText(R.string.handwriting_model_loading));
    }

    private String normalize(String text) {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            switch (c) {
                case '-':
                case '–':
                case '—':
                    result.append('−');
                    break;
                case '*':
                case 'x':
                case 'X':
                case '·':
                    result.append('×');
                    break;
                case '/':
                    result.append('÷');
                    break;
                case '.':
                case ',':
                    result.append(',');
                    break;
                case ' ':
                case '=':
                    break;
                default:
                    result.append(c);
            }
        }
        return result.toString();
    }
}
