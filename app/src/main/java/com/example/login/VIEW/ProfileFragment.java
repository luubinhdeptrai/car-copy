package com.example.login.VIEW;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.API.TokenManager;
import com.example.login.LOGIN.LoginActivity;
import com.example.login.MODELS.DeleteAccountResponse;
import com.example.login.MODELS.ProfileResponse;
import com.example.login.MODELS.User;
import com.example.login.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private TextView profileNameTextView;
    private TextView profilePhoneTextView;
    private Toolbar toolbar; // SỬA: Thêm biến Toolbar

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        profileNameTextView = view.findViewById(R.id.profile_name_textview);
        profilePhoneTextView = view.findViewById(R.id.profile_phone_textview);
        toolbar = view.findViewById(R.id.toolbar_profile); // SỬA: Ánh xạ Toolbar

        LinearLayout profileInfoLayout = view.findViewById(R.id.profile_info_layout);
        TextView settingMenuItem = view.findViewById(R.id.setting_menu_item);
        TextView supportMenuItem = view.findViewById(R.id.support_menu_item);
        TextView deleteAccountMenuItem = view.findViewById(R.id.delete_account_menu_item);
        TextView logoutMenuItem = view.findViewById(R.id.logout_menu_item);

        loadUserProfile();

        // SỬA: Thêm sự kiện click cho nút back
        toolbar.setNavigationOnClickListener(v -> {
            // Giả lập hành động nhấn nút back của hệ thống
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
        });

        profileInfoLayout.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileHostActivity.class);
            intent.putExtra("START_DESTINATION", "user_details");
            startActivity(intent);
        });

        settingMenuItem.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileHostActivity.class);
            intent.putExtra("START_DESTINATION", "settings");
            startActivity(intent);
        });

        supportMenuItem.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Quick Support Clicked", Toast.LENGTH_SHORT).show();
        });

        deleteAccountMenuItem.setOnClickListener(v -> {
            showDeleteConfirmationDialog();
        });

        logoutMenuItem.setOnClickListener(v -> {
            TokenManager.deleteToken(requireContext());
            navigateToLoginScreen();
            Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserProfile() {
        ApiService apiService = ApiClient.getAuthAPI(requireContext());
        apiService.getUserProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    if (user != null) {
                        profileNameTextView.setText(user.getFullname());
                        profilePhoneTextView.setText(user.getPhoneNumber());
                    }
                } else {
                    profileNameTextView.setText("Không có dữ liệu");
                    profilePhoneTextView.setText("N/A");
                    Toast.makeText(getContext(), "Không thể tải thông tin cá nhân.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                profileNameTextView.setText("Lỗi mạng");
                profilePhoneTextView.setText("Vui lòng thử lại");
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Xoá tài khoản")
                .setMessage("Bạn có chắc chắn muốn xoá tài khoản vĩnh viễn không? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    performAccountDeletion();
                })
                .setNegativeButton("Huỷ", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void performAccountDeletion() {
        Toast.makeText(getContext(), "Đang xử lý...", Toast.LENGTH_SHORT).show();

        ApiService apiService = ApiClient.getAuthAPI(requireContext());
        Call<DeleteAccountResponse> call = apiService.deleteAccount();

        call.enqueue(new Callback<DeleteAccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<DeleteAccountResponse> call, @NonNull Response<DeleteAccountResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    TokenManager.deleteToken(requireContext());
                    navigateToLoginScreen();
                } else {
                    Toast.makeText(getContext(), "Xoá tài khoản thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeleteAccountResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToLoginScreen() {
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}