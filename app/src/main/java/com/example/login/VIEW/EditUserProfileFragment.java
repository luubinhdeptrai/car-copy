package com.example.login.VIEW;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup; // << THÊM IMPORT
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast; // << THÊM IMPORT

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.login.API.ApiClient; // << THÊM IMPORT
import com.example.login.API.ApiService; // << THÊM IMPORT
import com.example.login.MODELS.ProfileResponse; // << THÊM IMPORT
import com.example.login.MODELS.User; // << THÊM IMPORT
import com.example.login.R;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException; // << THÊM IMPORT
import java.text.SimpleDateFormat; // << THÊM IMPORT
import java.util.Calendar;
import java.util.Date; // << THÊM IMPORT
import java.util.Locale; // << THÊM IMPORT
import java.util.TimeZone; // << THÊM IMPORT

import retrofit2.Call; // << THÊM IMPORT
import retrofit2.Callback; // << THÊM IMPORT
import retrofit2.Response; // << THÊM IMPORT

public class EditUserProfileFragment extends Fragment {

    // --- BIẾN MỚI ĐƯỢC THÊM ---
    private ApiService apiService;
    private TextView tvPhoneNumber;
    private TextView tvEditFullName, tvEditEmail, tvEditBirthday;
    private RadioGroup radioGroupGender;
    // -------------------------

    // --- THÊM PHƯƠNG THỨC ONCREATE ĐỂ KHỞI TẠO API SERVICE ---
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getAuthAPI(requireContext());
    }
    // --------------------------------------------------------

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- CẬP NHẬT: Ánh xạ các view còn lại ---
        tvPhoneNumber = view.findViewById(R.id.tv_phone_number_readonly);
        tvEditFullName = view.findViewById(R.id.tv_edit_full_name);
        tvEditEmail = view.findViewById(R.id.tv_edit_email);
        tvEditBirthday = view.findViewById(R.id.tv_edit_birthday);
        radioGroupGender = view.findViewById(R.id.gender_radio_group_details); // Giả sử RadioGroup có id này
        // ----------------------------------------

        RelativeLayout editFullNameLayout = view.findViewById(R.id.edit_full_name_layout);
        RelativeLayout editEmailLayout = view.findViewById(R.id.edit_email_layout);
        RelativeLayout editBirthdayLayout = view.findViewById(R.id.edit_birthday_layout);

        editFullNameLayout.setOnClickListener(v -> showEditTextDialog("Full name", tvEditFullName));
        editEmailLayout.setOnClickListener(v -> showEditTextDialog("Email", tvEditEmail));
        editBirthdayLayout.setOnClickListener(v -> showDatePicker());

        // --- THÊM MỚI: Gọi hàm tải dữ liệu người dùng ---
        loadInitialUserData();
        // ------------------------------------------
    }

    // --- THÊM MỚI: Hàm tải và hiển thị dữ liệu ban đầu ---
    private void loadInitialUserData() {
        apiService.getUserProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    if (user != null) {
                        // Điền thông tin vào các view
                        tvPhoneNumber.setText(user.getPhoneNumber());
                        tvEditFullName.setText(user.getFullname());

                        tvEditEmail.setText(isNullOrEmpty(user.getEmail()) ? "Please update" : user.getEmail());
                        tvEditBirthday.setText(isNullOrEmpty(user.getDateOfBirth()) ? "Please update" : formatDate(user.getDateOfBirth()));

                        // Xử lý RadioGroup cho giới tính
                        String gender = user.getGender();
                        if (!isNullOrEmpty(gender)) {
                            switch (gender) {
                                case "Male":
                                    radioGroupGender.check(R.id.radio_male);
                                    break;
                                case "Female":
                                    radioGroupGender.check(R.id.radio_female);
                                    break;
                                case "Others":
                                    radioGroupGender.check(R.id.radio_other);
                                    break;
                            }
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load user data.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    // ----------------------------------------------------

    private void showEditTextDialog(String title, TextView textViewToUpdate) {
        // ... code hiện có của bạn
    }

    private void showDatePicker() {
        // ... code hiện có của bạn
    }

    // --- CÁC HÀM HỖ TRỢ ĐƯỢC THÊM MỚI ---
    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private String formatDate(String utcDateString) {
        if (isNullOrEmpty(utcDateString)) {
            return "Please update";
        }
        try {
            SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
            utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = utcFormat.parse(utcDateString);

            SimpleDateFormat desiredFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return desiredFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return "Invalid date";
        }
    }
    // ---------------------------------------
}