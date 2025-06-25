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

    // LiveData để giữ danh sách ghế, Fragment sẽ lắng nghe sự thay đổi của nó
    private final MutableLiveData<List<Seat>> _seatList = new MutableLiveData<>();
    public LiveData<List<Seat>> getSeatList() {
        return _seatList;
    }

    // Cờ để đảm bảo chỉ gọi API một lần duy nhất
    private boolean areSeatsLoaded = false;

    // Phương thức để Fragment gọi khi cần tải dữ liệu ghế
    public void fetchSeats(ApiService apiService, String tripId, int capacity) {
        if (areSeatsLoaded) {
            return; // Nếu đã tải rồi thì không làm gì cả
        }

        apiService.getTicketsForTrip(tripId).enqueue(new Callback<TicketSeatListResponse>() {
            @Override
            public void onResponse(Call<TicketSeatListResponse> call, Response<TicketSeatListResponse> response) {
                List<Seat> freshSeatList = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    List<TicketSeatResponse> apiSeats = response.body().getData();
                    Map<String, String> apiSeatStatusMap = new HashMap<>();
                    if (apiSeats != null) {
                        for (TicketSeatResponse ticket : apiSeats) {
                            apiSeatStatusMap.put(ticket.getSeatNumber(), ticket.getStatus());
                        }
                    }
                    for (int i = 1; i <= capacity; i++) {
                        String seatNumDisplay = String.format(Locale.US, "%02d", i);
                        String seatNumLookup = String.valueOf(i);
                        String status = apiSeatStatusMap.getOrDefault(seatNumLookup, "available");
                        freshSeatList.add(new Seat(seatNumDisplay, status));
                    }
                } else {
                    // Nếu có lỗi, tạo danh sách ghế mặc định
                    for (int i = 1; i <= capacity; i++) {
                        freshSeatList.add(new Seat(String.format(Locale.US, "%02d", i), "available"));
                    }
                }
                // Cập nhật LiveData, Fragment sẽ tự động nhận được dữ liệu mới
                _seatList.postValue(freshSeatList);
                areSeatsLoaded = true; // Đánh dấu đã tải xong
            }

            @Override
            public void onFailure(Call<TicketSeatListResponse> call, Throwable t) {
                // Xử lý lỗi mạng
                Log.e("SelectSeatViewModel", "API call failed", t);
                List<Seat> fallbackList = new ArrayList<>();
                for (int i = 1; i <= capacity; i++) {
                    fallbackList.add(new Seat(String.format(Locale.US, "%02d", i), "available"));
                }
                _seatList.postValue(fallbackList);
                areSeatsLoaded = true;
            }
        });
    }

    // Phương thức để xử lý việc chọn/bỏ chọn một ghế
    public void toggleSeatSelection(Seat clickedSeat) {
        List<Seat> currentList = _seatList.getValue();
        if (currentList == null) return;

        for (Seat seat : currentList) {
            if (seat.getSeatNumber().equals(clickedSeat.getSeatNumber())) {
                if (seat.getStatus() == SeatStatus.AVAILABLE) {
                    seat.setStatus(SeatStatus.SELECTED);
                } else if (seat.getStatus() == SeatStatus.SELECTED) {
                    seat.setStatus(SeatStatus.AVAILABLE);
                }
                break;
            }
        }
        // Cập nhật lại LiveData để giao diện được vẽ lại với trạng thái mới
        _seatList.setValue(currentList);
    }
}