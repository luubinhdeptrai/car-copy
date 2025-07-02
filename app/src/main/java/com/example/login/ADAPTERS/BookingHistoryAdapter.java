package com.example.login.ADAPTERS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.MODELS.BookingHistoryItem;
import com.example.login.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class BookingHistoryAdapter extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {

    private Context context;
    private List<BookingHistoryItem> bookings;
    private OnBookingClickListener listener;

    public interface OnBookingClickListener {
        void onBookingClick(BookingHistoryItem booking);
    }

    public BookingHistoryAdapter(Context context, List<BookingHistoryItem> bookings, OnBookingClickListener listener) {
        this.context = context;
        this.bookings = bookings;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_booking_history, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        BookingHistoryItem booking = bookings.get(position);
        holder.bind(booking);
    }

    @Override
    public int getItemCount() {
        return bookings.size();
    }

    public void updateBookings(List<BookingHistoryItem> newBookings) {
        this.bookings = newBookings;
        notifyDataSetChanged();
    }

    class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView providerNameText, statusText, routeText, departureTimeText, seatNumbersText;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            providerNameText = itemView.findViewById(R.id.provider_name_text);
            statusText = itemView.findViewById(R.id.status_text);
            routeText = itemView.findViewById(R.id.route_text);
            departureTimeText = itemView.findViewById(R.id.departure_time_text);
            seatNumbersText = itemView.findViewById(R.id.seat_numbers_text);
        }

        public void bind(BookingHistoryItem booking) {
            // Set provider name
            if (booking.getProviderInfo() != null) {
                providerNameText.setText(booking.getProviderInfo().getName());
            }

            // Set status (combination of payment and approval status)
            String statusText = getStatusText(booking.getPaymentStatus(), booking.getApprovalStatus());
            this.statusText.setText(statusText);

            // Set route
            if (booking.getTripInfo() != null) {
                String route = booking.getTripInfo().getOrigin() + " → " + booking.getTripInfo().getDestination();
                routeText.setText(route);

                // Set departure time
                String formattedTime = formatDepartureTime(booking.getTripInfo().getDepartureTime());
                departureTimeText.setText(formattedTime);
            }

            // Set seat numbers
            if (booking.getTickets() != null && !booking.getTickets().isEmpty()) {
                StringBuilder seatNumbers = new StringBuilder();
                for (int i = 0; i < booking.getTickets().size(); i++) {
                    if (i > 0) seatNumbers.append(", ");
                    seatNumbers.append(booking.getTickets().get(i).getSeatNumber());
                }
                seatNumbersText.setText("Ghế: " + seatNumbers.toString());
            }

            // Set click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBookingClick(booking);
                }
            });
        }

        private String getStatusText(String paymentStatus, String approvalStatus) {
            // Combine both statuses for display
            String payment = "";
            String approval = "";
            
            switch (paymentStatus) {
                case "completed":
                    payment = "Đã thanh toán";
                    break;
                case "pending":
                    payment = "Chờ thanh toán";
                    break;
                case "expired":
                    payment = "Hết hạn chuyển khoản";
                    break;
                default:
                    payment = paymentStatus;
            }

            switch (approvalStatus) {
                case "confirmed_by_provider":
                    approval = "Đã duyệt";
                    break;
                case "pending_approval":
                    approval = "Chờ duyệt";
                    break;
                case "cancelled":
                    approval = "Từ chối";
                    break;
                default:
                    approval = approvalStatus;
            }

            return payment + " • " + approval;
        }

        private String formatDepartureTime(String utcDateString) {
            if (utcDateString == null) return "N/A";
            
            try {
                SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date utcDate = utcFormat.parse(utcDateString);

                SimpleDateFormat localFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.US);
                localFormat.setTimeZone(TimeZone.getDefault());
                return localFormat.format(utcDate);
            } catch (ParseException e) {
                e.printStackTrace();
                return "N/A";
            }
        }
    }
}