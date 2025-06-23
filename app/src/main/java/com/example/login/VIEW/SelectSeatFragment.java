package com.example.login.VIEW;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.login.R;
import com.example.login.MODELS.Seat;
import com.example.login.MODELS.SeatStatus;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SelectSeatFragment extends Fragment {

    private GridLayout seatGridLeft, seatGridRight;
    private List<Seat> seatList = new ArrayList<>();
    private List<Seat> selectedSeats = new ArrayList<>();
    private TextView selectedSeatsInfo, priceText;
    private static final double PRICE_PER_SEAT = 140000.0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_seat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các view
        seatGridLeft = view.findViewById(R.id.seat_grid_left);
        seatGridRight = view.findViewById(R.id.seat_grid_right);
        selectedSeatsInfo = view.findViewById(R.id.selected_seats_info);
        priceText = view.findViewById(R.id.price_text);
        Button continueButton = view.findViewById(R.id.continue_button);
        Toolbar toolbar = view.findViewById(R.id.toolbar_select_seat);

        // Tạo dữ liệu và hiển thị ghế
        createSeatData();
        populateSeatGrid();
        updateSummary();

        continueButton.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_selectSeat_to_confirmation));
        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(view).popBackStack());
    }

    private void createSeatData() {
        // Sơ đồ ghế 2x2 với 24 ghế
        for (int i = 1; i <= 24; i++) {
            if (i == 4 || i == 8) { // Ghế đã bán
                seatList.add(new Seat(String.format(Locale.US, "%02d", i), SeatStatus.SOLD_OUT));
            } else {
                seatList.add(new Seat(String.format(Locale.US, "%02d", i), SeatStatus.AVAILABLE));
            }
        }
    }

    private void populateSeatGrid() {
        seatGridLeft.removeAllViews();
        seatGridRight.removeAllViews();

        for (Seat seat : seatList) {
            Button seatButton = createSeatButton(seat);
            int seatNum = Integer.parseInt(seat.getSeatNumber());

            // Xác định vị trí của ghế trong hàng 4 ghế (0, 1, 2, 3)
            int positionInRow = (seatNum - 1) % 4;

            // SỬA LỖI: Sắp xếp ghế từ trái qua phải
            // Ghế 1, 2 (vị trí 0, 1 trong hàng) -> Dãy trái
            // Ghế 3, 4 (vị trí 2, 3 trong hàng) -> Dãy phải
            if (positionInRow == 0 || positionInRow == 1) {
                seatGridLeft.addView(seatButton);
            } else {
                seatGridRight.addView(seatButton);
            }
        }
    }

    // Phương thức helper để tạo một nút ghế
    private Button createSeatButton(Seat seat) {
        Button seatButton = new Button(getContext());
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = (int) getResources().getDimension(R.dimen.seat_button_size);
        params.height = (int) getResources().getDimension(R.dimen.seat_button_size);
        int margin = (int) getResources().getDimension(R.dimen.seat_button_margin);
        params.setMargins(margin, margin, margin, margin);
        seatButton.setLayoutParams(params);

        seatButton.setText(seat.getSeatNumber());
        updateSeatButtonAppearance(seatButton, seat);

        if (seat.getStatus() != SeatStatus.SOLD_OUT) {
            seatButton.setOnClickListener(v -> onSeatClick(seat, (Button) v));
        } else {
            seatButton.setEnabled(false);
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
    }

    private void updateSummary() {
        int ticketCount = selectedSeats.size();
        double totalPrice = ticketCount * PRICE_PER_SEAT;
        StringBuilder seatNumbers = new StringBuilder();
        for (int i = 0; i < selectedSeats.size(); i++) {
            seatNumbers.append(selectedSeats.get(i).getSeatNumber());
            if (i < selectedSeats.size() - 1) {
                seatNumbers.append(", ");
            }
        }
        selectedSeatsInfo.setText(String.format(Locale.US, "%d ticket(s)\n%s", ticketCount, seatNumbers.toString()));
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(totalPrice).replace("₫", "đ");
        priceText.setText(String.format("Price: %s", formattedPrice));
    }
}