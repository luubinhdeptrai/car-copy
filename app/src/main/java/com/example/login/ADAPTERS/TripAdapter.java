// File: com/example/login/ADAPTERS/TripAdapter.java
package com.example.login.ADAPTERS;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.MODELS.Trip;
import com.example.login.R;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> { // Sửa TripAdapter để extend RecyclerView.Adapter và sử dụng TripAdapter.TripViewHolder

    private Context context;
    private List<Trip> trips;

    public TripAdapter(Context context, List<Trip> trips) {
        this.context = context;
        this.trips = trips;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        holder.bind(trips.get(position));
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    // Đây là lớp inner class TripViewHolder
    // Đảm bảo nó là public static để có thể truy cập từ bên ngoài và tránh rò rỉ bộ nhớ
    public static class TripViewHolder extends RecyclerView.ViewHolder {
        // 1. Khai báo các biến View cho các ID mới trong XML
        TextView departureTimeText;
        TextView arrivalTimeText;
        TextView tripDetailsText;
        TextView departureLocationText;
        TextView tripDurationText;
        TextView destinationLocationText;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            // 2. Ánh xạ các view với ID chính xác từ layout item_trip.xml mới
            departureTimeText = itemView.findViewById(R.id.departure_time_text);
            arrivalTimeText = itemView.findViewById(R.id.arrival_time_text);
            tripDetailsText = itemView.findViewById(R.id.trip_details_text);
            departureLocationText = itemView.findViewById(R.id.departure_location_text);
            tripDurationText = itemView.findViewById(R.id.trip_duration_text);
            destinationLocationText = itemView.findViewById(R.id.destination_location_text);
        }

        /**
         * Phương thức này nhận một đối tượng Trip và điền dữ liệu vào các View
         */
        public void bind(final Trip trip) {
            // 3. Lấy dữ liệu từ đối tượng Trip và hiển thị lên các View tương ứng

            // Hiển thị giờ đi, giờ đến
            departureTimeText.setText(formatTime(trip.getDepartureTime()));
            arrivalTimeText.setText(formatTime(trip.getArrivalTime()));

            // Hiển thị địa điểm và thời gian di chuyển
            departureLocationText.setText(trip.getRoute().getOriginStation().getName());
            destinationLocationText.setText(trip.getRoute().getDestinationStation().getName());
            String durationString = String.format(Locale.US, "Khoảng cách: %dkm - %s",
                    trip.getRoute().getDistanceKm(),
                    calculateDuration(trip.getDepartureTime(), trip.getArrivalTime())
            );
            tripDurationText.setText(durationString);

            // --- PHẦN QUAN TRỌNG: TẠO CHUỖI TỔNG HỢP ---
            // Ghép thông tin Giá, Loại xe, Ghế trống vào một chuỗi duy nhất
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String formattedPrice = currencyFormat.format(trip.getPrice());
            String vehicleType = trip.getVehicle().getType();
            // TODO: API nên trả về số ghế trống thực tế
            int availableSeats = trip.getVehicle().getCapacity();

            String details = String.format(Locale.forLanguageTag("vi-VN"), "%s • %s • còn %d ghế",
                    formattedPrice,
                    vehicleType,
                    availableSeats
            );
            tripDetailsText.setText(details);

            // Gắn sự kiện click cho toàn bộ item (giữ nguyên như cũ)
            itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("SELECTED_TRIP", trip);
                Navigation.findNavController(v).navigate(R.id.action_selectTrip_to_selectSeat, bundle);
            });
        }

        // Các hàm tiện ích formatTime và calculateDuration giữ nguyên như phiên bản trước
        private String formatTime(String utcDateString) {
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
            try {
                SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date startDate = utcFormat.parse(startUtc);
                Date endDate = utcFormat.parse(endUtc);

                long durationMillis = endDate.getTime() - startDate.getTime();
                long hours = TimeUnit.MILLISECONDS.toHours(durationMillis);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(durationMillis) % 60;

                return String.format(Locale.US, "~%d giờ %d phút", hours, minutes);
            } catch (ParseException e) {
                e.printStackTrace();
                return "N/A";
            }
        }
    }
}