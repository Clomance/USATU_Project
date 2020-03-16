package com.example.server_client;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.navigation.NavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Objects;

public class CalculateActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    
    @Override // Создание страницы
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate_activity);

        AppBase.currentActivity = new WeakReference<Activity>(this);    // Установка текущей
        AppBase.currentAppActivity = AppActivity.Calculate;                      // активности

        NavigationView navigationView = findViewById(R.id.calculate_nav_view);  // Подключение навигационной
        navigationView.setNavigationItemSelectedListener(this);                 // панели
    }

    @Override // Действия при выборе на панели навигации
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        AppBase.navigation(id);
        DrawerLayout drawer = findViewById(R.id.calculate_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // TTTTTTT И     И   М     М     У     У   РРРРР
    //    T    И   И И   М М М М      У  У     Р    Р
    //    T    И  И  И   М  М  М       У       РРРРР
    //    T    И И   И   М     М      У        Р
    //    T    И     И   М     М     У         Р

    // Функция при клике (можно не только кнопку поставить)
    public void dataPick(View view){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    // Класс для диалога с датой
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);        // Установка
            int month = c.get(Calendar.MONTH);      // текущей
            int day = c.get(Calendar.DAY_OF_MONTH); // даты

            return new DatePickerDialog(Objects.requireNonNull(getActivity()), this, year, month, day);
        }
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            // Установка даты
        }
    }
}