package com.example.login.ADAPTERS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.MODELS.DateModel;
import com.example.login.R;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.DateViewHolder> {

    private List<DateModel> dateList;
    private int selectedPosition;
    private Context context;

    public DateAdapter(Context context, List<DateModel> dateList, int initialSelectedPosition) {
        this.context = context;
        this.dateList = dateList;
        this.selectedPosition = initialSelectedPosition;
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
            // Định dạng ngày và thứ
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.US);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.US);
            dayOfWeekText.setText(dayFormat.format(dateModel.getDate()));
            dateText.setText(dateFormat.format(dateModel.getDate()));

            // Chuẩn bị các biến ngày để so sánh
            Calendar todayCal = getNormalizedCalendar(Calendar.getInstance());
            Calendar itemCal = getNormalizedCalendar(Calendar.getInstance());
            itemCal.setTime(dateModel.getDate());

            boolean isToday = itemCal.equals(todayCal);
            boolean isSelected = (selectedPosition == position);

            // --- SỬA LỖI: Logic xử lý giao diện mới ---
            if (isSelected) {
                // Ưu tiên 1: Nếu item đang được chọn -> nền cam, chữ trắng.
                itemView.setBackgroundResource(R.drawable.date_item_background);
                itemView.setSelected(true); // Kích hoạt state_selected trong selector
                dayOfWeekText.setTextColor(ContextCompat.getColor(context, R.color.white));
                dateText.setTextColor(ContextCompat.getColor(context, R.color.white));

            } else if (isToday) {
                // Ưu tiên 2: Nếu không được chọn, kiểm tra xem có phải ngày hôm nay không.
                // Nếu là ngày hôm nay -> viền cam, chữ cam.
                itemView.setBackgroundResource(R.drawable.date_item_today_unselected_bg);
                itemView.setSelected(false);
                dayOfWeekText.setTextColor(ContextCompat.getColor(context, R.color.orange_primary));
                dateText.setTextColor(ContextCompat.getColor(context, R.color.orange_primary));

            } else {
                // Ưu tiên 3: Các trường hợp còn lại (ngày tương lai, không được chọn).
                // -> Nền trong suốt, chữ xám.
                itemView.setBackgroundResource(R.drawable.date_item_background);
                itemView.setSelected(false); // Kích hoạt state mặc định (trong suốt)
                dayOfWeekText.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
                dateText.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            }

            // Bắt sự kiện click
            itemView.setOnClickListener(v -> {
                if (selectedPosition != getAdapterPosition()) {
                    int previousSelectedPosition = selectedPosition;
                    selectedPosition = getAdapterPosition();
                    notifyItemChanged(previousSelectedPosition);
                    notifyItemChanged(selectedPosition);
                }
            });
        }

        private Calendar getNormalizedCalendar(Calendar cal) {
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            return cal;
        }
    }
}