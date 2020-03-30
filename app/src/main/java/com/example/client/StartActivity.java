package com.example.client;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.lang.ref.WeakReference;
import java.net.InetAddress;

import static com.example.client.AppBase.APP_SETTINGS_FILE;
import static com.example.client.AppBase.APP_SETTINGS_FILE_LOGIN;
import static com.example.client.AppBase.APP_SETTINGS_FILE_PASSWORD;
import static com.example.client.AppBase.APP_SETTINGS_FILE_SERVER_IP;
import static com.example.client.AppBase.APP_SETTINGS_FILE_SERVER_PORT;

public class StartActivity extends AppCompatActivity{
    EditText loginView; // Поле ввода логина
    EditText passwordView; // Поле ввода пароля

    Button sign_in_button; // Кнока входа
    Button sign_up_button; // Кнопка регистрации
    Button guest_button; // Кнопка входа в качестве гостя
    Button settings_button; // Кнопка настроек

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_activity); // Подключение нужного интерфеса

        // Загрузка локальных сохранённых данных приложения
        AppBase.app_settings = this.getSharedPreferences(APP_SETTINGS_FILE, Context.MODE_PRIVATE); // Получение ссылки на файл настроек

        String serverIp = AppBase.app_settings.getString(APP_SETTINGS_FILE_SERVER_IP,"192.168.0.100");
        AppBase.serverPort = AppBase.app_settings.getInt(APP_SETTINGS_FILE_SERVER_PORT,8080);

        AppBase.login = AppBase.app_settings.getString(APP_SETTINGS_FILE_LOGIN,"");
        AppBase.password = AppBase.app_settings.getString(APP_SETTINGS_FILE_PASSWORD,"");

        try{
            AppBase.serverIp = InetAddress.getByName(serverIp);  // Установка адреса сервера
        }
        catch (Exception ignored){}

        loginView = findViewById(R.id.login);                   //
        passwordView = findViewById(R.id.password);             // Связываение
        sign_in_button = findViewById(R.id.sign_in);            // кнопок
        sign_up_button = findViewById(R.id.sign_up);            // с
        guest_button = findViewById(R.id.guest);                // интерфейсом
        settings_button = findViewById(R.id.settings_head);     //

        //Проверка разрешений
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED) { // Разрешений нет
            setButtonsEnabled(false); // Отключение кнопок

            // Запрос о предоставлении (для  API >= 23)
            //if (Build.VERSION.SDK_INT >= 23) {
                String[] permissions = new String[]{Manifest.permission.INTERNET};
                ActivityCompat.requestPermissions(this, permissions, AppBase.REQUEST_INTERNET_ID);
            //}

        }
    }

    @Override // Действия при старте активности
    public void onStart(){
        AppBase.currentActivity = new WeakReference<AppCompatActivity>(this);   // Установка текущей
        AppBase.currentPage = AppActivity.Start;                                        // активности
        super.onStart();
    }

    @Override // Действия при закрытии активности
    public void onDestroy() {
        AppBase.stopServerTasks();
        super.onDestroy();
    }

    @Override // Действия при ответе на запрос разрешений
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == AppBase.REQUEST_INTERNET_ID){ // Нужные разрешения
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED ){ // Разрешения приняты

                setButtonsEnabled(true); // Включение кнопок
            }
        }
    }

    // Кнопка входа
    public void sign_in(View view){
        setButtonsEnabled(false); // Отключение кнопок

        AppBase.login = loginView.getText().toString().trim(); // Получение логина
        AppBase.password = passwordView.getText().toString().trim(); // Получение пароля

        if (AppBase.login.isEmpty() || AppBase.password.isEmpty()){ // Проверка пароля и логина на пустоту
            setButtonsEnabled(true); // Включение кнопок
            Toast.makeText(this,"Заполните все поля",Toast.LENGTH_LONG).show();
        }
        else{
            AppBase.serverTasks.start(Task.Sign_in);
        }
    }

    // Кнопка регистрации
    public void sign_up(View view){
        setButtonsEnabled(false); // Отключение кнопок

        AppBase.login = loginView.getText().toString().trim(); // Получение логина
        AppBase.password = passwordView.getText().toString().trim(); // Получение пароля

        if (AppBase.login.isEmpty() || AppBase.password.isEmpty()){ // Проверка пароля и логина на пустоту
            setButtonsEnabled(true); // Включение кнопок
            Toast.makeText(this,"Заполните все поля",Toast.LENGTH_LONG).show();
        }
        else{
            AppBase.serverTasks.start(Task.Sign_up);
        }
    }

    // Кнопка перехода к настройкам
    public void settings_button(View view){
        setButtonsEnabled(false); // Отключение кнопок

        this.startActivity(new Intent(this, SettingsActivity.class)); // Запуск новой активности (настроек)

        setButtonsEnabled(true); // Включение кнопок
    }

    // Кнопка входа в качестве гостя
    public void guest_button(View view){
        setButtonsEnabled(false); // Отключение кнопок
        startMainActivity();
    }

    // Запуск одной из страниц и закрытие стартовой
    void startMainActivity(){
        this.startActivity(new Intent(this, CalculateActivity.class));
        this.finish();
    }

    // Включение/выключение кнопок, чтобы случайно запустить оправку данных ещё раз
    void setButtonsEnabled(boolean enabled){
        sign_in_button.setEnabled(enabled);   //
        sign_up_button.setEnabled(enabled);   // Отключение
        guest_button.setEnabled(enabled);     // кнопок
        settings_button.setEnabled(enabled);  //
    }
}
