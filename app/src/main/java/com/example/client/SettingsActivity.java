package com.example.client;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;
import java.net.InetAddress;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    EditText addressView; // Поле для ввода адреса сервера
    EditText portView; // Поле для ввода порта сервера

    @Override // Создание страницы
    protected void onCreate(Bundle savedInstanceState) {
        AppBase.currentActivity = new WeakReference<AppCompatActivity>(this);   // Установка текущей
        AppBase.currentPage = AppActivity.Settings;                                     // активности

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity); // Подключение нужного интерфеса

        if (AppBase.currentPage == AppActivity.Start){
            DrawerLayout drawer = findViewById(R.id.settings_drawer_layout);        // Отключение навигационной панели
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);         // (отключение возвожности выдвинуть её)
        }
        else{
            NavigationView navigationView = findViewById(R.id.settings_nav_view);   // Подключение навигационной
            navigationView.setNavigationItemSelectedListener(this);                 // панели
        }

        addressView = findViewById(R.id.address);   // Связывание
        portView = findViewById(R.id.port);         // с интерфейсом

        addressView.setText(AppBase.serverIp.getHostAddress()); // Установка текущих
        portView.setText(String.valueOf(AppBase.serverPort));   // значений
    }

    @Override // Действия при выборе на панели навигации
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        AppBase.navigation(id);
        DrawerLayout drawer = findViewById(R.id.settings_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // Действие кнопки сохранения
    public void save(View view){
        Button save_button = (Button) view; // Связыавание с кнопкой

        String address = addressView.getText().toString();  // Получение текста
        String port = portView.getText().toString();        // из полей

        try{
            AppBase.serverIp = InetAddress.getByName(address);  // Конвертирование в нужный формат
            AppBase.serverPort = Integer.parseInt(port);        // и сохранение
            Toast.makeText(this,"Сохранено",Toast.LENGTH_LONG).show();
        }
        catch (Exception e){
            Toast.makeText(this,"Не верные данные",Toast.LENGTH_LONG).show();
        }
    }
}