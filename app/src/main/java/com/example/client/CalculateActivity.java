package com.example.client;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Objects;

public class CalculateActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    
    @Override // Создание страницы
    protected void onCreate(Bundle savedInstanceState) {
        AppBase.currentActivity = new WeakReference<AppCompatActivity>(this);   // Установка текущей
        AppBase.currentPage = AppActivity.Calculate;                                    // активности

        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate_activity); // Подключение нужного интерфеса

        NavigationView navigationView = findViewById(R.id.calculate_nav_view);  // Подключение навигационной
        navigationView.setNavigationItemSelectedListener(this);                 // панели
    }

    @Override
    public void onDestroy(){
        AppBase.stopServerTasks();
        super.onDestroy();
    }

    @Override // Действия при выборе на панели навигации
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();  // Переход к выбранной
        AppBase.navigation(id);     // активности

        DrawerLayout drawer = findViewById(R.id.calculate_drawer_layout);   // Закрытие боковой
        drawer.closeDrawer(GravityCompat.START);                            // панели

        return true;
    }

    // T T T T  И     И   М     М     У     У   Р Р Р
    //    T     И   И И   М М М М      У  У     Р    Р
    //    T     И  И  И   М  М  М       У       Р Р Р
    //    T     И И   И   М     М      У        Р
    //    T     И     И   М     М     У         Р

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