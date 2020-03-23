package com.example.client;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;

public class InfoActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ListView history;
    TextView head;
    
    @Override // Создание страницы
    protected void onCreate(Bundle savedInstanceState) {
        AppBase.currentActivity = new WeakReference<AppCompatActivity>(this);   // Установка текущей
        AppBase.currentPage = AppActivity.Info;                                         // активности

        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_activity); // Подключение нужного интерфеса

        NavigationView navigationView = findViewById(R.id.info_nav_view);   // Подключение навигационной
        navigationView.setNavigationItemSelectedListener(this);             // панели

        int len = AppBase.history.size();
        if (len == 0){
            head = findViewById(R.id.settings_head);
            head.append("\nПусто");
        }
        else{
            String[] array = new String[len];

            for (int i = 0; i < len; i++){
                array[i] = AppBase.history.get(i).toString();
            }
            ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.list_view_item, array);

            history = findViewById(R.id.history);
            history.setAdapter(adapter);
        }
    }

    @Override // Действия при выборе на панели навигации
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        AppBase.navigation(id);

        DrawerLayout drawer = findViewById(R.id.info_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
