package com.example.login.VIEW;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.example.login.R;

public class PaymentResultActivity extends AppCompatActivity {

    private ImageView statusIcon;
    private TextView paymentStatusText, paymentMessageText;
    private Button backToHomeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        bindViews();
        handlePaymentResult();

        backToHomeButton.setOnClickListener(v -> {
            // Navigate back to the main activity, clearing the task stack
            Intent intent = new Intent(PaymentResultActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void bindViews() {
        statusIcon = findViewById(R.id.iv_status_icon);
        paymentStatusText = findViewById(R.id.tv_payment_status);
        paymentMessageText = findViewById(R.id.tv_payment_message);
        backToHomeButton = findViewById(R.id.btn_back_to_home);
    }

    private void handlePaymentResult() {
        Uri data = getIntent().getData();
        if (data != null) {
            // VNPAY returns status in the 'vnp_ResponseCode' query parameter
            String responseCode = data.getQueryParameter("vnp_ResponseCode");

            if ("00".equals(responseCode)) {
                showSuccessScreen();
            } else {
                showFailureScreen(responseCode);
            }
        } else {
            // Fallback for unexpected error
            showFailureScreen("Unknown");
        }
    }

    private void showSuccessScreen() {
        // TODO: Create a success icon in your drawables, e.g., R.drawable.ic_success
        statusIcon.setImageResource(R.drawable.ic_success); // Replace with your actual drawable
        statusIcon.setColorFilter(ContextCompat.getColor(this, R.color.success_green));
        paymentStatusText.setText(R.string.payment_successful);
        paymentMessageText.setText(R.string.payment_successful_message);
    }

    private void showFailureScreen(String responseCode) {
        // TODO: Create a failure icon in your drawables, e.g., R.drawable.ic_failure
        statusIcon.setImageResource(R.drawable.ic_failure); // Replace with your actual drawable
        statusIcon.setColorFilter(ContextCompat.getColor(this, R.color.error_red));
        paymentStatusText.setText(R.string.payment_failed);
        String message = getString(R.string.payment_failed_message, responseCode);
        paymentMessageText.setText(message);
    }
}