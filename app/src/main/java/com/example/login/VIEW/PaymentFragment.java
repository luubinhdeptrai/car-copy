package com.example.login.VIEW;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.MODELS.ConfirmBookingRequest;
import com.example.login.MODELS.ConfirmBookingResponse;
import com.example.login.MODELS.CreatePaymentUrlRequest;
import com.example.login.MODELS.CreatePaymentUrlResponse;
import com.example.login.MODELS.ProfileResponse;
import com.example.login.MODELS.Seat;
import com.example.login.MODELS.Trip;
import com.example.login.MODELS.User;
import com.example.login.R;
import com.vnpay.authentication.VNP_AuthenticationActivity;
import com.vnpay.authentication.VNP_SdkCompletedCallback;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentFragment extends Fragment {

    private Trip trip;
    private ArrayList<Seat> seats;
    private ApiService apiService;
    private long lockExpirationTime;
    private CountDownTimer countDownTimer;

    private TextView tvRouteTitle, tvDate, tvPaymentRoute, tvPaymentDepartureTime,
            tvTicketCount, tvSeatPosition, tvPickup, tvDropoff, tvPassengerName,
            tvPassengerPhone, tvPassengerEmail, tvTicketPrice, tvPaymentFee, tvTotalPrice,
            tvCountdownTimer;
    private Button payButton;
    private Toolbar toolbar;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getAuthAPI(getContext());
        if (getArguments() != null) {
            trip = (Trip) getArguments().getSerializable("PAYMENT_TRIP");
            seats = (ArrayList<Seat>) getArguments().getSerializable("PAYMENT_SEATS");
            lockExpirationTime = getArguments().getLong("LOCK_EXPIRATION_TIME", 0);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (trip == null || seats == null || seats.isEmpty()) {
            Toast.makeText(getContext(), "Error: Payment data is missing.", Toast.LENGTH_LONG).show();
            Navigation.findNavController(view).popBackStack();
            return;
        }

        bindViews(view);
        populateData();
        startCountdownTimer();

        toolbar.setNavigationOnClickListener(v -> {
            if (getView() != null) {
                Navigation.findNavController(getView()).popBackStack();
            }
        });

        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(getView() != null) {
                    Navigation.findNavController(getView()).popBackStack();
                }
            }
        });

        payButton.setOnClickListener(v -> {
            payButton.setEnabled(false);
            showPaymentMethodDialog();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    private void bindViews(View view) {
        toolbar = view.findViewById(R.id.toolbar_payment);
        tvRouteTitle = view.findViewById(R.id.tv_route_title_payment);
        tvDate = view.findViewById(R.id.tv_date_payment);
        tvPaymentRoute = view.findViewById(R.id.tv_payment_route);
        tvPaymentDepartureTime = view.findViewById(R.id.tv_payment_departure_time);
        tvTicketCount = view.findViewById(R.id.tv_payment_ticket_count);
        tvSeatPosition = view.findViewById(R.id.tv_payment_seat_position);
        tvPickup = view.findViewById(R.id.tv_payment_pickup);
        tvDropoff = view.findViewById(R.id.tv_payment_dropoff);
        tvPassengerName = view.findViewById(R.id.tv_payment_fullname);
        tvPassengerPhone = view.findViewById(R.id.tv_payment_phone);
        tvPassengerEmail = view.findViewById(R.id.tv_payment_email);
        tvTicketPrice = view.findViewById(R.id.tv_payment_ticket_price);
        tvPaymentFee = view.findViewById(R.id.tv_payment_fee);
        tvTotalPrice = view.findViewById(R.id.tv_payment_total_price);
        payButton = view.findViewById(R.id.btn_pay);
        tvCountdownTimer = view.findViewById(R.id.tv_countdown_timer);
    }

    private void startCountdownTimer() {
        long remainingMillis = lockExpirationTime - System.currentTimeMillis();

        if (remainingMillis <= 0) {
            handleTimerFinish();
            return;
        }

        countDownTimer = new CountDownTimer(remainingMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
                String timeLeftFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
                tvCountdownTimer.setText("Thời gian giữ vé còn lại: " + timeLeftFormatted);
            }

            @Override
            public void onFinish() {
                handleTimerFinish();
            }
        }.start();
    }

    private void handleTimerFinish() {
        tvCountdownTimer.setText("Time expired!");
        payButton.setEnabled(false);
        payButton.setText("Expired");
        if (getContext() != null) {
            new AlertDialog.Builder(getContext())
                    .setTitle("Session Expired")
                    .setMessage("Your seat lock has expired. Please select seats again.")
                    .setPositiveButton("OK", (dialog, which) -> {
                        if (getView() != null) {
                            Navigation.findNavController(getView()).popBackStack(R.id.selectTripFragment, false);
                        }
                    })
                    .setCancelable(false)
                    .show();
        }
    }

    private void populateData() {
        String routeTitleText = trip.getRoute().getOriginStation().getName() + " → " + trip.getRoute().getDestinationStation().getName();
        tvRouteTitle.setText(routeTitleText);
        tvPaymentRoute.setText(routeTitleText);
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date departureDate = utcFormat.parse(trip.getDepartureTime());

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());

            tvDate.setText(dateFormat.format(departureDate));
            tvPaymentDepartureTime.setText(dateTimeFormat.format(departureDate));

        } catch (Exception e) {
            tvDate.setText("N/A");
            tvPaymentDepartureTime.setText("N/A");
        }
        tvTicketCount.setText(String.format(Locale.US, "%d vé", seats.size()));
        ArrayList<String> seatNumbers = new ArrayList<>();
        for (Seat seat : seats) {
            seatNumbers.add(seat.getSeatNumber());
        }
        tvSeatPosition.setText(String.join(", ", seatNumbers));
        tvPickup.setText(trip.getRoute().getOriginStation().getName());
        tvDropoff.setText(trip.getRoute().getDestinationStation().getName());

        double totalPriceValue = trip.getPrice() * seats.size();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(totalPriceValue);
        tvTicketPrice.setText(formattedPrice);
        tvPaymentFee.setText("0đ");
        tvTotalPrice.setText(formattedPrice);

        fetchAndDisplayUserInfo();
    }

    private void fetchAndDisplayUserInfo() {
        apiService.getUserProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    tvPassengerName.setText(user.getFullname());
                    tvPassengerPhone.setText(user.getPhoneNumber());
                    tvPassengerEmail.setText(user.getEmail());
                } else {
                    Toast.makeText(getContext(), "Failed to load user info.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network error loading user info.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPaymentMethodDialog() {
        final String[] paymentMethods = {"Chuyển khoản ngân hàng", "Tiền mặt"};
        final String[] paymentMethodKeys = {"bank_transfer", "cash"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        // Inflate layout custom từ file xml
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View customTitleView = inflater.inflate(R.layout.dialog_custom_title, null);

        // Gán layout custom làm tiêu đề cho dialog
        builder.setCustomTitle(customTitleView);

        // Các phần còn lại giữ nguyên
        builder.setItems(paymentMethods, (dialog, which) -> {
                    Toast.makeText(getContext(), "Creating booking...", Toast.LENGTH_SHORT).show();
                    confirmBooking(paymentMethodKeys[which]);
                })
                .setOnCancelListener(dialog -> payButton.setEnabled(true));
        builder.create().show();
    }

    private void confirmBooking(String paymentMethod) {
        if (seats == null || seats.isEmpty()) {
            Toast.makeText(getContext(), "No seats to book.", Toast.LENGTH_SHORT).show();
            payButton.setEnabled(true);
            return;
        }

        ArrayList<String> ticketIds = new ArrayList<>();
        for (Seat seat : seats) {
            ticketIds.add(seat.getId());
        }

        ConfirmBookingRequest request = new ConfirmBookingRequest(ticketIds, paymentMethod);
        apiService.confirmBooking(request).enqueue(new Callback<ConfirmBookingResponse>() {
            @Override
            public void onResponse(@NonNull Call<ConfirmBookingResponse> call, @NonNull Response<ConfirmBookingResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    String bookingId = response.body().getData().getBookingId();
                    if ("bank_transfer".equals(paymentMethod)) {
                        createPaymentUrl(bookingId);
                    } else {
                        Toast.makeText(getContext(), "Booking successful! Please pay in cash.", Toast.LENGTH_LONG).show();
                        payButton.setEnabled(true);
                        if (getView() != null) {
                            Navigation.findNavController(getView()).popBackStack(R.id.buyTicketFragment, false);
                        }
                    }
                } else {
                    String errorMsg = "Booking failed.";
                    if (response.body() != null) {
                        errorMsg += " " + response.body().getMessage();
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    payButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<ConfirmBookingResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error during booking.", Toast.LENGTH_SHORT).show();
                payButton.setEnabled(true);
            }
        });
    }

    private void createPaymentUrl(String bookingId) {
        CreatePaymentUrlRequest request = new CreatePaymentUrlRequest(bookingId);
        apiService.createPaymentUrl(request).enqueue(new Callback<CreatePaymentUrlResponse>() {
            @Override
            public void onResponse(@NonNull Call<CreatePaymentUrlResponse> call, @NonNull Response<CreatePaymentUrlResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String paymentUrl = response.body().getPaymentUrl();
                    if (paymentUrl != null && !paymentUrl.isEmpty()) {
                        openVnpaySdk(paymentUrl, bookingId);

                    } else {
                        Toast.makeText(getContext(), "Received an empty payment URL.", Toast.LENGTH_LONG).show();
                        payButton.setEnabled(true);
                    }
                } else {
                    String errorMsg = "Failed to create payment URL.";
                    if (response.body() != null) {
                        errorMsg += " " + response.body().getMessage();
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    payButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<CreatePaymentUrlResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error while creating payment URL.", Toast.LENGTH_SHORT).show();
                payButton.setEnabled(true);
            }
        });
    }

    private void openVnpaySdk(String paymentUrl, String bookingId) {
        Intent intent = new Intent(requireActivity(), VNP_AuthenticationActivity.class);
        intent.putExtra("url", paymentUrl);
        intent.putExtra("tmn_code", "EX6ATLAM");
        intent.putExtra("scheme", "paymentresult");
        intent.putExtra("is_sandbox", true);

        VNP_AuthenticationActivity.setSdkCompletedCallback(new VNP_SdkCompletedCallback() {
            @Override
            public void sdkAction(String action) {
                if (action == null) return;
                Log.d("VNPAY_SDK", "SDK Action: " + action);

                switch (action) {
                    case "AppBackAction":
                        Toast.makeText(getActivity(), "Payment cancelled.", Toast.LENGTH_SHORT).show();
                        if (getView() != null) {
                            Navigation.findNavController(getView()).popBackStack();
                        }
                        break;
                    case "CallMobileBankingApp":
                        savePendingTransaction(bookingId);
                        Toast.makeText(getActivity(), "Redirecting to banking app...", Toast.LENGTH_SHORT).show();
                        break;
                    case "WebBackAction":
                    case "FailedBackAction":
                    case "SuccessBackAction":
                        Intent resultIntent = new Intent(getActivity(), PaymentResultActivity.class);
                        Uri resultData = Uri.parse("paymentresult://payment?bookingId=" + bookingId + "&action=" + action);
                        resultIntent.setData(resultData);
                        startActivity(resultIntent);
                        if (getActivity() != null) {
                            getActivity().finish();
                        }
                        break;
                    default:
                        Toast.makeText(getActivity(), "Unknown VNPAY action", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        startActivity(intent);
    }

    private void savePendingTransaction(String transactionId) {
        if (getContext() != null && transactionId != null) {
            getContext().getSharedPreferences("PaymentPrefs", Context.MODE_PRIVATE)
                    .edit()
                    .putString("PENDING_TRANSACTION_ID", transactionId)
                    .apply();
        }
    }
}