package com.example.login.VIEW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.MODELS.LockManySeatsRequest;
import com.example.login.MODELS.LockSeatResponse;
import com.example.login.MODELS.ProfileResponse; // Import a class
import com.example.login.MODELS.Seat;
import com.example.login.MODELS.Trip;
import com.example.login.MODELS.User; // Import a class
import com.example.login.R;
import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmationFragment extends Fragment {

    private Trip tripToShow;
    private ArrayList<Seat> selectedSeats;
    private ApiService apiService;
    private Button continueButton;

    // Add TextViews for user information
    private TextView fullnameValue;
    private TextView phoneValue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getAuthAPI(getContext());

        if (getArguments() != null) {
            tripToShow = (Trip) getArguments().getSerializable("CONFIRMATION_TRIP");
            selectedSeats = (ArrayList<Seat>) getArguments().getSerializable("CONFIRMATION_SEATS");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind the user info TextViews
        fullnameValue = view.findViewById(R.id.fullname_value);
        phoneValue = view.findViewById(R.id.phone_value);


        if (tripToShow == null || selectedSeats == null || selectedSeats.isEmpty()) {
            Toast.makeText(getContext(), "Error: Confirmation data is missing.", Toast.LENGTH_LONG).show();
            Navigation.findNavController(view).popBackStack();
            return;
        }

        populateViews(view);
        // Fetch and display user information
        fetchAndDisplayUserInfo();


        continueButton = view.findViewById(R.id.continue_button);
        continueButton.setOnClickListener(v -> {
            continueButton.setEnabled(false);
            Toast.makeText(getContext(), "Locking selected tickets...", Toast.LENGTH_SHORT).show();
            lockAllSelectedTickets();
        });
    }

    private void fetchAndDisplayUserInfo() {
        apiService.getUserProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    fullnameValue.setText(user.getFullname());
                    phoneValue.setText(user.getPhoneNumber());
                } else {
                    Toast.makeText(getContext(), "Failed to load user info.", Toast.LENGTH_SHORT).show();
                    fullnameValue.setText("N/A");
                    phoneValue.setText("N/A");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network error loading user info.", Toast.LENGTH_SHORT).show();
                fullnameValue.setText("N/A");
                phoneValue.setText("N/A");
            }
        });
    }


    private void lockAllSelectedTickets() {
        if (selectedSeats == null || selectedSeats.isEmpty()) {
            Toast.makeText(getContext(), "No seats selected.", Toast.LENGTH_SHORT).show();
            continueButton.setEnabled(true);
            return;
        }

        List<String> ticketIds = new ArrayList<>();
        for (Seat seat : selectedSeats) {
            if (seat.getId() != null && !seat.getId().isEmpty()) {
                ticketIds.add(seat.getId());
            } else {
                Toast.makeText(getContext(), "Error: Ticket ID missing for seat " + seat.getSeatNumber(), Toast.LENGTH_SHORT).show();
                continueButton.setEnabled(true);
                return;
            }
        }

        if (ticketIds.isEmpty()) {
            Toast.makeText(getContext(), "No valid ticket IDs to lock.", Toast.LENGTH_SHORT).show();
            continueButton.setEnabled(true);
            return;
        }

        LockManySeatsRequest request = new LockManySeatsRequest(ticketIds);
        apiService.lockManySeats(request).enqueue(new Callback<LockSeatResponse>() {
            @Override
            public void onResponse(@NonNull Call<LockSeatResponse> call, @NonNull Response<LockSeatResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    navigateToPayment();
                } else {
                    String errorMsg = "Failed to lock seats.";
                    if (response.body() != null) {
                        errorMsg += " " + response.body().getMessage();
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                    continueButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(@NonNull Call<LockSeatResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error while locking seats.", Toast.LENGTH_SHORT).show();
                continueButton.setEnabled(true);
            }
        });
    }

    private void navigateToPayment() {
        Toast.makeText(getContext(), "All seats locked successfully!", Toast.LENGTH_SHORT).show();
        Bundle bundle = new Bundle();
        bundle.putSerializable("PAYMENT_TRIP", tripToShow);
        bundle.putSerializable("PAYMENT_SEATS", selectedSeats);
        Navigation.findNavController(requireView()).navigate(R.id.action_confirmation_to_payment, bundle);
    }

    private void populateViews(View view) {
        TextView tvRouteTitle = view.findViewById(R.id.tv_route_title_confirmation);
        View tripInfoCard = view.findViewById(R.id.trip_info_card);
        TextView departureTime = tripInfoCard.findViewById(R.id.departure_time_text);
        TextView arrivalTime = tripInfoCard.findViewById(R.id.arrival_time_text);
        TextView tripDetails = tripInfoCard.findViewById(R.id.trip_details_text);
        TextView departureLocation = tripInfoCard.findViewById(R.id.departure_location_text);
        TextView destinationLocation = tripInfoCard.findViewById(R.id.destination_location_text);
        TextView tripDuration = tripInfoCard.findViewById(R.id.trip_duration_text);
        TextView seatNumberValue = view.findViewById(R.id.seat_number_value);

        String routeTitleText = tripToShow.getRoute().getOriginStation().getName() + " → " + tripToShow.getRoute().getDestinationStation().getName();
        tvRouteTitle.setText(routeTitleText);
        departureTime.setText(formatTime(tripToShow.getDepartureTime(), "HH:mm"));
        arrivalTime.setText(formatTime(tripToShow.getArrivalTime(), "HH:mm"));
        departureLocation.setText(tripToShow.getRoute().getOriginStation().getName());
        destinationLocation.setText(tripToShow.getRoute().getDestinationStation().getName());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(tripToShow.getPrice());
        String detailsText = String.format(Locale.US, "%s • %s", formattedPrice, tripToShow.getVehicle().getType());
        tripDetails.setText(detailsText);

        String durationText = String.format(Locale.US, "Distance: %dkm - %s",
                tripToShow.getRoute().getDistanceKm(),
                calculateDuration(tripToShow.getDepartureTime(), tripToShow.getArrivalTime()));
        tripDuration.setText(durationText);

        ArrayList<String> seatNumbers = new ArrayList<>();
        for (Seat seat : selectedSeats) {
            seatNumbers.add(seat.getSeatNumber());
        }
        seatNumberValue.setText(String.join(", ", seatNumbers));
    }

    private String formatTime(String utcDateString, String format) {
        if (utcDateString == null) return "N/A";
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = utcFormat.parse(utcDateString);
            SimpleDateFormat desiredFormat = new SimpleDateFormat(format, Locale.getDefault());
            return desiredFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    private String calculateDuration(String startUtc, String endUtc) {
        if (startUtc == null || endUtc == null) return "N/A";
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date startDate = utcFormat.parse(startUtc);
            Date endDate = utcFormat.parse(endUtc);
            long durationMillis = endDate.getTime() - startDate.getTime();
            long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60;
            return String.format(Locale.US, "~%dh %dm", hours, minutes);
        } catch (ParseException e) {
            e.printStackTrace();
            return "N/A";
        }
    }
}