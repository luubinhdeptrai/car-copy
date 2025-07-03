package com.example.login.ADAPTERS;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.MODELS.FilterOption;
import com.example.login.R;
import java.util.List;

public class FilterOptionsAdapter extends RecyclerView.Adapter<FilterOptionsAdapter.ViewHolder> {
    private List<FilterOption> options;

    public FilterOptionsAdapter(List<FilterOption> options) {
        this.options = options;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_filter_option, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FilterOption option = options.get(position);
        holder.checkBox.setText(option.getName());
        holder.checkBox.setChecked(option.isSelected());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            option.setSelected(isChecked);
        });
    }

    @Override
    public int getItemCount() {
        return options.size();
    }

    public void resetSelections() {
        for (FilterOption option : options) {
            option.setSelected(false);
        }
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_option);
        }
    }
}
