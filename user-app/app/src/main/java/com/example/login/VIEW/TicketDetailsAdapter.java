package com.example.login.VIEW;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.MODELS.BookingHistoryItem;
import com.example.login.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class TicketDetailsAdapter extends RecyclerView.Adapter<TicketDetailsAdapter.TicketViewHolder> {

    private List<BookingHistoryItem.TicketInfo> tickets;

    public TicketDetailsAdapter(List<BookingHistoryItem.TicketInfo> tickets) {
        this.tickets = tickets;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ticket_detail, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        BookingHistoryItem.TicketInfo ticket = tickets.get(position);
        holder.bind(ticket);
    }

    @Override
    public int getItemCount() {
        return tickets.size();
    }

    static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView seatNumberText, priceText, accessIdText;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            seatNumberText = itemView.findViewById(R.id.seat_number_text);
            priceText = itemView.findViewById(R.id.price_text);
            accessIdText = itemView.findViewById(R.id.access_id_text);
        }

        public void bind(BookingHistoryItem.TicketInfo ticket) {
            seatNumberText.setText("Ghế " + ticket.getSeatNumber());
            
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
            String formattedPrice = currencyFormat.format(ticket.getPrice());
            priceText.setText(formattedPrice);
            
            if (ticket.getAccessId() != null && !ticket.getAccessId().isEmpty()) {
                accessIdText.setText("Mã: " + ticket.getAccessId());
                accessIdText.setVisibility(View.VISIBLE);
            } else {
                accessIdText.setVisibility(View.GONE);
            }
        }
    }
}