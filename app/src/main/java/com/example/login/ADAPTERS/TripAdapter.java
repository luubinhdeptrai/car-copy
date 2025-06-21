package com.example.login.ADAPTERS; // Thay đổi package cho phù hợp với dự án của bạn

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.MODELS.Trip; // Import lớp Trip của bạn
import com.example.login.R; // Import R của dự án của bạn

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TripAdapter extends RecyclerView.Adapter<TripAdapter.TripViewHolder> {

    private Context context;
    private List<Trip> tripList;

    /**
     * Constructor cho TripAdapter.
     * @param context Context của Activity hoặc Fragment.
     * @param tripList Danh sách các đối tượng Trip để hiển thị.
     */
    public TripAdapter(Context context, List<Trip> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    @NonNull
    @Override
    public TripViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout cho một item trong danh sách
        // R.layout.item_trip là tên file XML layout cho item bạn đã tạo ở bước 1
        View view = LayoutInflater.from(context).inflate(R.layout.item_trip, parent, false);
        return new TripViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TripViewHolder holder, int position) {
        // Lấy đối tượng Trip tại vị trí hiện tại
        Trip currentTrip = tripList.get(position);

        // Binding dữ liệu từ đối tượng Trip vào các View trong ViewHolder
        holder.tvDepartureTime.setText(currentTrip.getDepartureTime());
        holder.tvArrivalTime.setText(currentTrip.getArrivalTime());
        holder.tvDeparture.setText(currentTrip.getDeparture());
        holder.tvDestination.setText(currentTrip.getDestination());

        // Định dạng giá tiền
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        String formattedPrice = currencyFormat.format(currentTrip.getPrice());

        // Tạo chuỗi thông tin chuyến đi
        String tripInfo = String.format(Locale.getDefault(), "%s • %s • %d seats left",
                formattedPrice,
                currentTrip.getTypeSeat(),
                currentTrip.getEmptySeat());
        holder.tvTripInfo.setText(tripInfo);

        // Tạo chuỗi thông tin khoảng cách và thời gian
        String distanceDurationInfo = String.format(Locale.getDefault(), "Distance: %.0fkm - %.0fh",
                currentTrip.getDistance(),
                currentTrip.getDuration());
        holder.tvDistanceDuration.setText(distanceDurationInfo);
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng item trong danh sách
        return tripList != null ? tripList.size() : 0;
    }

    /**
     * Lớp ViewHolder chứa các View của một item.
     * Việc này giúp tối ưu hiệu suất bằng cách tránh gọi findViewById() nhiều lần.
     */
    public static class TripViewHolder extends RecyclerView.ViewHolder {
        TextView tvDepartureTime, tvArrivalTime, tvTripInfo, tvDeparture, tvDistanceDuration, tvDestination;

        public TripViewHolder(@NonNull View itemView) {
            super(itemView);

            // Ánh xạ các View từ layout item_trip.xml
            tvDepartureTime = itemView.findViewById(R.id.tvDepartureTime);
            tvArrivalTime = itemView.findViewById(R.id.tvArrivalTime);
            tvTripInfo = itemView.findViewById(R.id.tvTripInfo);
            tvDeparture = itemView.findViewById(R.id.tvDeparture);
            tvDistanceDuration = itemView.findViewById(R.id.tvDistanceDuration);
            tvDestination = itemView.findViewById(R.id.tvDestination);
        }
    }
}
