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
import androidx.fragment.app.Fragment;

import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.API.TokenManager;
import com.example.login.LOGIN.LoginActivity; // Giả sử bạn có LoginActivity
import com.example.login.MODELS.DeleteAccountResponse;
import com.example.login.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LinearLayout profileInfoLayout = view.findViewById(R.id.profile_info_layout);
        TextView settingMenuItem = view.findViewById(R.id.setting_menu_item);
        TextView supportMenuItem = view.findViewById(R.id.support_menu_item);
        TextView deleteAccountMenuItem = view.findViewById(R.id.delete_account_menu_item);
        TextView logoutMenuItem = view.findViewById(R.id.logout_menu_item);

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

        // SỬA ĐỔI: Thêm logic cho nút xoá tài khoản
        deleteAccountMenuItem.setOnClickListener(v -> {
            // Hiển thị hộp thoại xác nhận trước khi xoá
            showDeleteConfirmationDialog();
        });

        logoutMenuItem.setOnClickListener(v -> {
            // Xử lý logic đăng xuất
            TokenManager.deleteToken(requireContext());
            navigateToLoginScreen();
            Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
        });
    }

    // THÊM MỚI: Phương thức hiển thị hộp thoại xác nhận
    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("Xoá tài khoản")
                .setMessage("Bạn có chắc chắn muốn xoá tài khoản vĩnh viễn không? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    // Nếu người dùng đồng ý, thực hiện gọi API
                    performAccountDeletion();
                })
                .setNegativeButton("Huỷ", null) // Không làm gì khi nhấn "Huỷ"
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    // THÊM MỚI: Phương thức gọi API để xoá tài khoản
    private void performAccountDeletion() {
        // Hiển thị một Toast cho người dùng biết quá trình đang diễn ra
        Toast.makeText(getContext(), "Đang xử lý...", Toast.LENGTH_SHORT).show();

        // Lấy service API đã được xác thực (có token)
        ApiService apiService = ApiClient.getAuthAPI(requireContext());
        Call<DeleteAccountResponse> call = apiService.deleteAccount();

        call.enqueue(new Callback<DeleteAccountResponse>() {
            @Override
            public void onResponse(@NonNull Call<DeleteAccountResponse> call, @NonNull Response<DeleteAccountResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    // Nếu xoá thành công
                    Toast.makeText(getContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                    // Xoá token đã lưu
                    TokenManager.deleteToken(requireContext());
                    // Chuyển về màn hình đăng nhập
                    navigateToLoginScreen();
                } else {
                    // Nếu có lỗi từ server (ví dụ: token hết hạn, lỗi server...)
                    Toast.makeText(getContext(), "Xoá tài khoản thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeleteAccountResponse> call, @NonNull Throwable t) {
                // Nếu có lỗi mạng
                Toast.makeText(getContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // THÊM MỚI: Phương thức để điều hướng về màn hình đăng nhập
    private void navigateToLoginScreen() {
        // Tạo Intent để mở LoginActivity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        // Các flag này sẽ xoá tất cả các màn hình trước đó và tạo một task mới
        // Đảm bảo người dùng không thể nhấn "Back" để quay lại màn hình cũ sau khi đã đăng xuất/xoá tài khoản
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        // Kết thúc Activity chứa Fragment này (nếu cần)
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}