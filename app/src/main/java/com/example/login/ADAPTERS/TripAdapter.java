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
            departureLocationText.setText(trip.getRoute().getOriginStation().getName());
            destinationLocationText.setText(trip.getRoute().getDestinationStation().getName());
            // SỬA: Dịch chuỗi sang tiếng Anh
            String durationString = String.format(Locale.US, "Distance: %dkm - %s",
                    trip.getRoute().getDistanceKm(),
                    calculateDuration(trip.getDepartureTime(), trip.getArrivalTime())
            );
            tripDurationText.setText(durationString);

            // Format and combine Price, Vehicle Type, and Available Seats
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            String formattedPrice = currencyFormat.format(trip.getPrice());
            String vehicleType = trip.getVehicle().getType();
            int availableSeats = trip.getVehicle().getCapacity();

            // SỬA: Dịch chuỗi sang tiếng Anh
            String details = String.format(Locale.US, "%s • %s • %d seats left",
                    formattedPrice,
                    vehicleType,
                    availableSeats
            );
            tripDetailsText.setText(details);

            // Set click listener for the entire item
            itemView.setOnClickListener(v -> {
                Bundle bundle = new Bundle();
                bundle.putSerializable("SELECTED_TRIP", trip);
                Navigation.findNavController(v).navigate(R.id.action_selectTrip_to_selectSeat, bundle);
            });
        }

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

                // SỬA: Dịch chuỗi sang tiếng Anh
                return String.format(Locale.US, "~%d hours %d minutes", hours, minutes);
            } catch (ParseException e) {
                e.printStackTrace();
                return "N/A";
            }
        }
    }
}
    