package com.example.client;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Objects;

public class CalculateActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static Double deposit = null;

    static AppBase.Date data1;
    static AppBase.Date data2;

    static Double result = null;

    static WeakReference<TextView> data_result;

    @Override // Создание страницы
    protected void onCreate(Bundle savedInstanceState) {
        AppBase.currentActivity = new WeakReference<AppCompatActivity>(this);   // Установка текущей
        AppBase.currentPage = AppActivity.Calculate;                                    // активности

        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate_activity); // Подключение нужного интерфеса

        NavigationView navigationView = findViewById(R.id.calculate_nav_view);  // Подключение навигационной
        navigationView.setNavigationItemSelectedListener(this);                 // панели

        TextView data_resultView = findViewById(R.id.data_result);
        data_result = new WeakReference<>(data_resultView);
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

    // Вывод диалогового окна с календарём (для связывания с интерфесом)
    public void dataPick(View view){
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.selectedView = (TextView) view;
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    // Класс для диалога с датой
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        TextView selectedView;
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            if (selectedView.getId() == R.id.data1){
                if (data1 != null){
                    return new DatePickerDialog(Objects.requireNonNull(getActivity()), this, data1.year, data1.month, data1.day);
                }
                else{
                    final Calendar c = Calendar.getInstance(); // Получение текущей даты
                    int year = c.get(Calendar.YEAR);        // Установка
                    int month = c.get(Calendar.MONTH);      // текущей
                    int day = c.get(Calendar.DAY_OF_MONTH); // даты
                    return new DatePickerDialog(Objects.requireNonNull(getActivity()), this, year, month, day);
                }
            }
            else{
                if (data2 != null){
                    return new DatePickerDialog(Objects.requireNonNull(getActivity()), this, data2.year, data2.month, data2.day);
                }
                else{
                    final Calendar c = Calendar.getInstance(); // Получение текущей даты
                    int year = c.get(Calendar.YEAR);        // Установка
                    int month = c.get(Calendar.MONTH);      // текущей
                    int day = c.get(Calendar.DAY_OF_MONTH); // даты
                    return new DatePickerDialog(Objects.requireNonNull(getActivity()), this, year, month, day);
                }
            }
        }

        @Override // Действия при установке даты
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            String text = String.format("%d.%d.%d", dayOfMonth, month + 1, year);   // Вывод даты
            selectedView.setText(text);                                             // в нужное поле


            if (selectedView.getId() == R.id.data1){                // Сохранение
                data1 = new AppBase.Date(year, month, dayOfMonth);  // введённой
            }                                                       // даты
            else{                                                   //
                data2 = new AppBase.Date(year, month, dayOfMonth);  //
            }                                                       //

            if (data1 != null && data2 != null){                        //
                if (data1.toCalendar().before(data2.toCalendar())){                               //
                    long data1_millis = data1.toCalendar().getTimeInMillis();        //
                    long data2_millis = data2.toCalendar().getTimeInMillis();        //
                    long result = data2_millis - data1_millis;          //
                    int days = (int) result / (24 * 60 * 60 * 1000);    // Подсчёт
                    text = days + " дней";                              // дней
                    data_result.get().setText(text);                    // между
                }                                                       // введёнными
                else{                                                   // датами
                    data_result.get().setText("Ошибка");                //
                }                                                       //
            }                                                           //
            else{                                                       //
                data_result.get().setText("Результат");                 //
            }                                                           //
        }
    }

    // Вывод диалогового списка (для связывания с интерфесом)
    public void listPick(View view){
        ListPickerFragment newFragment = new ListPickerFragment();
        newFragment.selectedView = (TextView) view;
        newFragment.show(getSupportFragmentManager(), "listPicker");
    }

    // Класс для диалогового списка
    public static class ListPickerFragment extends DialogFragment{
        TextView selectedView;
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

            final String[] list = new String[]{"один элемент","два элемент","три элемент"};

            builder.setTitle("Заголовок").setItems(list, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Выбор из списка
                    selectedView.setText(list[which]);
                }
            });
            return builder.create();
        }
    }
}