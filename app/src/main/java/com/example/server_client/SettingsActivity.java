package com.example.server_client;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.design.widget.NavigationView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.net.InetAddress;

public class SettingsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    EditText addressView;
    EditText portView;

    @Override // Создание страницы
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        if (AppBase.currentAppActivity == AppActivity.Start){
            DrawerLayout drawer = findViewById(R.id.settings_drawer_layout);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
        else{
            NavigationView navigationView = findViewById(R.id.settings_nav_view);
            navigationView.setNavigationItemSelectedListener(this);
        }

        AppBase.currentAppActivity = AppActivity.Settings;
        AppBase.currentActivity = new WeakReference<Activity>(this);

        addressView = findViewById(R.id.address);
        portView = findViewById(R.id.port);

        addressView.setText(AppBase.serverIp.getHostAddress());
        portView.setText(String.valueOf(AppBase.serverPort));
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

    public void save(View view){
        String address = addressView.getText().toString();
        String port = portView.getText().toString();
        try{
            AppBase.serverIp = InetAddress.getByName(address);
            AppBase.serverPort = Integer.parseInt(port);
        }
        catch (Exception e){
            Toast.makeText(this,"Не верные данные",Toast.LENGTH_LONG).show();
        }
    }
}