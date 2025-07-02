package com.example.login.VIEW;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.example.login.R; // Thay bằng package của bạn

public class HistoryFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // "Thổi phồng" layout XML lên thành một đối tượng View
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tìm kiếm các view trong layout
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        // Tạo Adapter cho ViewPager2
        HistoryPagerAdapter adapter = new HistoryPagerAdapter(this);
        viewPager.setAdapter(adapter);

        // Kết nối TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("HISTORY");
            } else {
                tab.setText("DEPARTING SOON");
            }
        }).attach();
    }

    // Lớp Adapter để quản lý các Fragment con trong tab
    private class HistoryPagerAdapter extends FragmentStateAdapter {
        public HistoryPagerAdapter(@NonNull Fragment fragment) {
            super(fragment);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if (position == 0) {
                return new HistoryTabFragment();
            } else {
                return new DepartingSoonTabFragment();
            }
        }

        @Override
        public int getItemCount() {
            // Trả về số lượng tab
            return 2;
        }
    }
}
