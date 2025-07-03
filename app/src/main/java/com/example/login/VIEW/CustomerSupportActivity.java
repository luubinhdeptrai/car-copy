// File: com/example/login/VIEW/CustomerSupportActivity.java
package com.example.login.VIEW;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.login.API.ApiClient;
import com.example.login.API.ApiService;
import com.example.login.ADAPTERS.IssueAdapter;
import com.example.login.MODELS.CreateIssueRequest;
import com.example.login.MODELS.Issue;
import com.example.login.MODELS.IssueListResponse;
import com.example.login.MODELS.IssueResponse;
import com.example.login.MODELS.UpdateIssueRequest;
import com.example.login.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerSupportActivity extends AppCompatActivity implements IssueAdapter.OnItemClickListener {

    private Toolbar toolbar;
    private EditText etTitle, etDescription;
    private Spinner spinnerType;
    private Button btnSubmit, btnUpdate, btnDelete;
    private RecyclerView rvIssuesList;
    private TextView tvNoIssues;

    private ApiService apiService;
    private IssueAdapter issueAdapter;
    private List<Issue> issues = new ArrayList<>();
    private Issue selectedIssue = null; // Dùng để lưu issue đang được chọn để sửa/xóa

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_support);

        apiService = ApiClient.getAuthAPI(this); // Khởi tạo ApiService

        initViews();
        setupToolbar();
        setupSpinner();
        setupRecyclerView();
        setupClickListeners();

        fetchMyIssues(); // Tải danh sách issue khi Activity được tạo
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_customer_support);
        etTitle = findViewById(R.id.et_issue_title);
        etDescription = findViewById(R.id.et_issue_description);
        spinnerType = findViewById(R.id.spinner_issue_type);
        btnSubmit = findViewById(R.id.btn_submit_issue);
        btnUpdate = findViewById(R.id.btn_update_issue);
        btnDelete = findViewById(R.id.btn_delete_issue);
        rvIssuesList = findViewById(R.id.rv_issues_list);
        tvNoIssues = findViewById(R.id.tv_no_issues);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Hỗ trợ khách hàng");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupSpinner() {
        // ArrayAdapter sẽ tự động dùng @array/issue_types
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.issue_types, // Đảm bảo bạn đã định nghĩa cái này trong res/values/arrays.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
    }

    private void setupRecyclerView() {
        issueAdapter = new IssueAdapter(this, issues, this); // 'this' là listener cho onItemClick
        rvIssuesList.setLayoutManager(new LinearLayoutManager(this));
        rvIssuesList.setAdapter(issueAdapter);
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> createIssue());
        btnUpdate.setOnClickListener(v -> updateIssue());
        btnDelete.setOnClickListener(v -> deleteIssue());
    }

    // Phương thức kiểm tra và hiển thị/ẩn "Bạn chưa gửi vấn đề nào."
    private void checkNoIssuesVisibility() {
        if (issues.isEmpty()) {
            tvNoIssues.setVisibility(View.VISIBLE);
            rvIssuesList.setVisibility(View.GONE);
        } else {
            tvNoIssues.setVisibility(View.GONE);
            rvIssuesList.setVisibility(View.VISIBLE);
        }
    }

    //region API Calls
    private void fetchMyIssues() {
        apiService.getMyIssues().enqueue(new Callback<IssueListResponse>() {
            @Override
            public void onResponse(@NonNull Call<IssueListResponse> call, @NonNull Response<IssueListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    issues.clear();
                    if (response.body().getData() != null) {
                        issues.addAll(response.body().getData());
                    }
                    issueAdapter.notifyDataSetChanged();
                    checkNoIssuesVisibility();
                } else {
                    Toast.makeText(CustomerSupportActivity.this, "Không thể tải danh sách vấn đề: " + response.message(), Toast.LENGTH_SHORT).show();
                    checkNoIssuesVisibility();
                }
            }

            @Override
            public void onFailure(@NonNull Call<IssueListResponse> call, @NonNull Throwable t) {
                Toast.makeText(CustomerSupportActivity.this, "Lỗi mạng khi tải danh sách vấn đề: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                checkNoIssuesVisibility();
            }
        });
    }

    private void createIssue() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Tiêu đề và mô tả không được để trống.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false); // Vô hiệu hóa nút để tránh gửi nhiều lần

        CreateIssueRequest request = new CreateIssueRequest(title, description, type);
        apiService.createIssue(request).enqueue(new Callback<IssueResponse>() {
            @Override
            public void onResponse(@NonNull Call<IssueResponse> call, @NonNull Response<IssueResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(CustomerSupportActivity.this, "Vấn đề đã được gửi thành công!", Toast.LENGTH_SHORT).show();
                    if (response.body().getData() != null) {
                        issues.add(0, response.body().getData()); // Thêm vào đầu danh sách
                        issueAdapter.notifyItemInserted(0);
                        rvIssuesList.scrollToPosition(0);
                    }
                    clearInputFields();
                    checkNoIssuesVisibility();
                } else {
                    String errorMessage = response.body() != null ? response.body().getMessage() : "Không thể gửi vấn đề.";
                    Toast.makeText(CustomerSupportActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
                btnSubmit.setEnabled(true);
            }

            @Override
            public void onFailure(@NonNull Call<IssueResponse> call, @NonNull Throwable t) {
                Toast.makeText(CustomerSupportActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnSubmit.setEnabled(true);
            }
        });
    }

    private void updateIssue() {
        if (selectedIssue == null || TextUtils.isEmpty(selectedIssue.getId())) {
            Toast.makeText(this, "Vui lòng chọn một vấn đề để sửa.", Toast.LENGTH_SHORT).show();
            return;
        }

        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String type = spinnerType.getSelectedItem().toString();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(description)) {
            Toast.makeText(this, "Tiêu đề và mô tả không được để trống.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        // Giữ nguyên trạng thái nếu không muốn thay đổi qua UI
        String currentStatus = selectedIssue.getStatus();
        UpdateIssueRequest request = new UpdateIssueRequest(title, description, type, currentStatus);

        apiService.updateIssue(selectedIssue.getId(), request).enqueue(new Callback<IssueResponse>() {
            @Override
            public void onResponse(@NonNull Call<IssueResponse> call, @NonNull Response<IssueResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(CustomerSupportActivity.this, "Vấn đề đã được cập nhật!", Toast.LENGTH_SHORT).show();
                    if (response.body().getData() != null) {
                        // Cập nhật đối tượng trong danh sách
                        int index = issues.indexOf(selectedIssue);
                        if (index != -1) {
                            issues.set(index, response.body().getData()); // Thay thế bằng dữ liệu mới nhất từ server
                            issueAdapter.notifyItemChanged(index);
                        }
                    }
                    clearInputFields();
                    resetEditMode();
                } else {
                    String errorMessage = response.body() != null ? response.body().getMessage() : "Không thể cập nhật vấn đề.";
                    Toast.makeText(CustomerSupportActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            }

            @Override
            public void onFailure(@NonNull Call<IssueResponse> call, @NonNull Throwable t) {
                Toast.makeText(CustomerSupportActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        });
    }

    private void deleteIssue() {
        if (selectedIssue == null || TextUtils.isEmpty(selectedIssue.getId())) {
            Toast.makeText(this, "Vui lòng chọn một vấn đề để xóa.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);

        apiService.deleteIssue(selectedIssue.getId()).enqueue(new Callback<IssueResponse>() {
            @Override
            public void onResponse(@NonNull Call<IssueResponse> call, @NonNull Response<IssueResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(CustomerSupportActivity.this, "Vấn đề đã được xóa thành công!", Toast.LENGTH_SHORT).show();
                    int index = issues.indexOf(selectedIssue);
                    if (index != -1) {
                        issues.remove(index);
                        issueAdapter.notifyItemRemoved(index);
                    }
                    clearInputFields();
                    resetEditMode();
                    checkNoIssuesVisibility();
                } else {
                    String errorMessage = response.body() != null ? response.body().getMessage() : "Không thể xóa vấn đề.";
                    Toast.makeText(CustomerSupportActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            }

            @Override
            public void onFailure(@NonNull Call<IssueResponse> call, @NonNull Throwable t) {
                Toast.makeText(CustomerSupportActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
            }
        });
    }
    //endregion

    //region UI Helpers
    @Override
    public void onItemClick(Issue issue) {
        selectedIssue = issue;
        etTitle.setText(issue.getTitle());
        etDescription.setText(issue.getDescription());

        // Đặt Spinner đến giá trị tương ứng
        ArrayAdapter<CharSequence> adapter = (ArrayAdapter<CharSequence>) spinnerType.getAdapter();
        if (adapter != null) {
            int spinnerPosition = adapter.getPosition(issue.getType());
            spinnerType.setSelection(spinnerPosition);
        }

        // Hiển thị nút Sửa và Xóa, ẩn nút Gửi
        btnSubmit.setVisibility(View.GONE);
        btnUpdate.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.VISIBLE);
    }

    private void clearInputFields() {
        etTitle.setText("");
        etDescription.setText("");
        spinnerType.setSelection(0); // Đặt lại mặc định là "other" hoặc giá trị đầu tiên
    }

    private void resetEditMode() {
        selectedIssue = null;
        btnSubmit.setVisibility(View.VISIBLE);
        btnUpdate.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
    }
    //endregion
}