package com.ramindu.weeraman.weather.views;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ramindu.weeraman.weather.R;
import com.ramindu.weeraman.weather.WeatherApp;
import com.ramindu.weeraman.weather.models.ListItemView;
import com.ramindu.weeraman.weather.utils.ItemTouchHelperCallback;
import com.ramindu.weeraman.weather.utils.onSwipeListener;
import com.ramindu.weeraman.weather.viewmodels.LoadingStatus;
import com.ramindu.weeraman.weather.viewmodels.ViewModelFactory;
import com.ramindu.weeraman.weather.viewmodels.WeatherViewModel;
import com.ramindu.weeraman.weather.views.adapters.CityWeatherAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements onSwipeListener {

    @BindView(R.id.recyclerViewWeatherCards)
    RecyclerView recyclerView;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.fabAddCity)
    FloatingActionButton fabAddCity;
    @Inject
    ViewModelFactory mFactory;
    private List<ListItemView> cities = new ArrayList<ListItemView>();
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog progressDialog;
    private WeatherViewModel viewModel;
    private String cityToAdd = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        progressDialog = new ProgressDialog(this);

        initViewModel();
        initRecyclerView();
        setObservers();

        fabAddCity.setOnClickListener(view -> {
            showAlertAddCity(getString(R.string.add_city_header),
                    getString(R.string.add_city_message));
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.google_blue,
                R.color.google_green, R.color.google_red, R.color.google_yellow);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
        });

        viewModel.loadInitialWeatherList();
    }

    private void initViewModel() {
        ((WeatherApp) getApplication()).getAppComponent().inject(this);
        viewModel = ViewModelProviders.of(this, mFactory).get(WeatherViewModel.class);
    }

    private void initRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        adapter = new CityWeatherAdapter(cities, R.layout.weather_card, this,
                null);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(this);
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void setObservers() {

        viewModel.getSelectedCitiesLiveData().observe(this, localCities -> {
            cities.clear();
            cities.addAll(localCities);
            adapter.notifyDataSetChanged();
        });


        viewModel.getAddedCityLiveData().observe(this, listItemView -> {
            cities.add(listItemView);
            adapter.notifyItemInserted(cities.size() - 1);
            recyclerView.scrollToPosition(cities.size() - 1);
        });


        viewModel.getWeatherDataLoadingStatus().observe(this, loadingStatus ->
                handleStatus(loadingStatus, getString(R.string.add_city_failed))
        );


        viewModel.getDeleteCityStatus().observe(this, loadingStatus ->
                handleStatus(loadingStatus, getString(R.string.delete_city_failed))
        );

    }

    private void refreshData() {
        viewModel.loadInitialWeatherList();
        swipeRefreshLayout.setRefreshing(false);
    }

    public void showAlertAddCity(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (title != null) builder.setTitle(title);
        if (message != null) builder.setMessage(message);
        final View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_city, null);
        builder.setView(view);
        final EditText editTextAddCityName = view.findViewById(R.id.editTextAddCityName);

        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        builder.setPositiveButton(getString(R.string.add), (dialog, which) -> {
            cityToAdd = editTextAddCityName.getText().toString();
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            addCity(cityToAdd);
        });


        builder.setNegativeButton(getString(R.string.cancel), (dialog, which) -> {
            dialog.cancel();
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
        });

        builder.create().show();
    }

    public void addCity(String cityName) {
        if (TextUtils.isEmpty(cityName)) {
            showToastMessage(getString(R.string.city_name_empty));
        } else if(viewModel.isCityAlreadyAvailable(cityName)){
            showToastMessage(getString(R.string.city_already_available));
        }else {
            viewModel.addCity(cityName);
        }
    }

    @Override
    public void onItemDelete(int position) {
        viewModel.removeCity(cities.get(position));
        cities.remove(position);
        adapter.notifyItemRemoved(position);
    }

    public void showProgress() {
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
    }


    private void hideProgress() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showToastMessage(String toastMsg) {
        Toast.makeText(this, toastMsg,
                Toast.LENGTH_LONG).show();
    }

    private void handleStatus(LoadingStatus loadingStatus, String msg) {
        if (loadingStatus == LoadingStatus.FAIL) {
            showToastMessage(msg);
            hideProgress();
        } else if (loadingStatus == LoadingStatus.LOADING) {
            showProgress();
        } else if (loadingStatus == LoadingStatus.SUCCESS) {
            hideProgress();
        }
    }
}

