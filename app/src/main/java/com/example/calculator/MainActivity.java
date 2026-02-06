package com.example.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView display;
    private StringBuilder currentNumber = new StringBuilder();
    private double firstOperand = 0;
    private String operator = "";
    private boolean newOperation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        display = findViewById(R.id.display);
        setupButtons();
    }

    private void setupButtons() {
        int[] numberIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                          R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        int[] operatorIds = {R.id.btnAdd, R.id.btnSub, R.id.btnMul, R.id.btnDiv};

        for (int i = 0; i < numberIds.length; i++) {
            final int num = i;
            findViewById(numberIds[i]).setOnClickListener(v -> onNumberClick(num));
        }

        for (int i = 0; i < operatorIds.length; i++) {
            final int index = i;
            findViewById(operatorIds[i]).setOnClickListener(v -> onOperatorClick(index));
        }

        findViewById(R.id.btnClear).setOnClickListener(v -> onClearClick());
        findViewById(R.id.btnEquals).setOnClickListener(v -> onEqualsClick());
        findViewById(R.id.btnDot).setOnClickListener(v -> onDotClick());
        findViewById(R.id.btnBackspace).setOnClickListener(v -> onBackspaceClick());
    }

    private void onNumberClick(int num) {
        if (newOperation) {
            currentNumber = new StringBuilder();
            newOperation = false;
        }
        currentNumber.append(num);
        display.setText(currentNumber.toString());
    }

    private void onOperatorClick(int index) {
        if (currentNumber.length() > 0) {
            firstOperand = Double.parseDouble(currentNumber.toString());
        }
        String[] operators = {"+", "-", "×", "÷"};
        operator = operators[index];
        newOperation = true;
    }

    private void onDotClick() {
        if (currentNumber.length() == 0) {
            currentNumber.append("0");
        }
        if (!currentNumber.toString().contains(".")) {
            currentNumber.append(".");
            display.setText(currentNumber.toString());
        }
    }

    private void onBackspaceClick() {
        if (currentNumber.length() > 0) {
            currentNumber.deleteCharAt(currentNumber.length() - 1);
            display.setText(currentNumber.toString());
        }
    }

    private void onEqualsClick() {
        if (currentNumber.length() == 0 || operator.isEmpty()) return;

        double secondOperand = Double.parseDouble(currentNumber.toString());
        double result = 0;

        switch (operator) {
            case "+":
                result = firstOperand + secondOperand;
                break;
            case "-":
                result = firstOperand - secondOperand;
                break;
            case "×":
                result = firstOperand * secondOperand;
                break;
            case "÷":
                if (secondOperand != 0) {
                    result = firstOperand / secondOperand;
                } else {
                    display.setText("Error");
                    return;
                }
                break;
        }

        if (result == (long) result) {
            display.setText(String.valueOf((long) result));
        } else {
            display.setText(String.valueOf(result));
        }

        currentNumber = new StringBuilder();
        operator = "";
        newOperation = true;
    }

    private void onClearClick() {
        currentNumber = new StringBuilder();
        firstOperand = 0;
        operator = "";
        newOperation = true;
        display.setText("0");
    }
}
