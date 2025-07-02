package com.example.login.VIEW;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.MODELS.ProfileResponse;
import com.example.login.MODELS.UpdateProfileRequest;
import com.example.login.MODELS.User;
import com.example.login.R;
import com.google.android.material.textfield.TextInputEditText;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditUserProfileFragment extends Fragment {

    private ApiService apiService;
    private TextView tvPhoneNumber;
    private TextView tvEditFullName, tvEditEmail, tvEditBirthday;
    private RadioGroup radioGroupGender;
    private Button btnUpdate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getAuthAPI(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các Views
        tvPhoneNumber = view.findViewById(R.id.tv_phone_number_readonly);
        tvEditFullName = view.findViewById(R.id.tv_edit_full_name);
        tvEditEmail = view.findViewById(R.id.tv_edit_email);
        tvEditBirthday = view.findViewById(R.id.tv_edit_birthday);
        radioGroupGender = view.findViewById(R.id.gender_radio_group);
        btnUpdate = view.findViewById(R.id.btn_update);

        // Gán sự kiện cho các layout có thể click
        RelativeLayout editFullNameLayout = view.findViewById(R.id.edit_full_name_layout);
        RelativeLayout editBirthdayLayout = view.findViewById(R.id.edit_birthday_layout);

        editFullNameLayout.setOnClickListener(v -> showEditTextDialog("Full name", tvEditFullName));
        editBirthdayLayout.setOnClickListener(v -> showDatePicker());

        // Gán sự kiện cho nút Update
        btnUpdate.setOnClickListener(v -> performProfileUpdate());

        // Tải dữ liệu ban đầu của người dùng
        loadInitialUserData();
    }

    private void performProfileUpdate() {
        // Lấy dữ liệu từ các trường trên giao diện
        String fullName = tvEditFullName.getText().toString().trim();
        String gender = getGenderFromRadioGroup();
        String birthdayUI = tvEditBirthday.getText().toString();

        // Kiểm tra dữ liệu hợp lệ
        if (TextUtils.isEmpty(fullName)) {
            Toast.makeText(getContext(), "Full name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        // Định dạng lại ngày sinh cho API (từ dd/MM/yyyy -> yyyy-MM-dd)
        String birthdayForApi = formatBirthdayForApi(birthdayUI);

        // Tạo đối tượng request để gửi đi
        UpdateProfileRequest request = new UpdateProfileRequest(fullName, birthdayForApi, gender);

        btnUpdate.setEnabled(false);
        Toast.makeText(getContext(), "Updating...", Toast.LENGTH_SHORT).show();

        // Gọi API
        apiService.updateUserProfile(request).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                btnUpdate.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Profile updated successfully!", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(requireView()).popBackStack();
                } else {
                    Toast.makeText(getContext(), "Update failed. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                btnUpdate.setEnabled(true);
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadInitialUserData() {
        apiService.getUserProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    if (user != null) {
                        tvPhoneNumber.setText(user.getPhoneNumber());
                        tvEditFullName.setText(user.getFullname());
                        tvEditEmail.setText(isNullOrEmpty(user.getEmail()) ? "Please update" : user.getEmail());
                        tvEditBirthday.setText(isNullOrEmpty(user.getDateOfBirth()) ? "Please update" : formatDate(user.getDateOfBirth()));

                        String gender = user.getGender();
                        if (!isNullOrEmpty(gender) && radioGroupGender != null) {
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

    private void showEditTextDialog(String title, TextView textViewToUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_text, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextInputEditText editText = dialogView.findViewById(R.id.edit_text_input);

        dialogTitle.setText(title);
        editText.setText(textViewToUpdate.getText());
        editText.setSelection(textViewToUpdate.getText().length());

        builder.setView(dialogView)
                .setPositiveButton("Update", (dialog, id) -> {
                    String newText = editText.getText().toString();
                    textViewToUpdate.setText(newText);
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    String date = selectedDayOfMonth + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    tvEditBirthday.setText(date);
                }, year, month, day);

        datePickerDialog.show();
    }

    private String getGenderFromRadioGroup() {
        int selectedId = radioGroupGender.getCheckedRadioButtonId();
        if (selectedId == R.id.radio_male) {
            return "Male";
        } else if (selectedId == R.id.radio_female) {
            return "Female";
        } else if (selectedId == R.id.radio_other) {
            return "Others";
        }
        return null;
    }

    private String formatBirthdayForApi(String uiDate) {
        if (isNullOrEmpty(uiDate) || uiDate.equalsIgnoreCase("Please update")) {
            return null;
        }
        try {
            SimpleDateFormat uiFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            Date date = uiFormat.parse(uiDate);
            SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            return apiFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

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
}