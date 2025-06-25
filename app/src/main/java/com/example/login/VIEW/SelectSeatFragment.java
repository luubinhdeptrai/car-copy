package com.example.login.VIEW;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.MODELS.Seat;
import com.example.login.MODELS.SeatStatus;
import com.example.login.MODELS.TicketSeatResponse;
import com.example.login.MODELS.TicketSeatListResponse; // <-- Import mới
import com.example.login.MODELS.Trip;
import com.example.login.R;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelectSeatFragment extends Fragment {

    // Views
    private GridLayout seatGridLeft, seatGridRight;
    private TextView selectedSeatsInfo, priceText;
    private Button continueButton;
    private Toolbar toolbar;

    // Data
    private Trip selectedTrip;
    private List<Seat> seatList = new ArrayList<>();
    private List<Seat> selectedSeats = new ArrayList<>();
    private double pricePerSeat;
    private ApiService apiService; // Khai báo ApiService

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_seat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo ApiService sử dụng ApiClient.getAuthAPI()
        apiService = ApiClient.getAuthAPI(getContext());

        // 1. Nhận đối tượng Trip từ Bundle
        if (getArguments() != null) {
            selectedTrip = (Trip) getArguments().getSerializable("SELECTED_TRIP");
        }

        // Nếu không nhận được dữ liệu, báo lỗi và quay lại màn hình trước
        if (selectedTrip == null) {
            Toast.makeText(getContext(), "Lỗi: Không nhận được thông tin chuyến đi.", Toast.LENGTH_LONG).show();
            Navigation.findNavController(view).popBackStack();
            return;
        }

        // 2. Ánh xạ các view
        bindViews(view);

        // 3. Thiết lập dữ liệu và tạo giao diện ghế ngồi
        pricePerSeat = selectedTrip.getPrice();
        createAndPopulateSeats(); // Phương thức này sẽ gọi API để lấy trạng thái ghế
        updateSummary(); // Cập nhật summary ban đầu (sẽ là 0 ghế)

        // 4. Gắn sự kiện cho các nút
        setupClickListeners(view);
    }

    private void bindViews(View view) {
        seatGridLeft = view.findViewById(R.id.seat_grid_left);
        seatGridRight = view.findViewById(R.id.seat_grid_right);
        selectedSeatsInfo = view.findViewById(R.id.selected_seats_info);
        priceText = view.findViewById(R.id.price_text);
        continueButton = view.findViewById(R.id.continue_button);
        toolbar = view.findViewById(R.id.toolbar_select_seat);
    }

    private void setupClickListeners(View view) {
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());

        continueButton.setOnClickListener(v -> {
            if (selectedSeats.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất một ghế.", Toast.LENGTH_SHORT).show();
            } else {
                // Điều hướng sang màn hình xác nhận, gửi theo thông tin chuyến và ghế đã chọn
                Bundle bundle = new Bundle();
                bundle.putSerializable("SELECTED_TRIP_FINAL", selectedTrip);
                bundle.putSerializable("SELECTED_SEATS_FINAL", (ArrayList<Seat>) selectedSeats);
                Navigation.findNavController(v).navigate(R.id.action_selectSeat_to_confirmation, bundle);
            }
        });
    }

    private void createAndPopulateSeats() {
        int capacity = selectedTrip.getVehicle().getCapacity();
        seatList.clear(); // Xóa dữ liệu cũ

        String tripId = selectedTrip.getId(); // Lấy ID của chuyến đi
        if (tripId == null || tripId.isEmpty()) {
            Toast.makeText(getContext(), "Lỗi: Không có ID chuyến đi.", Toast.LENGTH_LONG).show();
            // Fallback nếu không có tripId, tạo tất cả ghế là AVAILABLE
            initializeSeatsFallback(capacity);
            populateSeatGrid();
            return;
        }

        // Thực hiện cuộc gọi API để lấy trạng thái ghế
        // THAY ĐỔI: Kiểu dữ liệu mong đợi từ List<TicketSeatResponse> thành TicketSeatListResponse
        apiService.getTicketsForTrip(tripId).enqueue(new Callback<TicketSeatListResponse>() {
            @Override
            public void onResponse(@NonNull Call<TicketSeatListResponse> call, @NonNull Response<TicketSeatListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TicketSeatListResponse ticketSeatListResponse = response.body(); // Lấy đối tượng wrapper
                    // THAY ĐỔI: Kiểm tra trường 'success' và lấy danh sách 'data'
                    if (ticketSeatListResponse.isSuccess()) {
                        List<TicketSeatResponse> apiSeats = ticketSeatListResponse.getData(); // Lấy danh sách thực tế
                        Map<String, String> apiSeatStatusMap = new HashMap<>();

                        // Đổ dữ liệu từ API vào Map để dễ dàng tra cứu
                        for (TicketSeatResponse ticket : apiSeats) {
                            apiSeatStatusMap.put(ticket.getSeatNumber(), ticket.getStatus());
                        }

                        // Duyệt qua tất cả các ghế (theo sức chứa của xe)
                        for (int i = 1; i <= capacity; i++) {
                            String seatNumber = String.format(Locale.US, "%02d", i);
                            String backendStatus = apiSeatStatusMap.getOrDefault(seatNumber, "available"); // Mặc định là "available" nếu không tìm thấy trong API

                            seatList.add(new Seat(seatNumber, backendStatus)); // Sử dụng constructor mới trong Seat
                        }
                    } else {
                        Log.e("SelectSeatFragment", "API call successful but 'success' field is false.");
                        Toast.makeText(getContext(), "Lỗi tải thông tin ghế: Server báo lỗi", Toast.LENGTH_LONG).show();
                        initializeSeatsFallback(capacity);
                    }
                } else {
                    Log.e("SelectSeatFragment", "API call unsuccessful: " + response.code() + " " + response.message());
                    Toast.makeText(getContext(), "Lỗi tải thông tin ghế: " + response.message(), Toast.LENGTH_LONG).show();
                    // Fallback nếu API không thành công
                    initializeSeatsFallback(capacity);
                }
                populateSeatGrid(); // Gọi populateSeatGrid sau khi dữ liệu đã sẵn sàng
            }

            @Override
            public void onFailure(@NonNull Call<TicketSeatListResponse> call, @NonNull Throwable t) { // <-- Thay đổi ở đây
                Log.e("SelectSeatFragment", "API call failed: " + t.getMessage(), t);
                Toast.makeText(getContext(), "Không thể kết nối đến máy chủ để tải ghế.", Toast.LENGTH_LONG).show();
                // Fallback nếu kết nối mạng thất bại
                initializeSeatsFallback(capacity);
                populateSeatGrid(); // Vẫn gọi để hiển thị gì đó
            }
        });
    }

    private void initializeSeatsFallback(int capacity) {
        // Fallback: khởi tạo tất cả ghế là AVAILABLE nếu không thể lấy dữ liệu từ API
        seatList.clear();
        for (int i = 1; i <= capacity; i++) {
            String seatNumber = String.format(Locale.US, "%02d", i);
            seatList.add(new Seat(seatNumber, "available")); // Khởi tạo với trạng thái backend "available"
        }
    }

    private void populateSeatGrid() {
        seatGridLeft.removeAllViews();
        seatGridRight.removeAllViews();

        for (Seat seat : seatList) {
            Button seatButton = createSeatButton(seat);
            int seatNum = Integer.parseInt(seat.getSeatNumber());

            // Giả sử xe có 4 ghế mỗi hàng (sơ đồ 2-2)
            int positionInRow = (seatNum - 1) % 4;
            if (positionInRow == 0 || positionInRow == 1) { // Ghế 1, 2 của hàng
                seatGridLeft.addView(seatButton);
            } else { // Ghế 3, 4 của hàng
                seatGridRight.addView(seatButton);
            }
        }
    }

    private Button createSeatButton(Seat seat) {
        Button seatButton = new Button(getContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = (int) getResources().getDimension(R.dimen.seat_button_size);
        params.height = (int) getResources().getDimension(R.dimen.seat_button_size);
        int margin = (int) getResources().getDimension(R.dimen.seat_button_margin);
        params.setMargins(margin, margin, margin, margin);
        seatButton.setLayoutParams(params);
        seatButton.setText(seat.getSeatNumber());

        // Cập nhật trạng thái hiển thị và khả năng tương tác của nút
        updateSeatButtonAppearance(seatButton, seat);

        // Logic để làm mờ và vô hiệu hóa các ghế không phải AVAILABLE
        // Ghế SOLD_OUT (từ backend 'booked', 'locked', 'pending_approval') sẽ bị làm mờ và không cho chọn
        // Ghế SELECTED (do người dùng tự chọn trên frontend) sẽ không bị làm mờ nhưng có thể bỏ chọn
        if (seat.getStatus() == SeatStatus.SOLD_OUT) {
            seatButton.setAlpha(0.5f); // Làm mờ 50%
            seatButton.setEnabled(false); // Không cho phép click
        } else if (seat.getStatus() == SeatStatus.AVAILABLE || seat.getStatus() == SeatStatus.SELECTED) {
            seatButton.setOnClickListener(v -> onSeatClick(seat, (Button) v));
            seatButton.setAlpha(1.0f); // Đảm bảo không bị mờ
            seatButton.setEnabled(true); // Đảm bảo có thể click
        }
        return seatButton;
    }


    private void onSeatClick(Seat seat, Button seatButton) {
        // Chỉ cho phép click nếu ghế là AVAILABLE hoặc đã SELECTED
        if (seat.getStatus() == SeatStatus.AVAILABLE) {
            seat.setStatus(SeatStatus.SELECTED);
            selectedSeats.add(seat);
        } else if (seat.getStatus() == SeatStatus.SELECTED) {
            seat.setStatus(SeatStatus.AVAILABLE);
            selectedSeats.remove(seat);
        }
        updateSeatButtonAppearance(seatButton, seat);
        updateSummary();
    }

    private void updateSeatButtonAppearance(Button seatButton, Seat seat) {
        switch (seat.getStatus()) {
            case AVAILABLE:
                seatButton.setBackgroundResource(R.drawable.seat_available_bg);
                seatButton.setTextColor(ContextCompat.getColor(getContext(), R.color.text_primary));
                break;
            case SOLD_OUT:
                seatButton.setBackgroundResource(R.drawable.seat_sold_out_bg);
                seatButton.setTextColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
                break;
            case SELECTED:
                seatButton.setBackgroundResource(R.drawable.seat_selected_bg);
                seatButton.setTextColor(ContextCompat.getColor(getContext(), R.color.orange_primary));
                break;
        }
    }

    private void updateSummary() {
        int ticketCount = selectedSeats.size();
        double totalPrice = ticketCount * pricePerSeat;

        StringBuilder seatNumbers = new StringBuilder();
        for (int i = 0; i < selectedSeats.size(); i++) {
            seatNumbers.append(selectedSeats.get(i).getSeatNumber());
            if (i < selectedSeats.size() - 1) {
                seatNumbers.append(", ");
            }
        }

        if (ticketCount > 0) {
            selectedSeatsInfo.setText(String.format(Locale.US, "%d vé: %s", ticketCount, seatNumbers.toString()));
        } else {
            selectedSeatsInfo.setText("Vui lòng chọn ghế");
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(totalPrice);
        priceText.setText(formattedPrice);
    }
}