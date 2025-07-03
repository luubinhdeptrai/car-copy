// File: com/example/login/ADAPTERS/IssueAdapter.java
package com.example.login.ADAPTERS;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.MODELS.Issue;
import com.example.login.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class IssueAdapter extends RecyclerView.Adapter<IssueAdapter.IssueViewHolder> {

    private final List<Issue> issueList;
    private final Context context;
    private OnItemClickListener listener;

    // Interface để xử lý sự kiện click vào item
    public interface OnItemClickListener {
        void onItemClick(Issue issue);
    }

    public IssueAdapter(Context context, List<Issue> issueList, OnItemClickListener listener) {
        this.context = context;
        this.issueList = issueList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IssueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_issue, parent, false);
        return new IssueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssueViewHolder holder, int position) {
        Issue issue = issueList.get(position);
        holder.tvTitle.setText(issue.getTitle());
        holder.tvDescription.setText(issue.getDescription());
        holder.tvType.setText(issue.getType());
        holder.tvStatus.setText(issue.getStatus());
        holder.tvDate.setText(formatDate(issue.getCreatedAt())); // Format ngày cho đẹp

        // Cập nhật màu nền cho trạng thái (bạn cần tạo các drawable tương ứng)
        switch (issue.getStatus().toLowerCase(Locale.ROOT)) {
            case "pending":
                holder.tvStatus.setBackgroundResource(R.drawable.rounded_status_background_pending);
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.white)); // Hoặc màu phù hợp
                break;
            case "in_progress":
                holder.tvStatus.setBackgroundResource(R.drawable.rounded_status_background_in_progress); // Tạo file này
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                break;
            case "resolved":
                holder.tvStatus.setBackgroundResource(R.drawable.rounded_status_background_resolved); // Tạo file này
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                break;
            case "closed":
                holder.tvStatus.setBackgroundResource(R.drawable.rounded_status_background_closed); // Tạo file này
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, android.R.color.white));
                break;
            default:
                holder.tvStatus.setBackgroundResource(R.drawable.rounded_tag_background); // Mặc định là màu xám
                holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
                break;
        }

        // Đặt lắng nghe sự kiện click cho toàn bộ item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(issue);
            }
        });
    }

    @Override
    public int getItemCount() {
        return issueList.size();
    }

    public static class IssueViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDescription, tvType, tvStatus, tvDate;

        public IssueViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_issue_title_item);
            tvDescription = itemView.findViewById(R.id.tv_issue_description_item);
            tvType = itemView.findViewById(R.id.tv_issue_type_item);
            tvStatus = itemView.findViewById(R.id.tv_issue_status_item);
            tvDate = itemView.findViewById(R.id.tv_issue_date_item);
        }
    }

    // Helper method to format date
    private String formatDate(String utcDateString) {
        if (utcDateString == null) return "N/A";
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            java.util.Date date = utcFormat.parse(utcDateString);
            SimpleDateFormat localFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return localFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return utcDateString.substring(0, 10); // Fallback to just date part if parsing fails
        }
    }
}