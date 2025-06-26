package com.example.login.VIEW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.MODELS.ProfileResponse;
import com.example.login.MODELS.Seat;
import com.example.login.MODELS.Trip;
import com.example.login.MODELS.User;
import com.example.login.R;
import java.io.Serializable;
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
    }

    private void populateData() {
        String routeTitleText = trip.getRoute().getOriginStation().getName() + " → " + trip.getRoute().getDestinationStation().getName();
        tvRouteTitle.setText(routeTitleText);
        tvPaymentRoute.setText(routeTitleText);
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            Date departureDateObj = utcFormat.parse(trip.getDepartureTime());
            SimpleDateFormat localDateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.US);
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
        tvPickup.setText(trip.getRoute().getOriginStation().getName()); // Giả sử tên bến xe là điểm đón
        tvDropoff.setText(trip.getRoute().getDestinationStation().getName()); // Giả sử tên bến xe là điểm trả

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
}