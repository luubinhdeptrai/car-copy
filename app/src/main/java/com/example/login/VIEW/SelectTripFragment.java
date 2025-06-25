package com.example.login.VIEW;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.ADAPTERS.DateAdapter;
import com.example.login.ADAPTERS.TripAdapter;
import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.INTERFACES.OnDateSelectedListener;
import com.example.login.MODELS.DateModel;
import com.example.login.MODELS.TicketSeatListResponse;
import com.example.login.MODELS.TicketSeatResponse;
import com.example.login.MODELS.Trip;
import com.example.login.MODELS.TripSearchResponse;
import com.example.login.R;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectTripFragment extends Fragment implements OnDateSelectedListener {

    private RecyclerView tripRecyclerView;
    private TripAdapter tripAdapter;
    private List<Trip> trips;
    private RecyclerView dateRecyclerView;
    private DateAdapter dateAdapter;
    private List<DateModel> dates;
    private View layoutNoData;
    private TextView tvRouteTitle;
    private String departureLocation;
    private String destinationLocation;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_trip, container, false);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tripRecyclerView = view.findViewById(R.id.trip_list_recycler_view);
        dateRecyclerView = view.findViewById(R.id.date_recycler_view);
        layoutNoData = view.findViewById(R.id.layout_no_data);
        tvRouteTitle = view.findViewById(R.id.tv_route_title);
        long selectedDateMillis = System.currentTimeMillis();

        if (getArguments() != null) {
            Serializable serializableTrips = getArguments().getSerializable("TRIPS_RESULT");
            if (serializableTrips instanceof List) {
                trips = (List<Trip>) serializableTrips;
            }
            selectedDateMillis = getArguments().getLong("SELECTED_DATE_MILLIS", selectedDateMillis);
            departureLocation = getArguments().getString("DEPARTURE_LOCATION", "");
            destinationLocation = getArguments().getString("DESTINATION_LOCATION", "");
        }

        String routeTitle = departureLocation + " → " + destinationLocation;
        tvRouteTitle.setText(routeTitle);

        if (trips == null) {
            trips = new ArrayList<>();
        }

        setupDateRecyclerView(selectedDateMillis);

        // THAY ĐỔI: Không cập nhật danh sách ngay, mà gọi hàm fetch số ghế trước
        if (!trips.isEmpty()) {
            fetchAvailableSeatsForAllTrips(trips);
        } else {
            updateTripList(trips); // Cập nhật để hiện màn hình "No data"
        }
    }

    private void updateTripList(List<Trip> newTrips) {
        this.trips = newTrips;
        if (trips.isEmpty()) {
            tripRecyclerView.setVisibility(View.GONE);
            layoutNoData.setVisibility(View.VISIBLE);
        } else {
            tripRecyclerView.setVisibility(View.VISIBLE);
            layoutNoData.setVisibility(View.GONE);
            tripRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            tripAdapter = new TripAdapter(getContext(), trips);
            tripRecyclerView.setAdapter(tripAdapter);
        }
    }

    // THÊM MỚI: Toàn bộ phương thức này được thêm vào
    private void fetchAvailableSeatsForAllTrips(List<Trip> tripsToProcess) {
        // Cập nhật UI ngay lập tức để người dùng thấy danh sách chuyến đi
        // Adapter sẽ hiển thị "Đang kiểm tra..." cho số ghế
        updateTripList(tripsToProcess);

        ApiService apiService = ApiClient.getNoAuthAPI();

        for (final Trip trip : tripsToProcess) {
            apiService.getTicketsForTrip(trip.getId()).enqueue(new Callback<TicketSeatListResponse>() {
                @Override
                public void onResponse(@NonNull Call<TicketSeatListResponse> call, @NonNull Response<TicketSeatListResponse> response) {
                    int availableCount = 0;
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        List<TicketSeatResponse> allSeats = response.body().getData();
                        if (allSeats != null) {
                            // Đếm chính xác số ghế có trạng thái "available"
                            for (TicketSeatResponse seat : allSeats) {
                                if (seat.getStatus() != null && "available".equalsIgnoreCase(seat.getStatus())) {
                                    availableCount++;
                                }
                            }
                        }
                    }
                    // Cập nhật số ghế vào đối tượng Trip
                    trip.setAvailableSeats(availableCount);

                    // Báo cho adapter biết item này đã thay đổi để cập nhật UI
                    if (tripAdapter != null) {
                        int position = trips.indexOf(trip);
                        if (position != -1) {
                            tripAdapter.notifyItemChanged(position);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<TicketSeatListResponse> call, @NonNull Throwable t) {
                    trip.setAvailableSeats(0); // Coi như là 0 nếu lỗi
                    if (tripAdapter != null) {
                        int position = trips.indexOf(trip);
                        if (position != -1) {
                            tripAdapter.notifyItemChanged(position);
                        }
                    }
                }
            });
        }
    }


    private void setupDateRecyclerView(long selectedDateMillis) {
        dates = new ArrayList<>();
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
        dateAdapter = new DateAdapter(getContext(), dates, initialSelectedPosition, this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        dateRecyclerView.setLayoutManager(layoutManager);
        dateRecyclerView.setAdapter(dateAdapter);
        final int positionToScroll = initialSelectedPosition;
        dateRecyclerView.post(() -> layoutManager.scrollToPositionWithOffset(positionToScroll, 150));
    }

    private void normalizeCalendar(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    @Override
    public void onDateSelected(Date selectedDate) {
        Toast.makeText(getContext(), "Đang tải các chuyến đi...", Toast.LENGTH_SHORT).show();
        fetchTripsForDate(selectedDate);
    }

    private void fetchTripsForDate(Date date) {
        SimpleDateFormat forApi = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = forApi.format(date);
        ApiService apiService = ApiClient.getNoAuthAPI();
        Call<TripSearchResponse> call = apiService.searchTrips(departureLocation, destinationLocation, formattedDate);
        call.enqueue(new Callback<TripSearchResponse>() {
            @Override
            public void onResponse(Call<TripSearchResponse> call, Response<TripSearchResponse> response) {
                List<Trip> newTrips = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    if (response.body().getData() != null) {
                        newTrips = response.body().getData();
                    }
                }
                // THAY ĐỔI: Gọi hàm fetch số ghế cho danh sách chuyến đi mới
                if (!newTrips.isEmpty()) {
                    fetchAvailableSeatsForAllTrips(newTrips);
                } else {
                    updateTripList(newTrips);
                }
            }

            @Override
            public void onFailure(Call<TripSearchResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                updateTripList(new ArrayList<>());
            }
        });
    }
}