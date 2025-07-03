package com.example.login.VIEW;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.MODELS.Trip;
import com.example.login.MODELS.TripSearchResponse;
import com.example.login.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyTicketFragment extends Fragment {

    private AutoCompleteTextView actvDeparture, actvDestination;
    private TextView tvDepartureDate, tvDepartureDay;
    private Button searchButton;
    private Toolbar toolbar;
    private Calendar selectedCalendar;
    private ImageView ivSwap;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buy_ticket, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        actvDeparture = view.findViewById(R.id.actv_departure);
        actvDestination = view.findViewById(R.id.actv_destination);
        tvDepartureDate = view.findViewById(R.id.tv_departure_date);
        tvDepartureDay = view.findViewById(R.id.tv_departure_day);
        searchButton = view.findViewById(R.id.search_route_button);
        toolbar = view.findViewById(R.id.toolbar);
        ivSwap = view.findViewById(R.id.iv_swap);

        setupAutoCompleteTextViews();
        setupDatePicker();
        setupClickListeners(view);
    }

    private void setupAutoCompleteTextViews() {
        String[] locations = {"Hồ Chí Minh", "Đà Lạt", "Vũng Tàu", "Nha Trang", "Cần Thơ"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, locations);
        actvDeparture.setAdapter(adapter);
        actvDestination.setAdapter(adapter);
    }




    private void setupDatePicker() {
        selectedCalendar = Calendar.getInstance();
        updateDateViews(selectedCalendar);
        View.OnClickListener departureClickListener = v -> showDepartureDatePickerDialog();
        tvDepartureDate.setOnClickListener(departureClickListener);
        tvDepartureDay.setOnClickListener(departureClickListener);
    }

    private void showDepartureDatePickerDialog() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        // SỬA: Thêm R.style.AppDatePickerTheme vào hàm tạo
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                R.style.AppDatePickerTheme, // <-- Áp dụng theme mới tại đây
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDayOfMonth);
                    updateDateViews(selectedCalendar);
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void updateDateViews(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.US);
        tvDepartureDate.setText(dateFormat.format(calendar.getTime()));
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));
        tvDepartureDay.setText(dayFormat.format(calendar.getTime()));
    }

    private void setupClickListeners(View view) {
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());
        searchButton.setOnClickListener(v -> searchForTrips(view));
        ivSwap.setOnClickListener(v -> swapLocations());
    }

    private void swapLocations() {
        String departure = actvDeparture.getText().toString();
        String destination = actvDestination.getText().toString();
        actvDeparture.setText(destination);
        actvDestination.setText(departure);
    }

    private void searchForTrips(View view) {
        String origin = actvDeparture.getText().toString().trim();
        String destination = actvDestination.getText().toString().trim();
        if (origin.isEmpty() || destination.isEmpty()) {
            Toast.makeText(getContext(), "Please select departure and destination points", Toast.LENGTH_SHORT).show();
            return;
        }
        SimpleDateFormat forApi = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = forApi.format(selectedCalendar.getTime());

        ApiService apiService = ApiClient.getNoAuthAPI();
        Call<TripSearchResponse> call = apiService.searchTrips(origin, destination, formattedDate);
        call.enqueue(new Callback<TripSearchResponse>() {
            @Override
            public void onResponse(Call<TripSearchResponse> call, Response<TripSearchResponse> response) {
                List<Trip> trips = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    if (response.body().getData() != null) {
                        trips = response.body().getData();
                    }
                } else {
                    String errorMessage = response.body() != null ? response.body().getMessage() : "Server error: " + response.code();
                    Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                }
                navigateToSelectTrip(view, origin, destination, trips);
            }

            @Override
            public void onFailure(Call<TripSearchResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Network connection error. Please try again!", Toast.LENGTH_SHORT).show();
                Log.e("BuyTicketFragment", "API call failed", t);
                navigateToSelectTrip(view, origin, destination, new ArrayList<>());
            }
        });
    }

    private void navigateToSelectTrip(View view, String origin, String destination, List<Trip> trips) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("TRIPS_RESULT", (Serializable) trips);
        bundle.putLong("SELECTED_DATE_MILLIS", selectedCalendar.getTimeInMillis());
        bundle.putString("DEPARTURE_LOCATION", origin);
        bundle.putString("DESTINATION_LOCATION", destination);
        Navigation.findNavController(view).navigate(R.id.action_buyTicket_to_selectTrip, bundle);
    }
}