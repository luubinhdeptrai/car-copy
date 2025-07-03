package com.example.login.VIEW;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.MODELS.LockSeatResponse;
import com.example.login.MODELS.ProfileResponse;
import com.example.login.MODELS.Seat;
import com.example.login.MODELS.Trip;
import com.example.login.MODELS.UnlockSeatsRequest;
import com.example.login.MODELS.User;
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
    private Toolbar toolbar;

    private TextView fullnameValue;
    private TextView phoneValue;

    private TextView pickupLocationTextView;
    private TextView dropoffLocationTextView;

    private TextView providerNameTextView;

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

        fullnameValue = view.findViewById(R.id.fullname_value);
        phoneValue = view.findViewById(R.id.phone_value);
        continueButton = view.findViewById(R.id.continue_button);
        toolbar = view.findViewById(R.id.toolbar_confirmation);
        pickupLocationTextView = view.findViewById(R.id.pickup_location_textview);
        dropoffLocationTextView = view.findViewById(R.id.dropoff_location_textview);


        if (tripToShow == null || selectedSeats == null || selectedSeats.isEmpty()) {
            Toast.makeText(getContext(), "Error: Confirmation data is missing.", Toast.LENGTH_LONG).show();
            Navigation.findNavController(view).popBackStack();
            return;
        }

        populateViews(view);
        fetchAndDisplayUserInfo();

        toolbar.setNavigationOnClickListener(v -> handleBackPressWithUnlock());
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleBackPressWithUnlock();
            }
        });

        continueButton.setEnabled(true);
        continueButton.setOnClickListener(v -> navigateToPayment());
    }

    private void handleBackPressWithUnlock() {
        toolbar.setNavigationOnClickListener(null);

        Toast.makeText(getContext(), "Cancelling...", Toast.LENGTH_SHORT).show();

        List<String> ticketIds = new ArrayList<>();
        for (Seat seat : selectedSeats) {
            ticketIds.add(seat.getId());
        }

        UnlockSeatsRequest request = new UnlockSeatsRequest(ticketIds);
        apiService.unlockSeats(request).enqueue(new Callback<LockSeatResponse>() {
            @Override
            public void onResponse(@NonNull Call<LockSeatResponse> call, @NonNull Response<LockSeatResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Seats have been unlocked.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Failed to unlock seats.", Toast.LENGTH_SHORT).show();
                }

                Log.d("ConfirmationFragment", "Unlock seats completed, navigating back.");
                if (getView() != null) {
                    Navigation.findNavController(getView()).popBackStack();
                }
            }
            @Override
            public void onFailure(@NonNull Call<LockSeatResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Failed to unlock seats due to network error.", Toast.LENGTH_SHORT).show();
                Log.e("ConfirmationFragment", "Unlock seats failed: " + t.getMessage());
                if (getView() != null) {
                    Navigation.findNavController(getView()).popBackStack();
                }
            }
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

    private void navigateToPayment() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("PAYMENT_TRIP", tripToShow);
        bundle.putSerializable("PAYMENT_SEATS", selectedSeats);
        if(getView() != null) {
            Navigation.findNavController(getView()).navigate(R.id.action_confirmation_to_payment, bundle);
        }
    }

    private void populateViews(View view) {
        // Ánh xạ các view
        TextView tvRouteTitle = view.findViewById(R.id.tv_route_title_confirmation);
        TextView tvToolbarDate = view.findViewById(R.id.tv_toolbar_date_confirmation); // SỬA: Thêm ánh xạ
        View tripInfoCard = view.findViewById(R.id.trip_info_card);
        TextView departureTime = tripInfoCard.findViewById(R.id.departure_time_text);
        TextView arrivalTime = tripInfoCard.findViewById(R.id.arrival_time_text);
        TextView tripDetails = tripInfoCard.findViewById(R.id.trip_details_text);
        TextView departureLocation = tripInfoCard.findViewById(R.id.departure_location_text);
        TextView destinationLocation = tripInfoCard.findViewById(R.id.destination_location_text);
        TextView tripDuration = tripInfoCard.findViewById(R.id.trip_duration_text);
        TextView providerName = tripInfoCard.findViewById(R.id.provider_name_text);
        TextView seatNumberValue = view.findViewById(R.id.seat_number_value);

        // Gán dữ liệu
        String routeTitleText = tripToShow.getRoute().getOriginStation().getName() + " → " + tripToShow.getRoute().getDestinationStation().getName();
        tvRouteTitle.setText(routeTitleText);

        // SỬA: Gán dữ liệu ngày cho Toolbar
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date departureDateObj = utcFormat.parse(tripToShow.getDepartureTime());

            Locale vietnameseLocale = new Locale("vi", "VN");
            SimpleDateFormat toolbarDateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", vietnameseLocale);

            tvToolbarDate.setText(toolbarDateFormat.format(departureDateObj));
        } catch (ParseException e) {
            e.printStackTrace();
            tvToolbarDate.setText("N/A");
        }
        // KẾT THÚC SỬA ĐỔI

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

        providerName.setText(tripToShow.getProvider().getName());

        if (tripToShow.getRoute() != null) {
            if (tripToShow.getRoute().getOriginStation() != null && tripToShow.getRoute().getOriginStation().getAddress() != null) {
                pickupLocationTextView.setText(tripToShow.getRoute().getOriginStation().getAddress());
            } else {
                pickupLocationTextView.setText("N/A");
            }

            if (tripToShow.getRoute().getDestinationStation() != null && tripToShow.getRoute().getDestinationStation().getAddress() != null) {
                dropoffLocationTextView.setText(tripToShow.getRoute().getDestinationStation().getAddress());
            } else {
                dropoffLocationTextView.setText("N/A");
            }
        } else {
            pickupLocationTextView.setText("N/A");
            dropoffLocationTextView.setText("N/A");
        }
        departureLocation.setOnClickListener(v -> {
            Trip.Station.Coordinate coordinates = tripToShow.getRoute().getOriginStation().getCoordinates();
            Uri gmmIntentUri = Uri.parse("geo:" + coordinates.getLatitude() + "," + coordinates.getLongitude() +
                    "?q=" + coordinates.getLatitude() + "," + coordinates.getLongitude() +
                    "(" + tripToShow.getRoute().getOriginStation().getName() + ")");

            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
            mapIntent.addFlags(Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS);

            startActivity(mapIntent);
        });
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