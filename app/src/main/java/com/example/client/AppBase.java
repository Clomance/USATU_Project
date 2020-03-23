package com.example.client;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.ref.WeakReference;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.Vector;

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
    static ServerTasks serverTasks = new ServerTasks(); // см. класс ServerTasks

    static String login;
    static String password;

    static Vector<Request> history = new Vector<>();

    //Приложение
    static WeakReference<AppCompatActivity> currentActivity;    // Текущая активность (страничка)
    static AppActivity currentPage;                             // Текущая активность (номер странички)

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
        // Доп. поля TODO
        double result;

        Request(double deposit, double percents, Date[] period){
            this.deposit = deposit;
            this.percents = percents;
            this.period = period;
        }

        Request(double deposit, double percents, Date[] period, double result){
            this.deposit = deposit;
            this.percents = percents;
            this.period = period;
            this.result = result;
        }

        @NonNull
        public String toString(){
            return "Вклад: " + deposit + "\n" + "Проценты: " + percents;
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
        Calendar toCalendar(){
            Calendar date = Calendar.getInstance();
            date.set(year, month, day);
            return date;
        }
    }
}
