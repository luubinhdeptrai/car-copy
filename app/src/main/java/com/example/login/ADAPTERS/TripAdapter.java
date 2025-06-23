package com.example.login.ADAPTERS;

import android.content.Context;
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
import java.util.List;
import java.util.Locale;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private Context context;
    private List<Trip> tripList;

    public TripAdapter(Context context, List<Trip> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip currentTrip = tripList.get(position);

        // Gán dữ liệu vào các View
        holder.departureTime.setText(currentTrip.getDepartureTime());
        holder.arrivalTime.setText(currentTrip.getArrivalTime());
        holder.departureLocation.setText(currentTrip.getDeparture());
        holder.destinationLocation.setText(currentTrip.getDestination());

        // Format giá tiền cho đẹp
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(currentTrip.getPrice()).replace("₫", "đ");

        String details = String.format(Locale.US, "%s • %s • %d seats left",
                formattedPrice,
                currentTrip.getTypeSeat(),
                currentTrip.getEmptySeat());
        holder.tripDetails.setText(details);

        String duration = String.format(Locale.US, "Distance: %.0fkm - %.1fh",
                currentTrip.getDistance(),
                currentTrip.getDuration());
        holder.tripDuration.setText(duration);
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }


    class TripViewHolder extends RecyclerView.ViewHolder {
        // Khai báo các View trong item_trip.xml
        TextView departureTime, arrivalTime, tripDetails, departureLocation, destinationLocation, tripDuration;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ các View
            departureTime = itemView.findViewById(R.id.departure_time_text);
            arrivalTime = itemView.findViewById(R.id.arrival_time_text);
            tripDetails = itemView.findViewById(R.id.trip_details_text);
            departureLocation = itemView.findViewById(R.id.departure_location_text);
            destinationLocation = itemView.findViewById(R.id.destination_location_text);
            tripDuration = itemView.findViewById(R.id.trip_duration_text);

            // Bắt sự kiện click cho toàn bộ item
            itemView.setOnClickListener(v -> {
                Navigation.findNavController(v).navigate(R.id.action_selectTrip_to_selectSeat);
            });
        }
    }
}