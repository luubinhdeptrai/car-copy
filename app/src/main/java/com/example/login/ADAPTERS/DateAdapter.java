package com.example.login.ADAPTERS;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.MODELS.DateModel;
import com.example.login.R;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private List<DateModel> dateList;
    private int selectedPosition = 0; // Mặc định chọn item đầu tiên

    public DateAdapter(List<DateModel> dateList) {
        this.dateList = dateList;
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DateModel dateModel = dateList.get(position);
        holder.bind(dateModel, position);
    }

    @Override
    public int getItemCount() {
        return dateList.size();
    }

    class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dayOfWeekText, dateText;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dayOfWeekText = itemView.findViewById(R.id.day_of_week_text);
            dateText = itemView.findViewById(R.id.date_text);
        }

        void bind(final DateModel dateModel, final int position) {
            // Định dạng ngày tháng
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.US); // EEE -> Wed
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.US); // dd -> 18

            dayOfWeekText.setText(dayFormat.format(dateModel.getDate()));
            dateText.setText(dateFormat.format(dateModel.getDate()));

            // Cập nhật giao diện dựa trên trạng thái được chọn
            itemView.setSelected(selectedPosition == position);

            // Bắt sự kiện click
            itemView.setOnClickListener(v -> {
                if (selectedPosition != getAdapterPosition()) {
                    int previousSelectedPosition = selectedPosition;
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(previousSelectedPosition);
                    notifyItemChanged(selectedPosition);

                    // TODO: Gọi interface để thông báo cho Fragment rằng ngày đã thay đổi
                }
            });
        }
    }
}