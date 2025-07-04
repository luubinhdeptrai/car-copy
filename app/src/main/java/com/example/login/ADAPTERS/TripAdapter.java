package com.example.login.ADAPTERS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.INTERFACES.OnTripSelectedListener; // <-- THÊM import
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
    private OnTripSelectedListener listener; // <-- THÊM biến Listener

    // SỬA: Thêm listener vào constructor
    public TripAdapter(Context context, List<Trip> trips, OnTripSelectedListener listener) {
        this.context = context;
        this.trips = trips;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip_search_result, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        // SỬA: Truyền listener vào hàm bind
        holder.bind(trips.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return trips.size();
    }

    public void updateTrips(List<Trip> newTrips) {
        this.trips.clear();
        this.trips.addAll(newTrips);
        notifyDataSetChanged();
    }

    public static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView departureTimeText, arrivalTimeText, tripDetailsText, departureLocationText, tripDurationText, destinationLocationText, providerNameText;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            departureTimeText = itemView.findViewById(R.id.departure_time_text);
            arrivalTimeText = itemView.findViewById(R.id.arrival_time_text);
            tripDetailsText = itemView.findViewById(R.id.trip_details_text);
            departureLocationText = itemView.findViewById(R.id.departure_location_text);
            tripDurationText = itemView.findViewById(R.id.trip_duration_text);
            destinationLocationText = itemView.findViewById(R.id.destination_location_text);
            providerNameText = itemView.findViewById(R.id.provider_name_text);
        }

        // SỬA: Cập nhật hàm bind để nhận listener
        public void bind(final Trip trip, final OnTripSelectedListener listener) {
            departureTimeText.setText(formatTime(trip.getDepartureTime()));
            arrivalTimeText.setText(formatTime(trip.getArrivalTime()));

            if (trip.getRoute() != null && trip.getRoute().getOriginStation() != null && trip.getRoute().getDestinationStation() != null) {
                departureLocationText.setText(trip.getRoute().getOriginStation().getName());
                destinationLocationText.setText(trip.getRoute().getDestinationStation().getName());
                String durationString = String.format(Locale.US, "Khoảng cách: %dkm - %s", trip.getRoute().getDistanceKm(), calculateDuration(trip.getDepartureTime(), trip.getArrivalTime()));
                tripDurationText.setText(durationString);
            }

            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String formattedPrice = currencyFormat.format(trip.getPrice());
            String vehicleType = trip.getVehicle().getType();
            String seatsInfo;

            if (trip.getAvailableSeats() == -1) {
                seatsInfo = "Checking...";
            } else {
                seatsInfo = String.format(Locale.US, "%d ghế trống", trip.getAvailableSeats());
            }

            String details = String.format(Locale.US, "%s     •     %s     •     %s", formattedPrice, vehicleType, seatsInfo);
            tripDetailsText.setText(details);


            // SỬA: Sự kiện click giờ sẽ gọi đến listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onTripSelected(trip);
                }
            });

            providerNameText.setText(trip.getProvider() != null ? trip.getProvider().getName() : "Unknown Provider");
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
                return String.format(Locale.US, "~%d hours %d minutes", hours, minutes);
            } catch (ParseException e) {
                e.printStackTrace();
                return "N/A";
            }
        }
    }
}
    