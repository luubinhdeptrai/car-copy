package com.example.login.MODELS;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateModel {
    private Date date;
    private boolean isSelected;

    // Constructor này phải có để chấp nhận 2 đối số
    public DateModel(Date date, boolean isSelected) {
        this.date = date;
        this.isSelected = isSelected;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    // Helper methods for display (giữ nguyên hoặc cập nhật nếu có)
    public String getDayOfMonth() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd", Locale.getDefault());
        return dateFormat.format(date);
    }

    public String getMonthOfYear() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM", Locale.getDefault());
        return dateFormat.format(date);
    }

    public String getDayOfWeekShort() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE", Locale.getDefault());
        return dateFormat.format(date);
    }
}