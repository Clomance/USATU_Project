package com.example.client;

import android.app.Activity;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.net.InetAddress;

enum AppActivity{
    Start,
    Settings,
    Calculate,
    Info
}

class AppBase {
    //Постоянные
    final static int REQUEST_INTERNET_ID = 0;

    //Текущие
    static InetAddress serverIp; // Адрес для подключения
    static int serverPort = 8080; // Порт для подключения
    static int[] tokens = null; // Токены для быстрой авторизации
    static ServerTasks serverTasks = new ServerTasks(); // см. класс ServerTasks

    //Приложение
    static boolean permissions = false; // Флаг разрешений: true - есть, false - нет

    static WeakReference<AppCompatActivity> currentActivity;    // Текущая активность (страничка)
    static AppActivity currentPage;                             // Текущая активность (номер странички)

    // Функция для навигации - сменяет страницы
    static void navigation(int id){
        switch (id){
            case R.id.nav_calculator:
                AppBase.changeActivity(CalculateActivity.class);
                break;

            case R.id.nav_settings:
                AppBase.changeActivity(SettingsActivity.class);
                break;

            case R.id.nav_info:
                AppBase.changeActivity(InfoActivity.class);
                break;

            default:
                break;
        }
    }

    // Смена страницы (открытие новой и закрытие предыдущей)
    private static void changeActivity(Class c){
        Activity activity = AppBase.currentActivity.get();
        if (activity.getClass() != c){
            Intent reg = new Intent(AppBase.currentActivity.get(), c);
            activity.startActivity(reg);
            activity.finish();
        }
    }
}
