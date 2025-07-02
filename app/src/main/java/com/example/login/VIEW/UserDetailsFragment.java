package com.example.login.VIEW;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup; // << THÊM IMPORT
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.MODELS.ProfileResponse;
import com.example.login.MODELS.User;
import com.example.login.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDetailsFragment extends Fragment {

    private ApiService apiService;
    private TextView tvFullName, tvPhoneNumber, tvEmail, tvBirthday;
    // --- SỬA LỖI: Thay TextView bằng RadioGroup ---
    private RadioGroup genderRadioGroupDetails;
    // ---------------------------------------------

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = ApiClient.getAuthAPI(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_user_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvFullName = view.findViewById(R.id.tv_full_name);
        tvPhoneNumber = view.findViewById(R.id.tv_phone_number);
        tvEmail = view.findViewById(R.id.tv_email);
        tvBirthday = view.findViewById(R.id.tv_birthday);
        // --- SỬA LỖI: Ánh xạ RadioGroup với ID đúng ---
        genderRadioGroupDetails = view.findViewById(R.id.gender_radio_group_details);
        // ------------------------------------------

        loadUserDetails();

        Button updateProfileButton = view.findViewById(R.id.btn_update_profile);
        updateProfileButton.setOnClickListener(v -> {
            Navigation.findNavController(v).navigate(R.id.action_userDetails_to_editProfile);
        });
    }

    private void loadUserDetails() {
        apiService.getUserProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(@NonNull Call<ProfileResponse> call, @NonNull Response<ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    if (user != null) {
                        tvFullName.setText(user.getFullname());
                        tvPhoneNumber.setText(user.getPhoneNumber());
                        tvEmail.setText(isNullOrEmpty(user.getEmail()) ? "Please update." : user.getEmail());
                        tvBirthday.setText(isNullOrEmpty(user.getDateOfBirth()) ? "Please update." : formatDate(user.getDateOfBirth()));

                        // --- SỬA LỖI: Dùng logic switch-case để chọn đúng RadioButton ---
                        String gender = user.getGender();
                        if (!isNullOrEmpty(gender)) {
                            switch (gender) {
                                case "Male":
                                    genderRadioGroupDetails.check(R.id.radio_male_details);
                                    break;
                                case "Female":
                                    genderRadioGroupDetails.check(R.id.radio_female_details);
                                    break;
                                case "Others":
                                    genderRadioGroupDetails.check(R.id.radio_other_details);
                                    break;
                            }
                        }
                        // -------------------------------------------------------------
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to load user details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProfileResponse> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    private String formatDate(String utcDateString) {
        if (isNullOrEmpty(utcDateString)) {
            return "Please update.";
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