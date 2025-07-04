package com.example.login.ADAPTERS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter; // THAY ĐỔI: Kế thừa từ ListAdapter
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.MODELS.DateModel;
import com.example.login.R;
// import com.example.login.INTERFACES.OnDateSelectedListener; // Nếu interface này đã có ở cấp cao hơn, không cần import lại

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// THAY ĐỔI: Sử dụng interface OnDateClickListener của chúng ta để đồng bộ
import com.example.login.ADAPTERS.DateAdapter.OnDateClickListener; // Import inner interface


// THAY ĐỔI: Kế thừa từ ListAdapter và truyền vào DiffCallback
public class DateAdapter extends ListAdapter<DateModel, DateAdapter.DateViewHolder> {

    // private List<DateModel> dateList; // KHÔNG CẦN NỮA, ListAdapter quản lý list
    private int selectedPosition = RecyclerView.NO_POSITION; // Khởi tạo an toàn
    private Context context;
    private OnDateClickListener listener; // THAY ĐỔI: Dùng OnDateClickListener

    // Định nghĩa Interface cho click listener
    public interface OnDateClickListener {
        void onDateSelected(Date selectedDate);
    }

    public void setOnDateClickListener(OnDateClickListener listener) {
        this.listener = listener;
    }


    // THAY ĐỔI: Constructor cho ListAdapter
    public DateAdapter(Context context, OnDateClickListener listener) {
        super(DIFF_CALLBACK); // Gọi constructor của ListAdapter với DiffCallback
        this.context = context;
        this.listener = listener;
        // dateList không còn là biến thành viên trực tiếp nữa
    }

    @NonNull
    @Override
    public DateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date, parent, false);
        return new DateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DateViewHolder holder, int position) {
        DateModel dateModel = getItem(position); // Lấy item từ ListAdapter
        holder.bind(dateModel, position == selectedPosition); // Truyền trạng thái selected

        // Logic click listener
        holder.itemView.setOnClickListener(v -> {
            if (selectedPosition != holder.getAdapterPosition()) { // Kiểm tra nếu click vào item khác
                int previousSelectedPosition = selectedPosition;
                selectedPosition = holder.getAdapterPosition(); // Cập nhật vị trí mới

                // Cập nhật giao diện của item cũ và item mới thông qua notifyItemChanged
                // Không cần get DateModel lại, chỉ cần update lại trạng thái selected của nó
                DateModel prevSelectedModel = getItem(previousSelectedPosition);
                if (prevSelectedModel != null) {
                    prevSelectedModel.setSelected(false);
                }
                dateModel.setSelected(true); // Đánh dấu item hiện tại là selected

                // Sử dụng submitList để cập nhật ListAdapter một cách hiệu quả
                List<DateModel> currentList = new ArrayList<>(getCurrentList());
                submitList(currentList); // Cập nhật toàn bộ list để DiffUtil xử lý selected state

                if (listener != null) {
                    listener.onDateSelected(dateModel.getDate());
                }
            }
        });
    }

    // KHÔNG CẦN getItemCount() NỮA, ListAdapter tự xử lý

    // THAY ĐỔI: Phương thức updateDates mới để đồng bộ với ListAdapter
    public void updateDates(List<DateModel> newDates, Date currentSelectedDate) {
        // Cập nhật trạng thái selected trong newDates trước khi submit
        int initialSelectedPos = 0;
        if (currentSelectedDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", Locale.US);
            String targetDateStr = dateFormat.format(currentSelectedDate);
            for (int i = 0; i < newDates.size(); i++) {
                if (dateFormat.format(newDates.get(i).getDate()).equals(targetDateStr)) {
                    initialSelectedPos = i;
                    break;
                }
            }
        }

        // Cập nhật selectedPosition và trạng thái selected cho item tương ứng
        if (selectedPosition != RecyclerView.NO_POSITION && selectedPosition < newDates.size()) {
            newDates.get(selectedPosition).setSelected(false); // Bỏ chọn item cũ (nếu có)
        }
        if (initialSelectedPos != RecyclerView.NO_POSITION && initialSelectedPos < newDates.size()) {
            newDates.get(initialSelectedPos).setSelected(true);
            selectedPosition = initialSelectedPos;
        } else if (!newDates.isEmpty()) { // Nếu không tìm thấy ngày khớp, chọn ngày đầu tiên
            newDates.get(0).setSelected(true);
            selectedPosition = 0;
        }

        submitList(newDates); // Gửi list mới hoàn toàn cho ListAdapter
    }


    static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeek, tvDate; // Đổi tên biến để khớp với item_date.xml

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayOfWeek = itemView.findViewById(R.id.tv_day_of_week); // ID đã sửa
            tvDate = itemView.findViewById(R.id.tv_date); // ID đã sửa
        }

        void bind(final DateModel dateModel, boolean isSelected) { // Thêm isSelected parameter
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEE", Locale.US);
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.US);
            tvDayOfWeek.setText(dayFormat.format(dateModel.getDate()));
            tvDate.setText(dateFormat.format(dateModel.getDate()));

            // Thiết lập trạng thái selected của itemView
            itemView.setSelected(isSelected); // Sử dụng phương thức setSelected của View
            // Màu sắc sẽ được điều khiển bởi date_item_selector và date_text_color_selector
        }
    }

    // THAY ĐỔI: DIffCallback cho DateModel
    private static final DiffUtil.ItemCallback<DateModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<DateModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull DateModel oldItem, @NonNull DateModel newItem) {
            // So sánh dựa trên ngày (unique identifier)
            return oldItem.getDate().getTime() == newItem.getDate().getTime();
        }

        @Override
        public boolean areContentsTheSame(@NonNull DateModel oldItem, @NonNull DateModel newItem) {
            // So sánh nội dung, bao gồm cả trạng thái selected
            return oldItem.getDate().equals(newItem.getDate()) && oldItem.isSelected() == newItem.isSelected();
        }
    };
}