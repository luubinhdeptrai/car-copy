package com.example.login.VIEW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login.MODELS.Seat;
import com.example.login.MODELS.Trip;
import com.example.login.R;

import java.io.Serializable;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ConfirmationFragment extends Fragment {

    private Trip tripToShow;
    private ArrayList<Seat> selectedSeats;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Nhận dữ liệu từ Bundle một cách an toàn
        if (getArguments() != null) {
            Serializable tripSerializable = getArguments().getSerializable("CONFIRMATION_TRIP");
            if (tripSerializable instanceof Trip) {
                tripToShow = (Trip) tripSerializable;
            }

            Serializable seatsSerializable = getArguments().getSerializable("CONFIRMATION_SEATS");
            if (seatsSerializable instanceof ArrayList) {
                selectedSeats = (ArrayList<Seat>) seatsSerializable;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_confirmation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (tripToShow == null || selectedSeats == null || selectedSeats.isEmpty()) {
            Toast.makeText(getContext(), "Error: Confirmation data is missing.", Toast.LENGTH_LONG).show();
            // TODO: Xử lý quay lại màn hình trước
            return;
        }

        // --- Ánh xạ Views ---
        TextView tvRouteTitle = view.findViewById(R.id.tv_route_title_confirmation);
        View tripInfoCard = view.findViewById(R.id.trip_info_card);
        TextView departureTime = tripInfoCard.findViewById(R.id.departure_time_text);
        TextView arrivalTime = tripInfoCard.findViewById(R.id.arrival_time_text);
        TextView tripDetails = tripInfoCard.findViewById(R.id.trip_details_text);
        TextView departureLocation = tripInfoCard.findViewById(R.id.departure_location_text);
        TextView destinationLocation = tripInfoCard.findViewById(R.id.destination_location_text);
        TextView tripDuration = tripInfoCard.findViewById(R.id.trip_duration_text);
        TextView seatNumberValue = view.findViewById(R.id.seat_number_value);

        // --- Gán dữ liệu ---
        // 1. Cập nhật tiêu đề lộ trình
        String routeTitle = tripToShow.getRoute().getOriginStation().getName() + " → " + tripToShow.getRoute().getDestinationStation().getName();
        tvRouteTitle.setText(routeTitle);

        // 2. Gán dữ liệu cho thẻ thông tin chuyến đi (item_trip)
        // SỬA: Sử dụng các hàm helper để định dạng thời gian
        departureTime.setText(formatTime(tripToShow.getDepartureTime()));
        arrivalTime.setText(formatTime(tripToShow.getArrivalTime()));
        departureLocation.setText(tripToShow.getRoute().getOriginStation().getName());
        destinationLocation.setText(tripToShow.getRoute().getDestinationStation().getName());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(tripToShow.getPrice());
        String detailsText = String.format(Locale.US, "%s • %s", formattedPrice, tripToShow.getVehicle().getType());
        tripDetails.setText(detailsText);

        // SỬA: Sử dụng các hàm helper để tính toán và định dạng thời gian di chuyển
        String durationText = String.format(Locale.US, "Distance: %dkm - %s",
                tripToShow.getRoute().getDistanceKm(),
                calculateDuration(tripToShow.getDepartureTime(), tripToShow.getArrivalTime()));
        tripDuration.setText(durationText);

        // 3. Hiển thị số ghế đã chọn
        ArrayList<String> seatNumbers = new ArrayList<>();
        for(Seat seat : selectedSeats) {
            seatNumbers.add(seat.getSeatNumber());
        }
        seatNumberValue.setText(String.join(", ", seatNumbers));
    }

    // --- THÊM: Các hàm tiện ích để định dạng thời gian ---
    private String formatTime(String utcDateString) {
        if (utcDateString == null) return "N/A";
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = utcFormat.parse(utcDateString);

            SimpleDateFormat localFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            localFormat.setTimeZone(TimeZone.getDefault());
            return localFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "N/A";
        }
    }

    private String calculateDuration(String startUtc, String endUtc) {
        if (startUtc == null || endUtc == null) return "N/A";
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date startDate = utcFormat.parse(startUtc);
            Date endDate = utcFormat.parse(endUtc);

            long durationMillis = endDate.getTime() - startDate.getTime();
            long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60;

            return String.format(Locale.US, "~%dh %dm", hours, minutes);
        } catch (ParseException e) {
            e.printStackTrace();
            return "N/A";
        }
    }
}
    