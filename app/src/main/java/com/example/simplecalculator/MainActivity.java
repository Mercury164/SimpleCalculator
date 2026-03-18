package com.example.simplecalculator;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.simplecalculator.databinding.ActivityMainBinding;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final StringBuilder currentInput = new StringBuilder();
    private double firstValue = 0.0;
    private String pendingOperation = "";
    private boolean resetInputOnNextDigit = false;
    private final DecimalFormat decimalFormat = new DecimalFormat("0.########");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setDigitClickListeners();
        setOperationClickListeners();
        setActionClickListeners();
        updateDisplay("0");
    }

    private void setDigitClickListeners() {
        int[] digitButtonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int id : digitButtonIds) {
            Button button = findViewById(id);
            button.setOnClickListener(v -> appendDigit(button.getText().toString()));
        }

        binding.btnDot.setOnClickListener(v -> appendDot());
    }

    private void setOperationClickListeners() {
        binding.btnAdd.setOnClickListener(v -> chooseOperation("+"));
        binding.btnSubtract.setOnClickListener(v -> chooseOperation("-"));
        binding.btnMultiply.setOnClickListener(v -> chooseOperation("×"));
        binding.btnDivide.setOnClickListener(v -> chooseOperation("÷"));
        binding.btnEquals.setOnClickListener(v -> calculateResult());
    }

    private void setActionClickListeners() {
        binding.btnClear.setOnClickListener(v -> clearAll());
        binding.btnDelete.setOnClickListener(v -> deleteLast());
    }

    private void appendDigit(String digit) {
        if (resetInputOnNextDigit) {
            currentInput.setLength(0);
            resetInputOnNextDigit = false;
        }

        if (currentInput.length() == 1 && currentInput.charAt(0) == '0' && !currentInput.toString().contains(".")) {
            currentInput.setLength(0);
        }

        currentInput.append(digit);
        updateDisplay(currentInput.toString());
    }

    private void appendDot() {
        if (resetInputOnNextDigit) {
            currentInput.setLength(0);
            resetInputOnNextDigit = false;
        }

        if (currentInput.length() == 0) {
            currentInput.append("0");
        }

        if (!currentInput.toString().contains(".")) {
            currentInput.append(".");
            updateDisplay(currentInput.toString());
        }
    }

    private void chooseOperation(String operation) {
        if (currentInput.length() > 0) {
            if (!pendingOperation.isEmpty() && !resetInputOnNextDigit) {
                calculateResult();
            }
            firstValue = Double.parseDouble(currentInput.toString());
        }

        pendingOperation = operation;
        resetInputOnNextDigit = true;
        binding.tvExpression.setText(decimalFormat.format(firstValue) + " " + pendingOperation);
    }

    private void calculateResult() {
        if (pendingOperation.isEmpty() || currentInput.length() == 0) {
            return;
        }

        double secondValue = Double.parseDouble(currentInput.toString());
        double result;

        switch (pendingOperation) {
            case "+":
                result = firstValue + secondValue;
                break;
            case "-":
                result = firstValue - secondValue;
                break;
            case "×":
                result = firstValue * secondValue;
                break;
            case "÷":
                if (secondValue == 0) {
                    updateDisplay(getString(R.string.error_division_by_zero));
                    binding.tvExpression.setText("");
                    currentInput.setLength(0);
                    pendingOperation = "";
                    firstValue = 0.0;
                    resetInputOnNextDigit = true;
                    return;
                }
                result = firstValue / secondValue;
                break;
            default:
                return;
        }

        String expression = decimalFormat.format(firstValue) + " " + pendingOperation + " " + decimalFormat.format(secondValue);
        String formattedResult = decimalFormat.format(result);

        binding.tvExpression.setText(expression);
        currentInput.setLength(0);
        currentInput.append(formattedResult);
        updateDisplay(formattedResult);

        firstValue = result;
        pendingOperation = "";
        resetInputOnNextDigit = true;
    }

    private void clearAll() {
        currentInput.setLength(0);
        firstValue = 0.0;
        pendingOperation = "";
        resetInputOnNextDigit = false;
        binding.tvExpression.setText("");
        updateDisplay("0");
    }

    private void deleteLast() {
        if (resetInputOnNextDigit) {
            currentInput.setLength(0);
            updateDisplay("0");
            resetInputOnNextDigit = false;
            return;
        }

        if (currentInput.length() > 0) {
            currentInput.deleteCharAt(currentInput.length() - 1);
        }

        updateDisplay(currentInput.length() == 0 ? "0" : currentInput.toString());
    }

    private void updateDisplay(String value) {
        binding.tvDisplay.setText(value);
    }
}
