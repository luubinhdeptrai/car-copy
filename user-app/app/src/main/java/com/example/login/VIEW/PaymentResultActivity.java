package com.example.login.VIEW;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.MODELS.PaymentStatusResponse;
import com.example.login.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentResultActivity extends AppCompatActivity {

    private ImageView statusIcon;
    private TextView paymentStatusText, paymentMessageText;
    private Button backToHomeButton;
    private ProgressBar progressBar;
    private ApiService apiService;
    private static final int MAX_RETRIES = 5;
    private static final long RETRY_DELAY_MS = 5000; // 5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_result);

        apiService = ApiClient.getAuthAPI(this);
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
        progressBar = findViewById(R.id.progressBar); // Assuming you add a ProgressBar with this ID to your layout
    }

    private void handlePaymentResult() {
        Uri data = getIntent().getData();
        if (data != null) {
            String bookingId = data.getQueryParameter("bookingId");

            if (bookingId != null && !bookingId.isEmpty()) {
                checkPaymentStatus(bookingId, 0);
            } else {
                showFailureScreen("Invalid booking data.");
            }
        } else {
            // Fallback for unexpected error
            showFailureScreen("Unknown error.");
        }
    }

    private void checkPaymentStatus(String bookingId, int retryCount) {
        if (retryCount == 0) {
            showLoading(true);
        }

        apiService.getPaymentStatus(bookingId).enqueue(new Callback<PaymentStatusResponse>() {
            @Override
            public void onResponse(Call<PaymentStatusResponse> call, Response<PaymentStatusResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String paymentStatus = response.body().getData().getPaymentStatus();
                    switch (paymentStatus) {
                        case "completed":
                            showLoading(false);
                            showSuccessScreen();
                            break;
                        case "pending":
                            if (retryCount < MAX_RETRIES) {
                                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                    checkPaymentStatus(bookingId, retryCount + 1);
                                }, RETRY_DELAY_MS);
                            } else {
                                showLoading(false);
                                showPendingScreen();
                            }
                            break;
                        case "expired":
                            showLoading(false);
                            showExpiredScreen();
                            break;
                        case "failed":
                        default:
                            showLoading(false);
                            showFailureScreen("Payment failed.");
                            break;
                    }
                } else {
                    showLoading(false);
                    showFailureScreen("Could not verify payment status.");
                }
            }

            @Override
            public void onFailure(Call<PaymentStatusResponse> call, Throwable t) {
                showLoading(false);
                showFailureScreen("Network error. Could not verify payment status.");
            }
        });
    }

    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            statusIcon.setVisibility(View.GONE);
            paymentStatusText.setVisibility(View.GONE);
            paymentMessageText.setVisibility(View.GONE);
            backToHomeButton.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            statusIcon.setVisibility(View.VISIBLE);
            paymentStatusText.setVisibility(View.VISIBLE);
            paymentMessageText.setVisibility(View.VISIBLE);
            backToHomeButton.setVisibility(View.VISIBLE);
        }
    }

    private void showSuccessScreen() {
        statusIcon.setImageResource(R.drawable.ic_success);
        statusIcon.setColorFilter(ContextCompat.getColor(this, R.color.success_green));
        paymentStatusText.setText(R.string.payment_successful);
        paymentMessageText.setText(R.string.payment_successful_message);
    }

    private void showFailureScreen(String message) {
        statusIcon.setImageResource(R.drawable.ic_failure);
        statusIcon.setColorFilter(ContextCompat.getColor(this, R.color.error_red));
        paymentStatusText.setText(R.string.payment_failed);
        paymentMessageText.setText(message);
    }

    private void showPendingScreen() {
        statusIcon.setImageResource(R.drawable.ic_pending); // You need to add this drawable
        statusIcon.setColorFilter(ContextCompat.getColor(this, R.color.pending_yellow)); // You need to add this color
        paymentStatusText.setText("Payment Pending");
        paymentMessageText.setText("Your payment is being processed. We will notify you once it's completed.");
    }

    private void showExpiredScreen() {
        statusIcon.setImageResource(R.drawable.ic_failure);
        statusIcon.setColorFilter(ContextCompat.getColor(this, R.color.error_red));
        paymentStatusText.setText("Payment Expired");
        paymentMessageText.setText("Your payment session has expired. Please try again.");
    }
}
