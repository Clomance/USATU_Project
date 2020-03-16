package com.example.client;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.navigation.NavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.net.InetAddress;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    EditText addressView; // Поле для ввода адреса сервера
    EditText portView; // Поле для ввода порта сервера

    @Override // Создание страницы
    protected void onCreate(Bundle savedInstanceState) {
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

        AppBase.currentActivity = new WeakReference<AppCompatActivity>(this);   // Установка текущей
        AppBase.currentPage = AppActivity.Settings;                              // активности


        addressView = findViewById(R.id.address);   // Связывание
        portView = findViewById(R.id.port);         // с интерфейсом

        addressView.setText(AppBase.serverIp.getHostAddress()); // Установка текущих
        portView.setText(String.valueOf(AppBase.serverPort));   // значений
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    @Override
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
        save_button.setEnabled(false); // Отключение кнопки

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

        save_button.setEnabled(true); // Включение кнопки
    }
}