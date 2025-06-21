package com.example.login.VIEW;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.example.login.R; // Thay bằng package của bạn

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // "Thổi phồng" layout XML lên thành một đối tượng View
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Tìm kiếm layout chứa thông tin profile
        LinearLayout profileInfoLayout = view.findViewById(R.id.profile_info_layout);

        // Bắt sự kiện click để chuyển sang màn hình UserDetails
        profileInfoLayout.setOnClickListener(v -> {
            // TODO: Tạo và chuyển sang UserDetailsFragment
            // Ví dụ:
            // Fragment userDetailsFragment = new UserDetailsFragment();
            // FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            // transaction.replace(R.id.fragment_container, userDetailsFragment);
            // transaction.addToBackStack(null);
            // transaction.commit();
            Toast.makeText(getContext(), "Navigate to User Details", Toast.LENGTH_SHORT).show();
        });

        // Bạn cũng có thể bắt sự kiện cho các TextView khác tương tự
    }
}
