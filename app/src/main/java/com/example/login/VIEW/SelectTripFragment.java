package com.example.login.VIEW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.ADAPTERS.DateAdapter;
import com.example.login.ADAPTERS.SearchResultAdapter;
import com.example.login.MODELS.DateModel;
import com.example.login.MODELS.TripSearchResponse;
import com.example.login.MODELS.TripSearchResult;
import com.example.login.R;

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

public class SelectTripFragment extends Fragment implements DateAdapter.OnDateClickListener { // THAY ĐỔI: Implement OnDateClickListener

    private Toolbar toolbar;
    private TextView tvRouteTitle;
    private RecyclerView dateRecyclerView;
    private DateAdapter dateAdapter;
    private Button btnFilterPrice;
    private Button btnFilterTime;
    private LinearLayout layoutNoData;

    private RecyclerView tripListRecyclerView;
    private SearchResultAdapter searchResultAdapter;

    private ApiService apiService;

    private String originCity;
    private String destinationCity;
    private String departureDateString; // Ngày đi (String từ API)
    private Date selectedSearchDate; // Ngày được chọn trên dateRecyclerView (Date object)

    private final SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US); // Format chuẩn API
    private final SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()); // Format hiển thị ngày (vd: 04/07/2025)

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_trip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = ApiClient.getPublicApiService();

        bindViews(view);
        setupToolbar();
        setupDateRecyclerView(); // Setup RecyclerView cho ngày
        setupFilterButtons(); // Setup các nút filter
        setupTripListRecyclerView(); // Setup RecyclerView chính hiển thị kết quả

        // Lấy arguments từ Bundle
        if (getArguments() != null) {
            originCity = getArguments().getString("originCity");
            destinationCity = getArguments().getString("destinationCity");
            departureDateString = getArguments().getString("departureDate"); // Lấy ngày đi dạng String

            tvRouteTitle.setText(originCity + " → " + destinationCity);

            // Chuyển đổi departureDateString thành Date và set cho dateAdapter
            try {
                selectedSearchDate = apiDateFormat.parse(departureDateString);
            } catch (java.text.ParseException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Lỗi định dạng ngày", Toast.LENGTH_SHORT).show();
                selectedSearchDate = Calendar.getInstance().getTime(); // Fallback to current date
            }

            // Cập nhật ngày cho dateAdapter (quan trọng để ngày tìm kiếm ban đầu được highlight)
            // Lần đầu tải dates cho DateAdapter sẽ nằm trong setupDateRecyclerView(),
            // sau đó gọi setSelectedDate để highlight ngày tương ứng.
            // dateAdapter.setSelectedDate(selectedSearchDate); // Gọi sau khi dates được submitList ban đầu
            // TẠI SAO PHẢI THỰC HIỆN LẠI TÌM KIẾM Ở ĐÂY?
            // Vì backend API searchTrips đã trả về kết quả cho ngày này rồi
            // Chúng ta chỉ cần submit list results.
            List<TripSearchResult> results = (List<TripSearchResult>) getArguments().getSerializable("searchResults");
            if (results != null && !results.isEmpty()) {
                searchResultAdapter.submitList(results);
                tripListRecyclerView.setVisibility(View.VISIBLE);
                layoutNoData.setVisibility(View.GONE);
            } else {
                searchResultAdapter.submitList(null);
                tripListRecyclerView.setVisibility(View.GONE);
                layoutNoData.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Không tìm thấy chuyến đi nào phù hợp.", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Trường hợp không có arguments, hiển thị "No data"
            searchResultAdapter.submitList(null);
            tripListRecyclerView.setVisibility(View.GONE);
            layoutNoData.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Không có dữ liệu tìm kiếm.", Toast.LENGTH_SHORT).show();
        }
    }

    private void bindViews(View view) {
        toolbar = view.findViewById(R.id.toolbar_select_trip);
        tvRouteTitle = view.findViewById(R.id.tv_route_title);
        dateRecyclerView = view.findViewById(R.id.date_recycler_view);
        btnFilterPrice = view.findViewById(R.id.btn_filter_price);
        btnFilterTime = view.findViewById(R.id.btn_filter_time);
        tripListRecyclerView = view.findViewById(R.id.trip_list_recycler_view);
        layoutNoData = view.findViewById(R.id.layout_no_data);
    }

    private void setupToolbar() {
        if (getActivity() instanceof AppCompatActivity) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            activity.setSupportActionBar(toolbar);
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setupDateRecyclerView() {
        // Khởi tạo Adapter
        // Pass 'this' (Fragment) as listener because it implements OnDateClickListener
        dateAdapter = new DateAdapter(requireContext(), this);
        dateRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        dateRecyclerView.setAdapter(dateAdapter);

        // Tạo danh sách ngày (ví dụ: 30 ngày tới)
        List<DateModel> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 30; i++) { // Hiển thị 30 ngày tới
            dates.add(new DateModel(calendar.getTime(), false));
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        dateAdapter.updateDates(dates, selectedSearchDate); // Gửi dates và ngày tìm kiếm ban đầu để highlight
    }

    private void setupFilterButtons() {
        btnFilterPrice.setOnClickListener(v -> Toast.makeText(getContext(), "Nút Lọc Giá", Toast.LENGTH_SHORT).show());
        btnFilterTime.setOnClickListener(v -> Toast.makeText(getContext(), "Nút Lọc Giờ", Toast.LENGTH_SHORT).show());
    }

    private void setupTripListRecyclerView() {
        searchResultAdapter = new SearchResultAdapter();
        tripListRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tripListRecyclerView.setAdapter(searchResultAdapter);

        searchResultAdapter.setOnItemClickListener(trip -> {
            Toast.makeText(getContext(), "Đã chọn chuyến: " + trip.getItinerary().getName() + " Giá: " + trip.getPriceForSelectedSegment(), Toast.LENGTH_SHORT).show();

            String selectedTripId = trip.getId();
            String selectedOriginStationId = trip.getItinerary().getBaseRoute().getOriginStation().getId();
            String selectedDestinationStationId = trip.getItinerary().getBaseRoute().getDestinationStation().getId();

            Bundle bundle = new Bundle();
            bundle.putString("tripId", selectedTripId);
            bundle.putString("originStopId", selectedOriginStationId);
            bundle.putString("destinationStopId", selectedDestinationStationId);

            Navigation.findNavController(requireView()).navigate(R.id.action_selectTrip_to_selectSeat, bundle);
        });
    }

    // === THÊM PHƯƠNG THỨC NÀY ĐỂ XỬ LÝ KHI NGƯỜI DÙNG CHỌN NGÀY MỚI ===
    @Override
    public void onDateSelected(Date selectedDate) {
        this.selectedSearchDate = selectedDate; // Cập nhật ngày đã chọn
        Toast.makeText(getContext(), "Selected date from RecyclerView: " + displayDateFormat.format(selectedDate), Toast.LENGTH_SHORT).show();

        // Gọi lại API tìm kiếm với ngày mới
        performSearchWithNewCriteria(originCity, destinationCity, selectedSearchDate);
    }

    private void performSearchWithNewCriteria(String originCity, String destinationCity, Date newDepartureDate) {
        String formattedDate = apiDateFormat.format(newDepartureDate); // Định dạng ngày cho API

        // Hiển thị loading state (nếu có ProgressBar riêng cho kết quả)
        // searchProgressBar.setVisibility(View.VISIBLE); // Không có ProgressBar riêng

        // Ẩn kết quả cũ
        tripListRecyclerView.setVisibility(View.GONE);
        layoutNoData.setVisibility(View.GONE);

        apiService.searchTrips(originCity, destinationCity, formattedDate)
                .enqueue(new Callback<TripSearchResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<TripSearchResponse> call, @NonNull Response<TripSearchResponse> response) {
                        // searchProgressBar.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            List<TripSearchResult> results = response.body().getData();
                            if (results != null && !results.isEmpty()) {
                                searchResultAdapter.submitList(results);
                                tripListRecyclerView.setVisibility(View.VISIBLE);
                                layoutNoData.setVisibility(View.GONE);
                            } else {
                                searchResultAdapter.submitList(null);
                                tripListRecyclerView.setVisibility(View.GONE);
                                layoutNoData.setVisibility(View.VISIBLE);
                                Toast.makeText(getContext(), "Không tìm thấy chuyến đi nào phù hợp.", Toast.LENGTH_SHORT).show();
                            }
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
                            searchResultAdapter.submitList(null);
                            tripListRecyclerView.setVisibility(View.GONE);
                            layoutNoData.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<TripSearchResponse> call, @NonNull Throwable t) {
                        // searchProgressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        searchResultAdapter.submitList(null);
                        tripListRecyclerView.setVisibility(View.GONE);
                        layoutNoData.setVisibility(View.VISIBLE);
                    }
                });
    }
}