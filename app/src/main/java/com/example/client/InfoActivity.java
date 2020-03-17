package com.example.client;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;

public class InfoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override // Создание страницы
    protected void onCreate(Bundle savedInstanceState) {
        AppBase.currentActivity = new WeakReference<AppCompatActivity>(this);   // Установка текущей
        AppBase.currentPage = AppActivity.Info;                                         // активности

        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity); // Подключение нужного интерфеса

        NavigationView navigationView = findViewById(R.id.info_nav_view);   // Подключение навигационной
        navigationView.setNavigationItemSelectedListener(this);             // панели
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        AppBase.navigation(id);

        DrawerLayout drawer = findViewById(R.id.info_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
