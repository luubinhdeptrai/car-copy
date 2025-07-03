package com.example.login.MODELS;

public class FilterOption {
    private String name;
    private boolean isSelected;
    // Thêm các trường khác nếu cần, ví dụ: id, value...

    public FilterOption(String name, boolean isSelected) {
        this.name = name;
        this.isSelected = isSelected;
    }
    public String getName() { return name; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
}
