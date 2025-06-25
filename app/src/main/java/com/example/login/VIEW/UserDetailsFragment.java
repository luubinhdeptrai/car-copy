package com.example.login.VIEW;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.login.R;

public class UserDetailsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button updateProfileButton = view.findViewById(R.id.btn_update_profile);

        // Bắt sự kiện click để điều hướng sang màn hình sửa
        updateProfileButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_userDetails_to_editProfile);
        });
    }
}