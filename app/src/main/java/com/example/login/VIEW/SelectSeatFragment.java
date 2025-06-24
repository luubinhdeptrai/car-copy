package com.example.login.VIEW;

import android.os.Bundle;
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

import com.example.login.MODELS.Seat;
import com.example.login.MODELS.SeatStatus;
import com.example.login.MODELS.Trip;
import com.example.login.R;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_seat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        createAndPopulateSeats(); // Phương thức này sẽ tạo seatList với các trạng thái
        updateSummary();

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
                Navigation.findNavController(v).navigate(R.id.action_selectSeat_to_confirmation, bundle); // Sử dụng v thay vì view
            }
        });
    }

    private void createAndPopulateSeats() {
        int capacity = selectedTrip.getVehicle().getCapacity();
        // --- Dữ liệu Giả định (MOCK DATA) ---
        // TODO: Trong thực tế, bạn sẽ gọi API GET /api/trips/{tripId}/seats để lấy danh sách này
        List<String> bookedSeats = Arrays.asList("04", "08", "11", "21");
        // ------------------------------------

        seatList.clear(); // Xóa dữ liệu cũ
        for (int i = 1; i <= capacity; i++) {
            String seatNumber = String.format(Locale.US, "%02d", i);
            if (bookedSeats.contains(seatNumber)) {
                seatList.add(new Seat(seatNumber, SeatStatus.SOLD_OUT));
            } else {
                seatList.add(new Seat(seatNumber, SeatStatus.AVAILABLE));
            }
        }

        // Vẽ ghế lên giao diện
        populateSeatGrid();
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

        if (seat.getStatus() == SeatStatus.SOLD_OUT || seat.getStatus() == SeatStatus.SELECTED) {
            // Làm mờ và không cho phép click
            seatButton.setAlpha(0.5f); // Làm mờ 50%
            seatButton.setEnabled(false); // Không cho phép click
        } else {
            // Cho phép click nếu ghế có sẵn
            seatButton.setOnClickListener(v -> onSeatClick(seat, (Button) v));
            seatButton.setAlpha(1.0f); // Đảm bảo không bị mờ
            seatButton.setEnabled(true); // Đảm bảo có thể click
        }
        return seatButton;
    }


    private void onSeatClick(Seat seat, Button seatButton) {
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
        // Thêm logic làm mờ/enable tại đây nếu trạng thái ghế thay đổi sau khi tạo (ví dụ: do API cập nhật)
        // Tuy nhiên, với yêu cầu "khi load data lên", việc này đã được xử lý trong createSeatButton.
        // Chỉ cần đảm bảo updateSeatButtonAppearance không ghi đè setAlpha/setEnabled nếu không cần.
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