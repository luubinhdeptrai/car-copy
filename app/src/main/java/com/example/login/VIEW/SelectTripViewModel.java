package com.example.login.VIEW;

import android.os.Bundle;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.MODELS.TicketSeatListResponse;
import com.example.login.MODELS.TicketSeatResponse;
import com.example.login.MODELS.Trip;
import com.example.login.MODELS.TripSearchResponse;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectTripViewModel extends ViewModel {

    // --- LiveData để Fragment lắng nghe ---
    private final MutableLiveData<List<Trip>> _filteredTrips = new MutableLiveData<>();
    public LiveData<List<Trip>> getFilteredTrips() {
        return _filteredTrips;
    }

    // --- Dữ liệu gốc và trạng thái ---
    private List<Trip> allTrips = new ArrayList<>();
    private boolean isDataInitialized = false;

    // --- Trạng thái bộ lọc ---
    private String selectedPriceFilter = "";
    private List<String> selectedSeatTypeFilters = new ArrayList<>();
    private List<String> selectedTimeFilters = new ArrayList<>();

    // Phương thức này chỉ được gọi một lần duy nhất
    public void init(Bundle args) {
        if (isDataInitialized || args == null) {
            return;
        }
        List<Trip> initialTrips = (List<Trip>) args.getSerializable("TRIPS_RESULT");
        if (initialTrips != null) {
            this.allTrips = initialTrips;
            if (allTrips.isEmpty()) {
                _filteredTrips.setValue(new ArrayList<>()); // Cập nhật để hiển thị "No data"
            } else {
                fetchAvailableSeatsForAllTrips();
            }
        }
        isDataInitialized = true;
    }

    private void fetchAvailableSeatsForAllTrips() {
        ApiService apiService = ApiClient.getNoAuthAPI();
        if (allTrips.isEmpty()) {
            applyAllFilters(); // Áp dụng bộ lọc cho danh sách rỗng (để hiển thị "No data")
            return;
        }
        AtomicInteger counter = new AtomicInteger(allTrips.size());

        for (final Trip trip : allTrips) {
            apiService.getTicketsForTrip(trip.getId()).enqueue(new Callback<TicketSeatListResponse>() {
                @Override
                public void onResponse(Call<TicketSeatListResponse> call, Response<TicketSeatListResponse> response) {
                    int availableCount = 0;
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<TicketSeatResponse> allSeats = response.body().getData();
                        if (allSeats != null) {
                            for (TicketSeatResponse seat : allSeats) {
                                if ("available".equalsIgnoreCase(seat.getStatus())) {
                                    availableCount++;
                                }
                            }
                        }
                    }
                    trip.setAvailableSeats(availableCount);
                    if (counter.decrementAndGet() == 0) {
                        applyAllFilters();
                    }
                }

                @Override
                public void onFailure(Call<TicketSeatListResponse> call, Throwable t) {
                    trip.setAvailableSeats(0);
                    if (counter.decrementAndGet() == 0) {
                        applyAllFilters();
                    }
                }
            });
        }
    }

    // THÊM: Hàm mới để xử lý việc chọn ngày
    public void fetchTripsForDate(String origin, String destination, Date date) {
        SimpleDateFormat forApi = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = forApi.format(date);

        ApiService apiService = ApiClient.getNoAuthAPI();
        apiService.searchTrips(origin, destination, formattedDate).enqueue(new Callback<TripSearchResponse>() {
            @Override
            public void onResponse(Call<TripSearchResponse> call, Response<TripSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allTrips = response.body().getData() != null ? response.body().getData() : new ArrayList<>();
                    fetchAvailableSeatsForAllTrips(); // Gọi lại để lấy số ghế và áp dụng bộ lọc
                } else {
                    allTrips.clear();
                    _filteredTrips.setValue(allTrips); // Cập nhật UI với danh sách rỗng
                }
            }

            @Override
            public void onFailure(Call<TripSearchResponse> call, Throwable t) {
                allTrips.clear();
                _filteredTrips.setValue(allTrips); // Cập nhật UI với danh sách rỗng
            }
        });
    }

    public void setPriceFilter(String priceFilter) {
        this.selectedPriceFilter = priceFilter;
        applyAllFilters();
    }

    public void setSeatTypeFilters(List<String> seatTypeFilters) {
        this.selectedSeatTypeFilters = seatTypeFilters;
        applyAllFilters();
    }

    public void setTimeFilters(List<String> timeFilters) {
        this.selectedTimeFilters = timeFilters;
        applyAllFilters();
    }

    private void applyAllFilters() {
        List<Trip> filteredList = new ArrayList<>(allTrips);

        if (!selectedSeatTypeFilters.isEmpty()) {
            filteredList = filteredList.stream()
                    .filter(trip -> trip.getVehicle() != null && selectedSeatTypeFilters.contains(trip.getVehicle().getType()))
                    .collect(Collectors.toList());
        }

        if (!selectedTimeFilters.isEmpty()) {
            filteredList = filteredList.stream()
                    .filter(trip -> {
                        int hour = getHourFromUtcString(trip.getDepartureTime());
                        if (hour == -1) return false;
                        for (String timeFilter : selectedTimeFilters) {
                            if (timeFilter.equals("Morning") && hour >= 0 && hour < 12) return true;
                            if (timeFilter.equals("Afternoon") && hour >= 12 && hour < 18) return true;
                            if (timeFilter.equals("Evening") && hour >= 18 && hour < 24) return true;
                        }
                        return false;
                    })
                    .collect(Collectors.toList());
        }

        if (!selectedPriceFilter.isEmpty()) {
            if (selectedPriceFilter.equals("Price Ascending")) {
                filteredList.sort(Comparator.comparingDouble(Trip::getPrice));
            } else if (selectedPriceFilter.equals("Price Descending")) {
                filteredList.sort(Comparator.comparingDouble(Trip::getPrice).reversed());
            }
        }
        _filteredTrips.setValue(filteredList);
    }

    private int getHourFromUtcString(String utcDateString) {
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = utcFormat.parse(utcDateString);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            return calendar.get(Calendar.HOUR_OF_DAY);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }
}