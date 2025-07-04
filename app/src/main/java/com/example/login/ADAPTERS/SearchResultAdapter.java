// src/main/java/com/example/login/ADAPTERS/SearchResultAdapter.java
package com.example.login.ADAPTERS;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.MODELS.TripSearchResult;
import com.example.login.R;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SearchResultAdapter extends ListAdapter<TripSearchResult, SearchResultAdapter.SearchResultViewHolder> {

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TripSearchResult trip);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public SearchResultAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trip_search_result, parent, false);
        return new SearchResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        TripSearchResult trip = getItem(position);
        holder.bind(trip);
        // Set click listener for the entire item view
        if (listener != null) {
            holder.itemView.setOnClickListener(v -> listener.onItemClick(trip));
        }
    }

    static class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private final TextView routeSummary;
        private final TextView timeInfo;
        private final TextView providerVehicle;
        private final TextView priceSegment;

        private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
        private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        public SearchResultViewHolder(@NonNull View itemView) {
            super(itemView);
            routeSummary = itemView.findViewById(R.id.text_view_route_summary);
            timeInfo = itemView.findViewById(R.id.text_view_time_info);
            providerVehicle = itemView.findViewById(R.id.text_view_provider_vehicle);
            priceSegment = itemView.findViewById(R.id.text_view_price_segment);
        }

        public void bind(TripSearchResult trip) {
            routeSummary.setText(trip.getItinerary().getBaseRoute().getOriginStation().getName() +
                    " (" + trip.getItinerary().getBaseRoute().getOriginStation().getCity() + ") → " +
                    trip.getItinerary().getBaseRoute().getDestinationStation().getName() +
                    " (" + trip.getItinerary().getBaseRoute().getDestinationStation().getCity() + ")");

            Date departureDate = trip.getDepartureTime();
            Date arrivalDate = trip.getArrivalTime();
            String departureTimeStr = (departureDate != null) ? dateTimeFormat.format(departureDate) : "N/A";
            String arrivalTimeStr = (arrivalDate != null) ? dateTimeFormat.format(arrivalDate) : "N/A";
            timeInfo.setText("Khởi hành: " + departureTimeStr +
                    " - Đến nơi: " + arrivalTimeStr);

            providerVehicle.setText(trip.getProvider().getName() + " - " + trip.getVehicle().getType());

            if (trip.getPriceForSelectedSegment() != null) {
                priceSegment.setText(currencyFormat.format(trip.getPriceForSelectedSegment()));
            } else {
                priceSegment.setText("N/A");
            }
        }
    }

    private static final DiffUtil.ItemCallback<TripSearchResult> DIFF_CALLBACK = new DiffUtil.ItemCallback<TripSearchResult>() {
        @Override
        public boolean areItemsTheSame(@NonNull TripSearchResult oldItem, @NonNull TripSearchResult newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull TripSearchResult oldItem, @NonNull TripSearchResult newItem) {
            return oldItem.equals(newItem);
        }
    };
}