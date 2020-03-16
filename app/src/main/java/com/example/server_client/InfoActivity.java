package com.example.server_client;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;

import java.lang.ref.WeakReference;

public class InfoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override // Создание страницы
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity);

        AppBase.currentActivity = new WeakReference<Activity>(this);
        AppBase.currentAppActivity = AppActivity.Info;

        NavigationView navigationView = findViewById(R.id.info_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
