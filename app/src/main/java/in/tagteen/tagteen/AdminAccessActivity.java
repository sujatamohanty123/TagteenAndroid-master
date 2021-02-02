package in.tagteen.tagteen;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.Calendar;
import java.util.List;

import in.tagteen.tagteen.Adapters.AdminAccessAdapter;
import in.tagteen.tagteen.Model.RewardsInfoModels;
import in.tagteen.tagteen.apimodule_retrofit.API_Call_Retrofit;
import in.tagteen.tagteen.apimodule_retrofit.Apimethods;
import in.tagteen.tagteen.util.Utils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminAccessActivity extends AppCompatActivity {
    private ImageView imgBack;
    private RecyclerView recyclerView;
    private EditText txtStartDate;
    private EditText txtEndDate;
    private EditText txtSearch;
    private ProgressBar loadingSpinner;
    private SwipeRefreshLayout swipeRefreshLayout;

    private Calendar startDate;
    private Calendar endDate;

    private List<RewardsInfoModels.RewardsInfoModel> dataList;
    private AdminAccessAdapter adminAccessAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rewards_admin);

        this.initWidgets();
        this.bindEvents();
    }

    private void initWidgets() {
        this.imgBack = findViewById(R.id.imgBack);
        this.recyclerView = findViewById(R.id.recyclerView);
        this.txtStartDate = findViewById(R.id.txtStartDate);
        this.txtEndDate = findViewById(R.id.txtEndDate);
        this.txtSearch = findViewById(R.id.txtSearch);
        this.loadingSpinner = findViewById(R.id.loadingSpinner);
        LinearLayoutManager gridLayoutManager = new LinearLayoutManager(this);
        this.recyclerView.setLayoutManager(gridLayoutManager);
        this.swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        this.swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);

        this.initDates();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadData();
            }
        }, 500);
    }

    private void initDates() {
        this.startDate = Calendar.getInstance();
        this.startDate.add(Calendar.DAY_OF_MONTH, -6);
        this.startDate.set(Calendar.HOUR_OF_DAY, 0);
        this.startDate.set(Calendar.MINUTE, 0);
        this.startDate.set(Calendar.SECOND, 0);
        int dayOfMonth = this.startDate.get(Calendar.DAY_OF_MONTH);
        int monthOfYear = this.startDate.get(Calendar.MONTH) + 1;
        String day = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10) {
            day = "0" + dayOfMonth;
        }
        String month = String.valueOf(monthOfYear);
        if (monthOfYear < 10) {
            month = "0" + monthOfYear;
        }
        String startDateStr = day + "-" + month + "-" + this.startDate.get(Calendar.YEAR);
        this.txtStartDate.setText(startDateStr);

        this.endDate = Calendar.getInstance();
        this.endDate.set(Calendar.HOUR_OF_DAY, 23);
        this.endDate.set(Calendar.MINUTE, 59);
        this.endDate.set(Calendar.SECOND, 59);
        dayOfMonth = this.endDate.get(Calendar.DAY_OF_MONTH);
        monthOfYear = this.endDate.get(Calendar.MONTH) + 1;
        day = String.valueOf(dayOfMonth);
        if (dayOfMonth < 10) {
            day = "0" + dayOfMonth;
        }
        month = String.valueOf(monthOfYear);
        if (monthOfYear < 10) {
            month = "0" + monthOfYear;
        }
        String endDateStr = day + "-" + month + "-" + this.endDate.get(Calendar.YEAR);
        this.txtEndDate.setText(endDateStr);
    }

    private void bindEvents() {
        this.imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        this.txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String key = txtSearch.getText().toString();
                if (adminAccessAdapter != null) {
                    adminAccessAdapter.filter(key);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        this.txtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AdminAccessActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        startDate.set(Calendar.YEAR, year);
                        startDate.set(Calendar.MONTH, monthOfYear);
                        startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        monthOfYear = monthOfYear + 1;

                        String month = String.valueOf(monthOfYear);
                        if (monthOfYear < 10) {
                            month = "0" + monthOfYear;
                        }
                        String day = String.valueOf(dayOfMonth);
                        if (dayOfMonth < 10) {
                            day = "0" + dayOfMonth;
                        }
                        String startDateStr = day + "-" + month + "-" + year;
                        txtStartDate.setText(startDateStr);
                        loadWeeklyLikesCount();
                    }
                }, startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        this.txtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(AdminAccessActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                        endDate.set(Calendar.YEAR, year);
                        endDate.set(Calendar.MONTH, monthOfYear);
                        endDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        monthOfYear = monthOfYear + 1;

                        String month = String.valueOf(monthOfYear);
                        if (monthOfYear < 10) {
                            month = "0" + monthOfYear;
                        }
                        String day = String.valueOf(dayOfMonth);
                        if (dayOfMonth < 10) {
                            day = "0" + dayOfMonth;
                        }
                        String endDateStr = day + "-" + month + "-" + year;
                        txtEndDate.setText(endDateStr);
                        loadWeeklyLikesCount();
                    }
                }, endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullToRefresh();
            }
        });
    }

    private void loadWeeklyLikesCount() {
        /*if (this.adminAccessAdapter != null) {
            this.adminAccessAdapter.loadLikesBetweenDates(this.startDate, this.endDate);
        }*/
    }

    private void pullToRefresh() {
        this.swipeRefreshLayout.setRefreshing(true);
        this.loadData();
    }

    private void loadData() {
        Apimethods methods = API_Call_Retrofit.getRetrofitWithMaxTimeout(this).create(Apimethods.class);
        Call<RewardsInfoModels> call = methods.getShowroomVideoInfo();
        call.enqueue(new Callback<RewardsInfoModels>() {
            @Override
            public void onResponse(Call<RewardsInfoModels> call, Response<RewardsInfoModels> response) {
                loadingSpinner.setVisibility(View.GONE);
                int statusCode = response.code();
                swipeRefreshLayout.setRefreshing(false);
                if (statusCode == 200) {
                    RewardsInfoModels models = response.body();
                    if (models != null) {
                        dataList = models.getDataList();
                        if (dataList != null) {
                            adminAccessAdapter =
                                    new AdminAccessAdapter(
                                            AdminAccessActivity.this, dataList, recyclerView, startDate, endDate);
                            recyclerView.setAdapter(adminAccessAdapter);
                        }
                    }
                } else {
                    Utils.showShortToast(AdminAccessActivity.this, "Failed loading data");
                }
            }

            @Override
            public void onFailure(Call<RewardsInfoModels> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                t.printStackTrace();
                Log.e("AdminAccess", "Failed url:" + call.request().url().toString());
                Utils.showShortToast(AdminAccessActivity.this, t.getMessage());
            }
        });
    }
}
