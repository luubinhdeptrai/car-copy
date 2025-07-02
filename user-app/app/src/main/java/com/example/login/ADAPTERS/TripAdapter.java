package com.example.login.ADAPTERS;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

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

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

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

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView departureTimeText;
        TextView arrivalTimeText;
        TextView tripDetailsText;
        TextView departureLocationText;
        TextView tripDurationText;
        TextView destinationLocationText;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            departureTimeText = itemView.findViewById(R.id.departure_time_text);
            arrivalTimeText = itemView.findViewById(R.id.arrival_time_text);
            tripDetailsText = itemView.findViewById(R.id.trip_details_text);
            departureLocationText = itemView.findViewById(R.id.departure_location_text);
            tripDurationText = itemView.findViewById(R.id.trip_duration_text);
            destinationLocationText = itemView.findViewById(R.id.destination_location_text);
        }

        public void bind(final Trip trip) {
            // Display departure and arrival times
            departureTimeText.setText(formatTime(trip.getDepartureTime()));
            arrivalTimeText.setText(formatTime(trip.getArrivalTime()));

            // Display location and duration
            if (trip.getRoute() != null && trip.getRoute().getOriginStation() != null && trip.getRoute().getDestinationStation() != null) {
                departureLocationText.setText(trip.getRoute().getOriginStation().getName());
                destinationLocationText.setText(trip.getRoute().getDestinationStation().getName());
                String durationString = String.format(Locale.US, "Khoảng cách: %dkm - %s",
                        trip.getRoute().getDistanceKm(),
                        calculateDuration(trip.getDepartureTime(), trip.getArrivalTime())
                );
                tripDurationText.setText(durationString);
            }


            // THAY ĐỔI: Toàn bộ logic hiển thị chi tiết chuyến đi
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String formattedPrice = currencyFormat.format(trip.getPrice());
            String vehicleType = trip.getVehicle().getType();
            String seatsInfo;

            // Kiểm tra xem số ghế đã được load chưa (dựa vào giá trị khởi tạo -1)
            if (trip.getAvailableSeats() == -1) {
                seatsInfo = "Đang kiểm tra..."; // Hiển thị text tạm thời
            } else {
                seatsInfo = String.format(Locale.forLanguageTag("vi-VN"), "%d ghế trống", trip.getAvailableSeats());
            }

            String details = String.format(Locale.US, "%s • %s • %s",
                    formattedPrice,
                    vehicleType,
                    seatsInfo
            );
            tripDetailsText.setText(details);

            // Set click listener for the entire item
            itemView.setOnClickListener(v -> {
                // Kiểm tra xem số ghế đã load xong chưa trước khi chuyển màn hình
                if (trip.getAvailableSeats() == -1) {
                    Toast.makeText(itemView.getContext(), "Vui lòng đợi kiểm tra thông tin ghế...", Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("SELECTED_TRIP", trip);
                Navigation.findNavController(v).navigate(R.id.action_selectTrip_to_selectSeat, bundle);
            });
        }

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

                return String.format(Locale.US, "~%d giờ %d phút", hours, minutes);
            } catch (ParseException e) {
                e.printStackTrace();
                return "N/A";
            }
        }
    }
}