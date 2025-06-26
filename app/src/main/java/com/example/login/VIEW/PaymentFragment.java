package com.example.login.VIEW;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentFragment extends Fragment {

    private Trip trip;
    private ArrayList<Seat> seats;
    private ApiService apiService;

    private TextView tvRouteTitle, tvDate, tvPaymentRoute, tvPaymentDepartureTime,
            tvTicketCount, tvSeatPosition, tvPassengerName, tvPassengerPhone,
            tvPassengerEmail, tvTicketPrice, tvPaymentFee, tvTotalPrice,
            tvPickup, tvDropoff;
    private Button payButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getAuthAPI(getContext());
        if (getArguments() != null) {
            trip = (Trip) getArguments().getSerializable("PAYMENT_TRIP");
            seats = (ArrayList<Seat>) getArguments().getSerializable("PAYMENT_SEATS");
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

        Toolbar toolbar = view.findViewById(R.id.toolbar_payment);
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).popBackStack());

        bindViews(view);

        if (trip != null && seats != null) {
            populateData();
        } else {
            Toast.makeText(getContext(), "Error: Payment data is missing.", Toast.LENGTH_LONG).show();
        }

        payButton.setOnClickListener(v -> showPaymentMethodDialog());
    }

    private void bindViews(View view) {
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
    }

    private void populateData() {
        String routeTitleText = trip.getRoute().getOriginStation().getName() + " → " + trip.getRoute().getDestinationStation().getName();
        tvRouteTitle.setText(routeTitleText);
        tvPaymentRoute.setText(routeTitleText);
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            Date departureDateObj = utcFormat.parse(trip.getDepartureTime());
            SimpleDateFormat localDateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("vi", "VN"));
            SimpleDateFormat localTimeFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.US);
            tvDate.setText(localDateFormat.format(departureDateObj));
            tvPaymentDepartureTime.setText(localTimeFormat.format(departureDateObj));
        } catch (Exception e) {
            tvDate.setText("N/A");
            tvPaymentDepartureTime.setText("N/A");
        }
        tvTicketCount.setText(String.format(Locale.US, "%d ticket(s)", seats.size()));
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
                    if (user != null) {
                        tvPassengerName.setText(user.getFullname());
                        tvPassengerPhone.setText(user.getPhoneNumber());
                        tvPassengerEmail.setText(user.getEmail());
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load user info", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPaymentMethodDialog() {
        final String[] paymentMethods = {"Bank Transfer", "Cash"};
        final String[] paymentMethodKeys = {"bank_transfer", "cash"};

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Choose Payment Method")
                .setItems(paymentMethods, (dialog, which) -> {
                    String selectedMethod = paymentMethodKeys[which];
                    payButton.setEnabled(false);
                    Toast.makeText(getContext(), "Processing booking...", Toast.LENGTH_SHORT).show();
                    confirmBooking(selectedMethod);
                });
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
                    String bookingId = response.body().getData().getBookingId();
                    Toast.makeText(getContext(), "Booking created. Generating payment link...", Toast.LENGTH_SHORT).show();
                    createPaymentUrl(bookingId);
                } else {
                    String errorMsg = "Booking failed: " + (response.body() != null ? response.body().getMessage() : "Unknown error");
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
                    openPaymentUrlInBrowser(paymentUrl);
                } else {
                    String errorMsg = "Failed to create payment URL: " + (response.body() != null ? response.body().getMessage() : "Unknown error");
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
                payButton.setEnabled(true);
            }

            @Override
            public void onFailure(@NonNull Call<CreatePaymentUrlResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error creating payment URL.", Toast.LENGTH_SHORT).show();
                payButton.setEnabled(true);
            }
        });
    }

    private void openPaymentUrlInBrowser(String url) {
        if (url == null || url.isEmpty()) {
            Toast.makeText(getContext(), "Invalid payment URL received.", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Could not open browser for payment.", Toast.LENGTH_LONG).show();
        }
    }
}