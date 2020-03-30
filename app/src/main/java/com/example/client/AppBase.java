package com.example.client;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Vector;

// Флаг текущей страницы (нужна только при переходе из стартового окна в настройки)
// украдена из старого проекта:)
enum AppActivity{
    Start,
    Settings,
    Calculate,
    Info
}


class AppBase {
    //Постоянные
    final static String APP_SETTINGS_FILE = "APP_SETTINGS_FILE"; // Название файла с настройками
    final static String APP_SETTINGS_FILE_SERVER_IP = "SERVER_IP"; // Ключ с сохранённым адресом сервера
    final static String APP_SETTINGS_FILE_SERVER_PORT = "SERVER_PORT"; // Ключ с сохранённым портом сервера
    final static String APP_SETTINGS_FILE_LOGIN = "LOGIN"; // Ключ с сохранённым логином
    final static String APP_SETTINGS_FILE_PASSWORD = "PASSWORD"; // Ключ с сохранённым паролем
    final static int REQUEST_INTERNET_ID = 0;

    //Текущие
    static InetAddress serverIp; // Адрес для подключения
    static int serverPort = 8080; // Порт для подключения


    static String login = null;
    static String password = null;

    static boolean authorized = false;

    static Vector<Request> history = new Vector<>();

    //Приложение
    static WeakReference<AppCompatActivity> currentActivity;    // Текущая активность (страничка)
    static AppActivity currentPage;                             // Текущая активность (номер странички)
    static ServerTasks serverTasks = new ServerTasks();         // см. класс ServerTasks
    static SharedPreferences app_settings;                      // Файл с настройками приложения

    // Функция для навигации - сменяет страницы
    static void navigation(int id){
        switch (id){
            case R.id.nav_calculator: // Калькулятор
                AppBase.changeActivity(CalculateActivity.class);
                break;

            case R.id.nav_settings: // Настройки
                AppBase.changeActivity(SettingsActivity.class);
                break;

            case R.id.nav_info: // Информация
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

    static void stopServerTasks(){
        if (serverTasks.running()){
            serverTasks.stop();
        }
    }

    // Запрос
    static class Request {
        double deposit;
        double percents;
        Date[] period;
        byte capitalization;
        double result;

        Request(double deposit, double percents, Date[] period, byte capitalization, double result){
            this.deposit = deposit;
            this.percents = percents;
            this.period = period;
            this.capitalization = capitalization;
            this.result = result;
        }

        @NonNull
        public String toString(){
            return "Вклад: " + deposit
                    + "\n" + "Проценты: " + percents
                    + "\n" + "Период: " + period[0].toString() + " - " + period[1].toString()
                    + "\n" + "Капитализация " + CalculateActivity.listCapitalization[capitalization]
                    + "\n" + "Результат: " + result;
        }
    }

    static class Date{
        int year;
        byte month;
        byte day;

        Date(int year, byte month, byte day){
            this.year = year;
            this.month = month;
            this.day = day;
        }


        @NonNull @SuppressLint("DefaultLocale")
        public String toString(){
            return String.format("%d.%d.%d.", year, month, day);
        }

        Calendar toCalendar(){
            Calendar date = Calendar.getInstance();
            date.set(year, month, day);
            return date;
        }
    }
}
