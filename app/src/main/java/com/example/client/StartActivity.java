package com.example.client;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.net.InetAddress;

public class StartActivity extends AppCompatActivity{
    EditText loginView; // Поле ввода логина
    EditText passwordView; // Поле ввода пароля

    Button sign_in_button; // Кнока входа
    Button sign_up_button; // Кнопка регистрации
    Button guest_button; // Кнопка входа в качестве гостя
    //Button settings_button; // Кнопка настроек

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity); // Подключение нужного интерфеса

        AppBase.currentActivity = new WeakReference<Activity>(this);    // Установка текущей
        AppBase.currentAppActivity = AppActivity.Start;                         // активности

        try{                                                            //
            AppBase.serverIp = InetAddress.getByName("192.168.0.100");  // Установка начального (default)
        }                                                               // адреса сервера
        catch (Exception ignored){}                                     //

        loginView = findViewById(R.id.login);           //
        passwordView = findViewById(R.id.password);     // Связываение кнопок
        sign_in_button = findViewById(R.id.sign_in);    // с интерфейсом
        sign_up_button = findViewById(R.id.sign_up);    //
        guest_button = findViewById(R.id.guest);        //

        //Проверка разрешений
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            // Разрешения есть
            AppBase.permissions = true; // Флаг разрешений
        }
        else{ // Разрешений нет
            sign_in_button.setEnabled(false);   //
            sign_up_button.setEnabled(false);   // Отключений кнопок входа
            guest_button.setEnabled(false);     //

            // Запрос о предоставлении (для  API >= 23)
            //if (Build.VERSION.SDK_INT >= 23) {
                String[] permissions = new String[]{Manifest.permission.INTERNET};
                ActivityCompat.requestPermissions(this, permissions, AppBase.REQUEST_INTERNET_ID);
            //}

        }
    }
    @Override // Действия при закрытии активности
    public void onDestroy() {
        super.onDestroy();
    }

    @Override // Действия при ответе на запрос разрешений
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AppBase.REQUEST_INTERNET_ID){ // Нужные разрешения
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED ){ // Разрешения приняты

                AppBase.permissions = true; // Флаг разрешений

                guest_button.setEnabled(true);      //
                sign_in_button.setEnabled(true);    // Включение кнопок входа
                sign_up_button.setEnabled(true);    //
            }
        }
    }

    // Кнопка входа
    public void sign_in(View view){
        String login = loginView.getText().toString().trim(); // Получение логина
        String password = passwordView.getText().toString().trim(); // Получение пароля

        Toast.makeText(this,"Сервера нет)))",Toast.LENGTH_LONG).show();

        if (login.isEmpty() || password.isEmpty()){ // Проверка пароля и логина на пустоту
            //Toast.makeText(this,"Заполните все поля",Toast.LENGTH_LONG).show();
        }
        else{
            //startMainActivity();
        }
    }

    // Кнопка регистрации
    public void sign_up(View view){
        String login = loginView.getText().toString().trim(); // Получение логина
        String password = passwordView.getText().toString().trim(); // Получение пароля

        Toast.makeText(this,"Сервера нет)))",Toast.LENGTH_LONG).show();

        if (login.isEmpty() || password.isEmpty()){ // Проверка пароля и логина на пустоту

            //Toast.makeText(this,"Заполните все поля",Toast.LENGTH_LONG).show();
        }
        else{
            //startMainActivity();
        }
    }

    // Кнопка перехода к настройкам
    public void settings_button(View view){
        Intent reg = new Intent(this, SettingsActivity.class);
        this.startActivity(reg);
    }

    // Кнопка входа в качестве гостя
    public void guest_button(View view){
        startMainActivity();
    }

    // Запуск одной из страниц и закрытие стартовой
    private void startMainActivity(){
        Intent reg = new Intent(this, CalculateActivity.class);
        this.startActivity(reg);
        this.finish();
    }
}
