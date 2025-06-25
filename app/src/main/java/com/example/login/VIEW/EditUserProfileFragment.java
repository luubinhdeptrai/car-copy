package com.example.login.VIEW;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.login.R;
import com.google.android.material.textfield.TextInputEditText;
import java.util.Calendar;

public class EditUserProfileFragment extends Fragment {

    private TextView tvEditFullName, tvEditEmail, tvEditBirthday;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_user_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvEditFullName = view.findViewById(R.id.tv_edit_full_name);
        tvEditEmail = view.findViewById(R.id.tv_edit_email);
        tvEditBirthday = view.findViewById(R.id.tv_edit_birthday);

        RelativeLayout editFullNameLayout = view.findViewById(R.id.edit_full_name_layout);
        RelativeLayout editEmailLayout = view.findViewById(R.id.edit_email_layout);
        RelativeLayout editBirthdayLayout = view.findViewById(R.id.edit_birthday_layout);

        // Bắt sự kiện click cho các trường
        editFullNameLayout.setOnClickListener(v -> showEditTextDialog("Full name", tvEditFullName));
        editEmailLayout.setOnClickListener(v -> showEditTextDialog("Email", tvEditEmail));
        editBirthdayLayout.setOnClickListener(v -> showDatePicker());
    }

    private void showEditTextDialog(String title, TextView textViewToUpdate) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_edit_text, null);

        TextView dialogTitle = dialogView.findViewById(R.id.dialog_title);
        TextInputEditText editText = dialogView.findViewById(R.id.edit_text_input);

        dialogTitle.setText(title);
        editText.setText(textViewToUpdate.getText());

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
}