// File: com/example/login/ADAPTERS/TripAdapter.java
package com.example.login.ADAPTERS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.MODELS.Route;
import com.example.login.MODELS.Trip;
import com.example.login.MODELS.Vehicle;
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

    // Sửa lại trong file: TripAdapter.java

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        Trip currentTrip = tripList.get(position);
        Route routeInfo = currentTrip.getRoute();
        Vehicle vehicleInfo = currentTrip.getVehicle();

        // --- Lấy dữ liệu từ các object lồng nhau ---

        // Lấy thời gian đi và đến (chỉ lấy giờ:phút)
        String departureTimeStr = currentTrip.getDepartureTime().substring(11, 16);
        String arrivalTimeStr = currentTrip.getArrivalTime().substring(11, 16);

        holder.departureTime.setText(departureTimeStr);
        holder.arrivalTime.setText(arrivalTimeStr);

        // Lấy tên điểm đi và điểm đến từ trong 'route'
        holder.departureLocation.setText(routeInfo.getOriginStation().getName());
        holder.destinationLocation.setText(routeInfo.getDestinationStation().getName());

        // Format giá tiền
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(currentTrip.getPrice()).replace("₫", "đ");

        // Lấy thông tin loại xe từ trong 'vehicle'
        // Số ghế trống bây giờ không có trong JSON, nên ta có thể hiển thị sức chứa của xe
        String details = String.format(Locale.US, "%s • %s • %d seats",
                formattedPrice,
                vehicleInfo.getType(),
                vehicleInfo.getCapacity());
        holder.tripDetails.setText(details);

        // Chuyển đổi phút sang giờ để hiển thị
        int totalMinutes = routeInfo.getEstimatedDurationMin();
        double hours = totalMinutes / 60.0;

        String duration = String.format(Locale.US, "Distance: %dkm - %.1fh",
                routeInfo.getDistanceKm(),
                hours);
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
                // Chuyển sang màn hình chọn ghế
                // Bạn có thể truyền thông tin của chuyến đi được chọn (`currentTrip`) sang màn hình tiếp theo nếu cần
                // Ví dụ: Bundle bundle = new Bundle();
                // bundle.putSerializable("SELECTED_TRIP", tripList.get(getAdapterPosition()));
                // Navigation.findNavController(v).navigate(R.id.action_selectTrip_to_selectSeat, bundle);
                Navigation.findNavController(v).navigate(R.id.action_selectTrip_to_selectSeat);
            });
        }
    }
}