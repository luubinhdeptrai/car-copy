package com.example.login.VIEW;

import android.os.Bundle;
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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.MODELS.LockManySeatsRequest;
import com.example.login.MODELS.LockSeatResponse;
import com.example.login.MODELS.Seat;
import com.example.login.MODELS.SeatStatus;
import com.example.login.MODELS.Trip;
import com.example.login.R;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectSeatFragment extends Fragment {

    private GridLayout seatGridLeft, seatGridRight;
    private TextView selectedSeatsInfo, priceText;
    private Button continueButton;
    private Toolbar toolbar;
    private TextView tvToolbarRoute, tvToolbarDate, tvDepartureDetails;
    private Trip selectedTrip;
    private ApiService apiService;
    private SelectSeatViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(SelectSeatViewModel.class);
        apiService = ApiClient.getAuthAPI(getContext());

        if (getArguments() != null) {
            selectedTrip = (Trip) getArguments().getSerializable("SELECTED_TRIP");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_seat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (selectedTrip == null) {
            Toast.makeText(getContext(), "Error: Could not retrieve trip information.", Toast.LENGTH_LONG).show();
            Navigation.findNavController(view).popBackStack();
            return;
        }

        bindViews(view);
        populateTripInfo();
        setupClickListeners(view);
        observeViewModel();

        viewModel.fetchSeats(apiService, selectedTrip.getId(), selectedTrip.getVehicle().getCapacity());
    }

    private void bindViews(View view) {
        seatGridLeft = view.findViewById(R.id.seat_grid_left);
        seatGridRight = view.findViewById(R.id.seat_grid_right);
        selectedSeatsInfo = view.findViewById(R.id.selected_seats_info);
        priceText = view.findViewById(R.id.price_text);
        continueButton = view.findViewById(R.id.continue_button);
        toolbar = view.findViewById(R.id.toolbar_select_seat);
        tvToolbarRoute = view.findViewById(R.id.tv_toolbar_route);
        tvToolbarDate = view.findViewById(R.id.tv_toolbar_date);
        tvDepartureDetails = view.findViewById(R.id.tv_departure_details);
    }

    private void populateTripInfo() {
        String routeTitle = selectedTrip.getRoute().getOriginStation().getName() + " → " + selectedTrip.getRoute().getDestinationStation().getName();
        tvToolbarRoute.setText(routeTitle);

        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date departureDateObj = utcFormat.parse(selectedTrip.getDepartureTime());

            SimpleDateFormat toolbarDateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.US);
            SimpleDateFormat detailsDateFormat = new SimpleDateFormat("HH:mm EEEE, dd/MM/yyyy", Locale.US);

            tvToolbarDate.setText(toolbarDateFormat.format(departureDateObj));
            tvDepartureDetails.setText(detailsDateFormat.format(departureDateObj));
        } catch (ParseException e) {
            e.printStackTrace();
            tvToolbarDate.setText("N/A");
            tvDepartureDetails.setText("N/A");
        }
    }

    private void observeViewModel() {
        viewModel.getSeatList().observe(getViewLifecycleOwner(), seatList -> {
            if (seatList != null) {
                populateSeatGrid(seatList);
                updateSummary(seatList);
            }
        });
    }

    private void setupClickListeners(View view) {
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        // << SỬA ĐỔI: Thêm logic lock ghế vào nút Continue >>
        continueButton.setOnClickListener(v -> {
            lockSelectedSeatsAndNavigate();
        });
    }

    // << THÊM HÀM MỚI: Dùng để lock ghế và điều hướng >>
    private void lockSelectedSeatsAndNavigate() {
        ArrayList<Seat> selectedSeats = getSelectedSeatsFromList(viewModel.getSeatList().getValue());
        if (selectedSeats.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one seat.", Toast.LENGTH_SHORT).show();
            return;
        }

        continueButton.setEnabled(false);
        Toast.makeText(getContext(), "Locking seats...", Toast.LENGTH_SHORT).show();

        List<String> ticketIds = new ArrayList<>();
        for (Seat seat : selectedSeats) {
            ticketIds.add(seat.getId());
        }

        LockManySeatsRequest request = new LockManySeatsRequest(ticketIds);
        apiService.lockManySeats(request).enqueue(new Callback<LockSeatResponse>() {
            @Override
            public void onResponse(@NonNull Call<LockSeatResponse> call, @NonNull Response<LockSeatResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Seats locked successfully!", Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("CONFIRMATION_TRIP", selectedTrip);
                    bundle.putSerializable("CONFIRMATION_SEATS", selectedSeats);
                    if (getView() != null) {
                        Navigation.findNavController(getView()).navigate(R.id.action_selectSeat_to_confirmation, bundle);
                    }
                } else {
                    String errorMsg = "Failed to lock seats.";
                    if (response.body() != null) {
                        errorMsg += " " + response.body().getMessage();
                    }
                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
                }
                continueButton.setEnabled(true);
            }

            @Override
            public void onFailure(@NonNull Call<LockSeatResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                continueButton.setEnabled(true);
            }
        });
    }

    private void populateSeatGrid(List<Seat> seats) {
        seatGridLeft.removeAllViews();
        seatGridRight.removeAllViews();
        if (seats == null) return;

        for (Seat seat : seats) {
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

        if (seat.getStatus() != SeatStatus.SOLD_OUT) {
            seatButton.setOnClickListener(v -> viewModel.toggleSeatSelection(seat));
        } else {
            seatButton.setEnabled(false);
        }
        return seatButton;
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

    private void updateSummary(List<Seat> allSeats) {
        ArrayList<Seat> selectedSeats = getSelectedSeatsFromList(allSeats);
        int ticketCount = selectedSeats.size();
        double totalPrice = ticketCount * selectedTrip.getPrice();

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

    private ArrayList<Seat> getSelectedSeatsFromList(List<Seat> allSeats) {
        ArrayList<Seat> selected = new ArrayList<>();
        if (allSeats != null) {
            for (Seat seat : allSeats) {
                if (seat.getStatus() == SeatStatus.SELECTED) {
                    selected.add(seat);
                }
            }
        }
        return selected;
    }
}