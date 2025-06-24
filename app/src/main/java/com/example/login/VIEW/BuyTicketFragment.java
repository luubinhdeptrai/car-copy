package com.example.login.VIEW;

import android.app.DatePickerDialog; // THÊM import
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar; // THÊM import
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BuyTicketFragment extends Fragment {

    // Khai báo các view
    private AutoCompleteTextView actvDeparture, actvDestination;
    private TextView tvDepartureDate, tvDepartureDay; // THÊM tvDepartureDay
    private Spinner ticketNumberSpinner;
    private SwitchMaterial roundTripSwitch;
    private LinearLayout returnDateLayout;
    private Button searchButton;
    private Toolbar toolbar;

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
        tvDepartureDay = view.findViewById(R.id.tv_departure_day); // THÊM ánh xạ
        ticketNumberSpinner = view.findViewById(R.id.ticket_number_spinner);
        roundTripSwitch = view.findViewById(R.id.round_trip_switch);
        returnDateLayout = view.findViewById(R.id.return_date_layout);
        searchButton = view.findViewById(R.id.search_route_button);
        toolbar = view.findViewById(R.id.toolbar);

        // Gọi các hàm thiết lập
        setupAutoCompleteTextViews();
        setupTicketNumberSpinner();
        setupRoundTripSwitch();
        setupDatePicker(); // THÊM hàm thiết lập DatePicker
        setupClickListeners(view);
    }

    // --- CÁC HÀM CŨ GIỮ NGUYÊN ---
    private void setupAutoCompleteTextViews() {
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

    // --- THÊM PHẦN LOGIC CHO DATEPICKER ---
    private void setupDatePicker() {
        // 1. Mặc định là ngày hiện tại của hệ thống
        Calendar today = Calendar.getInstance();
        updateDateViews(today);

        // 2. Gắn sự kiện khi người dùng bấm vào TextView
        tvDepartureDate.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        // Lấy ngày đang được chọn trên TextView để mở Lịch đúng ngày đó
        Calendar selectedDate = Calendar.getInstance();
        try {
            // Cố gắng đọc ngày từ TextView
            Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(tvDepartureDate.getText().toString());
            selectedDate.setTime(date);
        } catch (ParseException e) {
            // Nếu có lỗi, dùng ngày hiện tại
            e.printStackTrace();
        }

        int year = selectedDate.get(Calendar.YEAR);
        int month = selectedDate.get(Calendar.MONTH);
        int day = selectedDate.get(Calendar.DAY_OF_MONTH);

        // Tạo DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    // 3. Khi người dùng chọn ngày và nhấn OK, cập nhật lại TextView
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(selectedYear, selectedMonth, selectedDayOfMonth);
                    updateDateViews(newDate);
                },
                year, month, day
        );

        // Ngăn người dùng chọn ngày trong quá khứ
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

        // Hiển thị hộp thoại
        datePickerDialog.show();
    }

    // Hàm tiện ích để cập nhật cả TextView ngày (dd/MM/yyyy) và thứ (Tuesday)
    private void updateDateViews(Calendar calendar) {
        // Định dạng ngày
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvDepartureDate.setText(dateFormat.format(calendar.getTime()));

        // Định dạng thứ
        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.forLanguageTag("vi-VN")); // Hiển thị tiếng Việt, ví dụ "Thứ ba"
        tvDepartureDay.setText(dayFormat.format(calendar.getTime()));
    }


    // --- CÁC HÀM CŨ GIỮ NGUYÊN ---
    private void setupClickListeners(View view) {
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());
        searchButton.setOnClickListener(v -> searchForTrips(view));
    }

    private void searchForTrips(View view) {
        // ... (Code gọi API không thay đổi)
        String origin = actvDeparture.getText().toString().trim();
        String destination = actvDestination.getText().toString().trim();
        String departureDate = tvDepartureDate.getText().toString();

        if (origin.isEmpty() || destination.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng chọn điểm đi và điểm đến", Toast.LENGTH_SHORT).show();
            return;
        }

        String formattedDate = formatDateForApi(departureDate);
        if (formattedDate == null) {
            Toast.makeText(getContext(), "Định dạng ngày không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "Đang tìm kiếm chuyến đi...", Toast.LENGTH_SHORT).show();

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
                            Toast.makeText(getContext(), "Không tìm thấy chuyến đi nào phù hợp", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Lỗi: " + searchResponse.getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Lỗi máy chủ: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TripSearchResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi kết nối mạng. Vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                Log.e("BuyTicketFragment", "API call failed", t);
            }
        });
    }

    private String formatDateForApi(String dateInDdMmYyyy) {
        SimpleDateFormat fromUser = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat forApi = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = fromUser.parse(dateInDdMmYyyy);
            return forApi.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}