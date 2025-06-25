package com.example.login.VIEW;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.MODELS.Seat;
import com.example.login.MODELS.SeatStatus;
import com.example.login.MODELS.TicketSeatListResponse;
import com.example.login.MODELS.TicketSeatResponse;
import com.example.login.MODELS.Trip;
import com.example.login.R;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectSeatFragment extends Fragment {

    // Views
    private GridLayout seatGridLeft, seatGridRight;
    private TextView selectedSeatsInfo, priceText;
    private Button continueButton;
    private Toolbar toolbar;

    // Data
    private Trip selectedTrip;
    private final List<Seat> seatList = new ArrayList<>();
    private final ArrayList<Seat> selectedSeats = new ArrayList<>(); // Giữ lại kiểu Seat để dễ quản lý
    private double pricePerSeat;
    private ApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_seat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getAuthAPI(getContext());

        if (getArguments() != null) {
            selectedTrip = (Trip) getArguments().getSerializable("SELECTED_TRIP");
        }

        if (selectedTrip == null) {
            Toast.makeText(getContext(), "Error: Could not retrieve trip information.", Toast.LENGTH_LONG).show();
            Navigation.findNavController(view).popBackStack();
            return;
        }

        bindViews(view);
        pricePerSeat = selectedTrip.getPrice();
        createAndPopulateSeats();
        updateSummary();
        setupClickListeners(view);
    }

    private void bindViews(View view) {
        seatGridLeft = view.findViewById(R.id.seat_grid_left);
        seatGridRight = view.findViewById(R.id.seat_grid_right);
        selectedSeatsInfo = view.findViewById(R.id.selected_seats_info);
        priceText = view.findViewById(R.id.price_text);
        continueButton = view.findViewById(R.id.continue_button);
        toolbar = view.findViewById(R.id.toolbar_select_seat);
    }

    private void setupClickListeners(View view) {
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        continueButton.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(getContext(), "Please select at least one seat.", Toast.LENGTH_SHORT).show();
            } else {
                Bundle bundle = new Bundle();
                bundle.putSerializable("CONFIRMATION_TRIP", selectedTrip);
                bundle.putSerializable("CONFIRMATION_SEATS", selectedSeats); // Gửi cả list Seat đi
                Navigation.findNavController(v).navigate(R.id.action_selectSeat_to_confirmation, bundle);
            }
        });
    }

    private void createAndPopulateSeats() {
        int capacity = selectedTrip.getVehicle().getCapacity();
        seatList.clear();

        String tripId = selectedTrip.getId();
        if (tripId == null || tripId.isEmpty()) {
            Toast.makeText(getContext(), "Error: Trip ID is missing.", Toast.LENGTH_LONG).show();
            initializeSeatsFallback(capacity);
            populateSeatGrid();
            return;
        }

        apiService.getTicketsForTrip(tripId).enqueue(new Callback<TicketSeatListResponse>() {
            @Override
            public void onResponse(@NonNull Call<TicketSeatListResponse> call, @NonNull Response<TicketSeatListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TicketSeatListResponse wrapperResponse = response.body();

                    if (wrapperResponse.isSuccess()) {
                        List<TicketSeatResponse> apiSeats = wrapperResponse.getData();
                        Map<String, String> apiSeatStatusMap = new HashMap<>();

                        if (apiSeats != null) {
                            for (TicketSeatResponse ticket : apiSeats) {
                                apiSeatStatusMap.put(ticket.getSeatNumber(), ticket.getStatus());
                            }
                        }

                        for (int i = 1; i <= capacity; i++) {
                            String seatNumberForDisplay = String.format(Locale.US, "%02d", i);
                            String seatNumberForLookup = String.valueOf(i);
                            String backendStatus = apiSeatStatusMap.getOrDefault(seatNumberForLookup, "available");
                            seatList.add(new Seat(seatNumberForDisplay, backendStatus));
                        }
                    } else {
                        Log.e("SelectSeatFragment", "API call successful but 'success' is false. Message: " + wrapperResponse.getMessage());
                        Toast.makeText(getContext(), "Error loading seat info: " + wrapperResponse.getMessage(), Toast.LENGTH_LONG).show();
                        initializeSeatsFallback(capacity);
                    }
                } else {
                    Log.e("SelectSeatFragment", "API call unsuccessful: " + response.code());
                    Toast.makeText(getContext(), "Error loading seat info: " + response.code(), Toast.LENGTH_LONG).show();
                    initializeSeatsFallback(capacity);
                }
                populateSeatGrid();
            }

            @Override
            public void onFailure(@NonNull Call<TicketSeatListResponse> call, @NonNull Throwable t) {
                Log.e("SelectSeatFragment", "API call failed: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Could not connect to the server to load seats.", Toast.LENGTH_LONG).show();
                initializeSeatsFallback(capacity);
                populateSeatGrid();
            }
        });
    }

    private void initializeSeatsFallback(int capacity) {
        seatList.clear();
        for (int i = 1; i <= capacity; i++) {
            String seatNumber = String.format(Locale.US, "%02d", i);
            seatList.add(new Seat(seatNumber, "available"));
        }
    }

    private void populateSeatGrid() {
        seatGridLeft.removeAllViews();
        seatGridRight.removeAllViews();

        for (Seat seat : seatList) {
            Button seatButton = createSeatButton(seat);
            int seatNum = Integer.parseInt(seat.getSeatNumber());
            int positionInRow = (seatNum - 1) % 4;

            if (positionInRow == 0 || positionInRow == 1) {
                seatGridLeft.addView(seatButton);
            } else {
                seatGridRight.addView(seatButton);
            }
        }
    }

    private Button createSeatButton(final Seat seat) {
        Button seatButton = new Button(getContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = (int) getResources().getDimension(R.dimen.seat_button_size);
        params.height = (int) getResources().getDimension(R.dimen.seat_button_size);
        int margin = (int) getResources().getDimension(R.dimen.seat_button_margin);
        params.setMargins(margin, margin, margin, margin);
        seatButton.setLayoutParams(params);
        seatButton.setText(seat.getSeatNumber());

        updateSeatButtonAppearance(seatButton, seat);

        if (seat.getStatus() == SeatStatus.SOLD_OUT) {
            seatButton.setEnabled(false);
        } else {
            seatButton.setEnabled(true);
            seatButton.setOnClickListener(v -> onSeatClick(seat, (Button) v));
        }
        return seatButton;
    }

    private void onSeatClick(Seat seat, Button seatButton) {
        if (seat.getStatus() == SeatStatus.AVAILABLE) {
            seat.setStatus(SeatStatus.SELECTED);
            selectedSeats.add(seat);
        } else if (seat.getStatus() == SeatStatus.SELECTED) {
            seat.setStatus(SeatStatus.AVAILABLE);
            selectedSeats.remove(seat);
        }
        updateSeatButtonAppearance(seatButton, seat);
        updateSummary();
    }

    private void updateSeatButtonAppearance(Button seatButton, Seat seat) {
        switch (seat.getStatus()) {
            case AVAILABLE:
                seatButton.setBackgroundResource(R.drawable.seat_available_bg);
                seatButton.setTextColor(ContextCompat.getColor(getContext(), R.color.text_primary));
                break;
            case SOLD_OUT:
                seatButton.setBackgroundResource(R.drawable.seat_sold_out_bg);
                seatButton.setTextColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
                break;
            case SELECTED:
                seatButton.setBackgroundResource(R.drawable.seat_selected_bg);
                seatButton.setTextColor(ContextCompat.getColor(getContext(), R.color.orange_primary));
                break;
        }
    }

    private void updateSummary() {
        int ticketCount = selectedSeats.size();
        double totalPrice = ticketCount * pricePerSeat;

        ArrayList<String> seatNumbersList = new ArrayList<>();
        for (Seat seat : selectedSeats) {
            seatNumbersList.add(seat.getSeatNumber());
        }
        Collections.sort(seatNumbersList);
        String seatNumbersText = String.join(", ", seatNumbersList);

        if (ticketCount > 0) {
            selectedSeatsInfo.setText(String.format(Locale.US, "%d ticket(s): %s", ticketCount, seatNumbersText));
        } else {
            selectedSeatsInfo.setText("Please select a seat");
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(totalPrice);
        priceText.setText(formattedPrice);
    }
}