package com.example.login.VIEW;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.login.ADAPTERS.FilterOptionsAdapter;
import com.example.login.MODELS.FilterOption;
import com.example.login.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import java.util.ArrayList;
import java.util.List;

public class FilterBottomSheetDialogFragment extends BottomSheetDialogFragment {

    public interface FilterListener {
        void onFilterApplied(String filterType, List<FilterOption> selectedOptions);
    }

    private String filterType;
    private List<FilterOption> options;
    private FilterListener listener;
    private FilterOptionsAdapter adapter;

    public static FilterBottomSheetDialogFragment newInstance(String filterType, ArrayList<FilterOption> options) {
        FilterBottomSheetDialogFragment fragment = new FilterBottomSheetDialogFragment();
        Bundle args = new Bundle();
        args.putString("FILTER_TYPE", filterType);
        args.putSerializable("OPTIONS", options);
        fragment.setArguments(args);
        return fragment;
    }

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filterType = getArguments().getString("FILTER_TYPE");
            options = (List<FilterOption>) getArguments().getSerializable("OPTIONS");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_filter_options, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView dialogTitle = view.findViewById(R.id.dialog_title);
        ImageView closeButton = view.findViewById(R.id.close_button);
        RecyclerView optionsRecyclerView = view.findViewById(R.id.options_recycler_view);
        TextView resetButton = view.findViewById(R.id.reset_filter_button);
        Button applyButton = view.findViewById(R.id.apply_button);

        dialogTitle.setText(filterType);

        adapter = new FilterOptionsAdapter(options);
        optionsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        optionsRecyclerView.setAdapter(adapter);

        closeButton.setOnClickListener(v -> dismiss());
        resetButton.setOnClickListener(v -> adapter.resetSelections());
        applyButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFilterApplied(filterType, options);
            }
            dismiss();
        });
    }
}