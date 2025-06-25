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
import android.widget.LinearLayout;
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
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyTicketFragment extends Fragment {

    // Khai báo các view
    private AutoCompleteTextView actvDeparture, actvDestination;
    private TextView tvDepartureDate, tvDepartureDay, tvReturnDate, tvReturnDay;
    private Spinner ticketNumberSpinner;
    private SwitchMaterial roundTripSwitch;
    private LinearLayout returnDateLayout;
    private Button searchButton;
    private Toolbar toolbar;

    // Thêm biến để lưu ngày đi và ngày về
    private Calendar selectedCalendar;
    private Calendar selectedReturnCalendar;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_buy_ticket, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ toàn bộ view
        actvDeparture = view.findViewById(R.id.actv_departure);
        actvDestination = view.findViewById(R.id.actv_destination);
        tvDepartureDate = view.findViewById(R.id.tv_departure_date);
        tvDepartureDay = view.findViewById(R.id.tv_departure_day);
        ticketNumberSpinner = view.findViewById(R.id.ticket_number_spinner);
        roundTripSwitch = view.findViewById(R.id.round_trip_switch);
        returnDateLayout = view.findViewById(R.id.return_date_layout);
        searchButton = view.findViewById(R.id.search_route_button);
        toolbar = view.findViewById(R.id.toolbar);
        // Ánh xạ các view cho ngày về
        tvReturnDate = view.findViewById(R.id.tv_return_date);
        tvReturnDay = view.findViewById(R.id.tv_return_day);


        // Gọi các hàm thiết lập
        setupAutoCompleteTextViews();
        setupTicketNumberSpinner();
        setupRoundTripSwitch();
        setupDatePicker();
        setupClickListeners(view);
    }

    private void setupAutoCompleteTextViews() {
        // SỬA: Dịch sang tiếng Anh
        String[] locations = {"Hồ Chí Minh", "Đà Lạt", "Vũng Tàu", "Nha Trang", "Cần Thơ"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, locations);
        actvDeparture.setAdapter(adapter);
        actvDestination.setAdapter(adapter);
    }

    private void setupTicketNumberSpinner() {
        List<String> ticketNumbers = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            ticketNumbers.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, ticketNumbers);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ticketNumberSpinner.setAdapter(adapter);
    }

    private void setupRoundTripSwitch() {
        roundTripSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            returnDateLayout.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });
    }

    private void setupDatePicker() {
        // Khởi tạo ngày đi và ngày về
        selectedCalendar = Calendar.getInstance();
        selectedReturnCalendar = Calendar.getInstance();
        selectedReturnCalendar.add(Calendar.DAY_OF_YEAR, 1); // Mặc định ngày về sau ngày đi 1 ngày

        // Cập nhật giao diện
        updateDateViews(selectedCalendar, tvDepartureDate, tvDepartureDay);
        updateDateViews(selectedReturnCalendar, tvReturnDate, tvReturnDay);

        // Gắn sự kiện click cho ngày đi
        View.OnClickListener departureClickListener = v -> showDepartureDatePickerDialog();
        tvDepartureDate.setOnClickListener(departureClickListener);
        tvDepartureDay.setOnClickListener(departureClickListener);

        // Gắn sự kiện click cho ngày về
        View.OnClickListener returnClickListener = v -> showReturnDatePickerDialog();
        tvReturnDate.setOnClickListener(returnClickListener);
        tvReturnDay.setOnClickListener(returnClickListener);
    }

    private void showDepartureDatePickerDialog() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    selectedCalendar.set(selectedYear, selectedMonth, selectedDayOfMonth);
                    updateDateViews(selectedCalendar, tvDepartureDate, tvDepartureDay);

                    // Đảm bảo ngày về luôn sau ngày đi
                    if (selectedReturnCalendar.before(selectedCalendar)) {
                        selectedReturnCalendar.setTime(selectedCalendar.getTime());
                        selectedReturnCalendar.add(Calendar.DAY_OF_YEAR, 1);
                        updateDateViews(selectedReturnCalendar, tvReturnDate, tvReturnDay);
                    }
                },
                year, month, day
        );
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    private void showReturnDatePickerDialog() {
        int year = selectedReturnCalendar.get(Calendar.YEAR);
        int month = selectedReturnCalendar.get(Calendar.MONTH);
        int day = selectedReturnCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    selectedReturnCalendar.set(selectedYear, selectedMonth, selectedDayOfMonth);
                    updateDateViews(selectedReturnCalendar, tvReturnDate, tvReturnDay);
                },
                year, month, day
        );

        // Quan trọng: Ngày về phải sau hoặc bằng ngày đi
        datePickerDialog.getDatePicker().setMinDate(selectedCalendar.getTimeInMillis());
        datePickerDialog.show();
    }

    // Hàm tiện ích để cập nhật giao diện ngày
    private void updateDateViews(Calendar calendar, TextView dateTextView, TextView dayTextView) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
        dateTextView.setText(dateFormat.format(calendar.getTime()));

        // SỬA: Dùng Locale.US để hiển thị tiếng Anh
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
        dayTextView.setText(dayFormat.format(calendar.getTime()));
    }

    private void setupClickListeners(View view) {
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());
        searchButton.setOnClickListener(v -> searchForTrips(view));
    }

    private void searchForTrips(View view) {
        String origin = actvDeparture.getText().toString().trim();
        String destination = actvDestination.getText().toString().trim();

        if (origin.isEmpty() || destination.isEmpty()) {
            // SỬA: Dịch sang tiếng Anh
            Toast.makeText(getContext(), "Please select departure and destination", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat forApi = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = forApi.format(selectedCalendar.getTime());

        // SỬA: Dịch sang tiếng Anh
        Toast.makeText(getContext(), "Searching for trips...", Toast.LENGTH_SHORT).show();

        ApiService apiService = ApiClient.getNoAuthAPI();
        Call<TripSearchResponse> call = apiService.searchTrips(origin, destination, formattedDate);

        call.enqueue(new Callback<TripSearchResponse>() {
            @Override
            public void onResponse(Call<TripSearchResponse> call, Response<TripSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TripSearchResponse searchResponse = response.body();
                    if (searchResponse.isSuccess()) {
                        List<Trip> trips = searchResponse.getData();
                        if (trips != null && !trips.isEmpty()) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("TRIPS_RESULT", (Serializable) trips);
                            Navigation.findNavController(view).navigate(R.id.action_buyTicket_to_selectTrip, bundle);
                        } else {
                            // SỬA: Dịch sang tiếng Anh
                            Toast.makeText(getContext(), "No suitable trips found", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        // SỬA: Dịch sang tiếng Anh
                        Toast.makeText(getContext(), "Error: " + searchResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    // SỬA: Dịch sang tiếng Anh
                    Toast.makeText(getContext(), "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TripSearchResponse> call, Throwable t) {
                // SỬA: Dịch sang tiếng Anh
                Toast.makeText(getContext(), "Network connection error. Please try again!", Toast.LENGTH_SHORT).show();
                Log.e("BuyTicketFragment", "API call failed", t);
            }
        });
    }
}
    