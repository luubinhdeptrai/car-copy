package com.example.login.VIEW;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.MODELS.TripSearchResponse;
import com.example.login.MODELS.TripSearchResult;
import com.example.login.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchTripFragment extends Fragment {

    // Khai báo các View để khớp với fragment_search_trip.xml
    private AutoCompleteTextView actvDeparture, actvDestination;
    private TextView tvDepartureDateDisplay, tvDepartureDayDisplay; // TextView hiển thị ngày/thứ
    private Button searchButton;
    private Toolbar toolbar;
    private TextView toolbarTitle; // Tiêu đề Toolbar
    private Calendar selectedCalendar;
    private ImageView ivSwap;

    private ApiService apiService;

    // Formatter cho API (ISO 8601 UTC)
    private static final SimpleDateFormat apiDateFormat;
    // Formatter cho hiển thị ngày (dd/MM)
    private static final SimpleDateFormat displayDateFormat;
    // Formatter cho hiển thị thứ (ví dụ: Tuesday)
    private static final SimpleDateFormat dayOfWeekFormat;

    static {
        apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
        apiDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        displayDateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
        dayOfWeekFormat = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_trip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getPublicApiService();

        bindViews(view);
        setupToolbar();
        setupAutoCompleteTextViews();
        setupDatePicker();
        setupClickListeners(view);
    }

    private void bindViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title); // ID của TextView bên trong Toolbar
        actvDeparture = view.findViewById(R.id.actv_departure); // AutoCompleteTextView cho điểm đi
        actvDestination = view.findViewById(R.id.actv_destination); // AutoCompleteTextView cho điểm đến
        tvDepartureDateDisplay = view.findViewById(R.id.tv_departure_date); // TextView hiển thị ngày (vd: 25/06)
        tvDepartureDayDisplay = view.findViewById(R.id.tv_departure_day); // TextView hiển thị thứ (vd: Tuesday)
        searchButton = view.findViewById(R.id.search_route_button); // Nút "Tìm chuyến đi"
        ivSwap = view.findViewById(R.id.iv_swap); // Nút swap
        // Layout này không còn ProgressBar search riêng nữa, nên không cần gán:
        // searchProgressBar = view.findViewById(R.id.progress_bar_search);
        // searchResultsRecyclerView = view.findViewById(R.id.recycler_view_search_results);
        // layoutNoData = view.findViewById(R.id.layout_no_data);
    }

    private void setupToolbar() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false); // Ẩn title mặc định
            }
        }
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed()); // Xử lý nút back
        toolbarTitle.setText("Mua Vé"); // Đặt text cho toolbar, theo ý bạn trong XML
    }

    private void setupAutoCompleteTextViews() {
        String[] locations = {"TP.HCM", "Đà Lạt", "Cần Thơ", "Nha Trang", "Hà Nội", "Đà Nẵng", "Huế", "Vũng Tàu"}; // Danh sách các thành phố
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, locations);
        actvDeparture.setAdapter(adapter);
        actvDestination.setAdapter(adapter);
    }

    private void setupDatePicker() {
        selectedCalendar = Calendar.getInstance();
        updateDateViews(selectedCalendar); // Cập nhật hiển thị ngày ban đầu

        View.OnClickListener dateClickListener = v -> showDepartureDatePickerDialog();
        tvDepartureDateDisplay.setOnClickListener(dateClickListener);
        tvDepartureDayDisplay.setOnClickListener(dateClickListener);
    }

    private void showDepartureDatePickerDialog() {
        int year = selectedCalendar.get(Calendar.YEAR);
        int month = selectedCalendar.get(Calendar.MONTH);
        int day = selectedCalendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                R.style.AppDatePickerTheme, // Đảm bảo style này tồn tại trong styles.xml hoặc themes.xml
                (view, selectedYear, selectedMonthOfYear, selectedDayOfMonth) -> {
                    selectedCalendar.set(selectedYear, selectedMonthOfYear, selectedDayOfMonth);
                    updateDateViews(selectedCalendar); // Cập nhật hiển thị sau khi chọn ngày
                },
                year, month, day
        );

        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000); // Chỉ cho phép chọn từ ngày hiện tại trở đi
        datePickerDialog.show();
    }

    private void updateDateViews(Calendar calendar) {
        tvDepartureDateDisplay.setText(displayDateFormat.format(calendar.getTime()));
        tvDepartureDayDisplay.setText(dayOfWeekFormat.format(calendar.getTime()));
    }

    private void setupClickListeners(View view) {
        searchButton.setOnClickListener(v -> performSearch(view)); // Truyền View để Navigation tìm NavController
        ivSwap.setOnClickListener(v -> swapLocations());
    }

    private void swapLocations() {
        String origin = actvDeparture.getText().toString();
        String destination = actvDestination.getText().toString();
        actvDeparture.setText(destination);
        actvDestination.setText(origin);
    }

    private void performSearch(View view) { // Thêm View parameter
        String originCity = actvDeparture.getText().toString().trim();
        String destinationCity = actvDestination.getText().toString().trim();
        // Lấy ngày đã chọn và format theo chuẩn API
        String departureDate = apiDateFormat.format(selectedCalendar.getTime());

        if (originCity.isEmpty() || destinationCity.isEmpty() || departureDate.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin tìm kiếm.", Toast.LENGTH_SHORT).show();
            return;
        }

        // searchProgressBar.setVisibility(View.VISIBLE); // Không có ProgressBar search riêng nữa trong layout này
        searchButton.setEnabled(false); // Vô hiệu hóa nút tìm kiếm

        apiService.searchTrips(originCity, destinationCity, departureDate)
                .enqueue(new Callback<TripSearchResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TripSearchResponse> call, @NonNull Response<TripSearchResponse> response) {
                        searchButton.setEnabled(true); // Kích hoạt lại nút tìm kiếm
                        if (response.isSuccessful() && response.body() != null) {
                            List<TripSearchResult> results = response.body().getData();
                            // === ĐIỀU HƯỚNG VÀ TRUYỀN KẾT QUẢ ĐẾN SELECTTRIPFRAGMENT ===
                            Bundle bundle = new Bundle();
                            bundle.putString("originCity", originCity);
                            bundle.putString("destinationCity", destinationCity);
                            bundle.putString("departureDate", departureDate); // Ngày đã format cho API

                            if (results != null) {
                                bundle.putSerializable("searchResults", (Serializable) results);
                            } else {
                                bundle.putSerializable("searchResults", new ArrayList<TripSearchResult>());
                            }
                            // Điều hướng đến SelectTripFragment (màn hình hiển thị kết quả)
                            Navigation.findNavController(view).navigate(R.id.action_searchTrip_to_selectTrip, bundle);

                        } else {
                            String errorMessage = "Lỗi tìm kiếm: " + response.code();
                            if (response.errorBody() != null) {
                                try {
                                    errorMessage += " - " + response.errorBody().string();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            Toast.makeText(getContext(), errorMessage, Toast.LENGTH_LONG).show();
                            // Dù lỗi hay không có kết quả, vẫn điều hướng để hiển thị màn hình kết quả với thông báo thích hợp
                            Bundle bundle = new Bundle();
                            bundle.putString("originCity", originCity);
                            bundle.putString("destinationCity", destinationCity);
                            bundle.putString("departureDate", departureDate);
                            bundle.putSerializable("searchResults", new ArrayList<TripSearchResult>());
                            Navigation.findNavController(view).navigate(R.id.action_searchTrip_to_selectTrip, bundle);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TripSearchResponse> call, @NonNull Throwable t) {
                        searchButton.setEnabled(true); // Kích hoạt lại nút tìm kiếm
                        Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        // Dù lỗi kết nối, vẫn điều hướng để hiển thị màn hình kết quả với thông báo thích hợp
                        Bundle bundle = new Bundle();
                        bundle.putString("originCity", originCity);
                        bundle.putString("destinationCity", destinationCity);
                        bundle.putString("departureDate", departureDate);
                        bundle.putSerializable("searchResults", new ArrayList<TripSearchResult>());
                        Navigation.findNavController(view).navigate(R.id.action_searchTrip_to_selectTrip, bundle);
                    }
                });
    }
}