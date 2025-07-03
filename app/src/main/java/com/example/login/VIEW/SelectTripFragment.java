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
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.ADAPTERS.DateAdapter;
import com.example.login.ADAPTERS.TripAdapter;
import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.INTERFACES.OnDateSelectedListener;
import com.example.login.INTERFACES.OnTripSelectedListener;
import com.example.login.MODELS.DateModel;
import com.example.login.MODELS.FilterOption;
import com.example.login.MODELS.Trip;
import com.example.login.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class SelectTripFragment extends Fragment implements OnDateSelectedListener, FilterBottomSheetDialogFragment.FilterListener, OnTripSelectedListener {

    // Views
    private RecyclerView tripRecyclerView;
    private TripAdapter tripAdapter;
    private RecyclerView dateRecyclerView;
    private View layoutNoData;
    private TextView tvRouteTitle;
    private Button btnFilterPrice, btnFilterTime;

    // ViewModel & Data
    private SelectTripViewModel viewModel;
    private String departureLocation;
    private String destinationLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo ViewModel
        viewModel = new ViewModelProvider(this).get(SelectTripViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_trip, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupClickListeners();

        long selectedDateMillis = System.currentTimeMillis();
        if (getArguments() != null) {
            selectedDateMillis = getArguments().getLong("SELECTED_DATE_MILLIS", selectedDateMillis);
            departureLocation = getArguments().getString("DEPARTURE_LOCATION", "");
            destinationLocation = getArguments().getString("DESTINATION_LOCATION", "");
        }

        String routeTitle = departureLocation + " → " + destinationLocation;
        tvRouteTitle.setText(routeTitle);

        setupRecyclerViews();
        setupDateRecyclerView(selectedDateMillis);
        observeViewModel();

        // Gọi ViewModel để khởi tạo dữ liệu (chỉ chạy một lần duy nhất)
        viewModel.init(getArguments());
    }

    private void bindViews(View view) {
        tripRecyclerView = view.findViewById(R.id.trip_list_recycler_view);
        dateRecyclerView = view.findViewById(R.id.date_recycler_view);
        layoutNoData = view.findViewById(R.id.layout_no_data);
        tvRouteTitle = view.findViewById(R.id.tv_route_title);
        btnFilterPrice = view.findViewById(R.id.btn_filter_price);
        btnFilterTime = view.findViewById(R.id.btn_filter_time);
    }

    private void setupRecyclerViews() {
        tripRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Khởi tạo adapter rỗng và truyền 'this' làm listener
        tripAdapter = new TripAdapter(getContext(), new ArrayList<>(), this);
        tripRecyclerView.setAdapter(tripAdapter);
    }

    private void observeViewModel() {
        // Lắng nghe dữ liệu từ ViewModel
        viewModel.getFilteredTrips().observe(getViewLifecycleOwner(), trips -> {
            if (trips == null || trips.isEmpty()) {
                tripRecyclerView.setVisibility(View.GONE);
                layoutNoData.setVisibility(View.VISIBLE);
            } else {
                tripRecyclerView.setVisibility(View.VISIBLE);
                layoutNoData.setVisibility(View.GONE);
                // Cập nhật dữ liệu cho adapter
                tripAdapter.updateTrips(trips);
            }
        });
    }

    private void setupClickListeners() {
        btnFilterPrice.setOnClickListener(v -> showFilterDialog("Price"));
        btnFilterTime.setOnClickListener(v -> showFilterDialog("Hour"));
    }

    @Override
    public void onDateSelected(Date selectedDate) {
        Toast.makeText(getContext(), "Loading trips for new date...", Toast.LENGTH_SHORT).show();
        // Thông báo cho ViewModel biết ngày đã thay đổi để nó tự gọi API
        viewModel.fetchTripsForDate(departureLocation, destinationLocation, selectedDate);
    }

    @Override
    public void onTripSelected(Trip trip) {
        if (trip.getAvailableSeats() == -1) {
            Toast.makeText(getContext(), "Please wait, checking seat info...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (getView() != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("SELECTED_TRIP", trip);
            Navigation.findNavController(getView()).navigate(R.id.action_selectTrip_to_selectSeat, bundle);
        }
    }

    @Override
    public void onFilterApplied(String filterType, List<FilterOption> selectedOptions) {
        List<String> selectedNames = selectedOptions.stream()
                .filter(FilterOption::isSelected)
                .map(FilterOption::getName)
                .collect(Collectors.toList());

        switch (filterType) {
            case "Price":
                viewModel.setPriceFilter(selectedNames.isEmpty() ? "" : selectedNames.get(0));
                break;
            case "Seat types":
                viewModel.setSeatTypeFilters(selectedNames);
                break;
            case "Hour":
                List<String> timeKeywords = new ArrayList<>();
                for (String name : selectedNames) {
                    if (name.startsWith("Morning")) timeKeywords.add("Morning");
                    else if (name.startsWith("Afternoon")) timeKeywords.add("Afternoon");
                    else if (name.startsWith("Evening")) timeKeywords.add("Evening");
                }
                viewModel.setTimeFilters(timeKeywords);
                break;
        }
    }

    private void showFilterDialog(String filterType) {
        ArrayList<FilterOption> options = new ArrayList<>();
        // TODO: Đoạn code này cần truy cập vào trạng thái filter hiện tại từ ViewModel
        // Để đơn giản, tạm thời tạo mới. Bạn có thể nâng cấp sau.
        switch (filterType) {
            case "Price":
                options.add(new FilterOption("Price Ascending", false));
                options.add(new FilterOption("Price Descending", false));
                break;
            case "Seat types":
                options.add(new FilterOption("Chair", false));
                options.add(new FilterOption("Limousine", false));
                options.add(new FilterOption("Bed", false));
                break;
            case "Hour":
                options.add(new FilterOption("Morning (00:00 - 11:59)", false));
                options.add(new FilterOption("Afternoon (12:00 - 17:59)", false));
                options.add(new FilterOption("Evening (18:00 - 23:59)", false));
                break;
        }
        FilterBottomSheetDialogFragment dialog = FilterBottomSheetDialogFragment.newInstance(filterType, options);
        dialog.setFilterListener(this);
        dialog.show(getParentFragmentManager(), "FilterDialog");
    }

    private void setupDateRecyclerView(long selectedDateMillis) {
        List<DateModel> dates = new ArrayList<>();
        Calendar iterator = Calendar.getInstance();
        normalizeCalendar(iterator);
        for (int i = 0; i < 365; i++) {
            dates.add(new DateModel(iterator.getTime()));
            iterator.add(Calendar.DAY_OF_YEAR, 1);
        }
        Calendar today = Calendar.getInstance();
        normalizeCalendar(today);
        Calendar selectedDate = Calendar.getInstance();
        selectedDate.setTimeInMillis(selectedDateMillis);
        normalizeCalendar(selectedDate);
        long diffInMillis = selectedDate.getTimeInMillis() - today.getTimeInMillis();
        int initialSelectedPosition = (int) TimeUnit.MILLISECONDS.toDays(diffInMillis);
        if (initialSelectedPosition < 0) {
            initialSelectedPosition = 0;
        }
        DateAdapter dateAdapter = new DateAdapter(getContext(), dates, initialSelectedPosition, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        dateRecyclerView.setLayoutManager(layoutManager);
        dateRecyclerView.setAdapter(dateAdapter);
        int finalInitialSelectedPosition = initialSelectedPosition;
        dateRecyclerView.post(() -> layoutManager.scrollToPositionWithOffset(finalInitialSelectedPosition, 150));
    }

    private void normalizeCalendar(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
}