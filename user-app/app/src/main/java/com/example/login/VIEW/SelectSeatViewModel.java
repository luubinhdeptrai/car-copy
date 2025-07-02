package com.example.login.VIEW;

import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.login.API.ApiService;
import com.example.login.MODELS.Seat;
import com.example.login.MODELS.SeatStatus;
import com.example.login.MODELS.TicketSeatListResponse;
import com.example.login.MODELS.TicketSeatResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectSeatViewModel extends ViewModel {

    private final MutableLiveData<List<Seat>> _seatList = new MutableLiveData<>();
    public LiveData<List<Seat>> getSeatList() {
        return _seatList;
    }

    private boolean areSeatsLoaded = false;

    public void fetchSeats(ApiService apiService, String tripId, int capacity) {
        if (areSeatsLoaded) {
            return;
        }

        apiService.getTicketsForTrip(tripId).enqueue(new Callback<TicketSeatListResponse>() {
            @Override
            public void onResponse(Call<TicketSeatListResponse> call, Response<TicketSeatListResponse> response) {
                List<Seat> freshSeatList = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<TicketSeatResponse> apiSeats = response.body().getData();
                    Map<String, TicketSeatResponse> apiSeatMap = new HashMap<>();
                    if (apiSeats != null) {
                        for (TicketSeatResponse ticket : apiSeats) {
                            apiSeatMap.put(ticket.getSeatNumber(), ticket);
                        }
                    }

                    for (int i = 1; i <= capacity; i++) {
                        String seatNumLookup = String.valueOf(i);
                        String seatNumDisplay = String.format(Locale.US, "%02d", i);
                        TicketSeatResponse ticket = apiSeatMap.get(seatNumLookup);

                        if (ticket != null) {
                            // Use ticket data if it exists
                            freshSeatList.add(new Seat(ticket.getId(), seatNumDisplay, ticket.getStatus()));
                        } else {
                            // Otherwise, it's an available seat with no ticket ID yet
                            freshSeatList.add(new Seat(null, seatNumDisplay, "available"));
                        }
                    }
                } else {
                    // On error, create a default list of available seats
                    for (int i = 1; i <= capacity; i++) {
                        String seatNumDisplay = String.format(Locale.US, "%02d", i);
                        freshSeatList.add(new Seat(null, seatNumDisplay, "available"));
                    }
                }
                _seatList.postValue(freshSeatList);
                areSeatsLoaded = true;
            }

            @Override
            public void onFailure(Call<TicketSeatListResponse> call, Throwable t) {
                Log.e("SelectSeatViewModel", "API call failed", t);
                List<Seat> fallbackList = new ArrayList<>();
                for (int i = 1; i <= capacity; i++) {
                    String seatNumDisplay = String.format(Locale.US, "%02d", i);
                    fallbackList.add(new Seat(null, seatNumDisplay, "available"));
                }
                _seatList.postValue(fallbackList);
                areSeatsLoaded = true;
            }
        });
    }

    public void toggleSeatSelection(Seat seatToToggle) {
        List<Seat> currentList = _seatList.getValue();
        if (currentList == null) return;

        List<Seat> newList = new ArrayList<>();
        for (Seat seat : currentList) {
            if (seat.getSeatNumber().equals(seatToToggle.getSeatNumber())) {
                if (seat.getStatus() == SeatStatus.AVAILABLE) {
                    seat.setStatus(SeatStatus.SELECTED);
                } else if (seat.getStatus() == SeatStatus.SELECTED) {
                    seat.setStatus(SeatStatus.AVAILABLE);
                }
            }
            newList.add(seat);
        }
        _seatList.setValue(newList);
    }
}