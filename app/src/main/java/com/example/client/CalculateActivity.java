package com.example.client;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.navigation.NavigationView;

import java.lang.ref.WeakReference;
import java.util.Calendar;
import java.util.Objects;

import static com.example.client.AppBase.Date;
import static java.lang.String.format;

public class CalculateActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    // Постоянные
    final static String[] listCurrency = new String[]{"Рубль","Доллар","Евро"}; // Полные валюты для выбора
    final static String[] shortList = new String[]{" руб.", " долл.", " евро"}; // Краткие валюты для ввода
    final static String[] listCapitalization = new String[]{"Отсутствует","Ежемесячная","Ежеквартальная"}; // Вид капитализации

    // Для расчёта
    static Double deposit = null; // Размер вклада
    static Double percents = null; // Размер процентной ставки
    static Date[] period = new Date[2]; // Период
    static Byte capitalization = null; // Индекс капитализации
    static Integer currency = null; // Индекс валюты
    static Double result = null; // Расчётов

    // Для вывода
    TextView dateResultView; // Поле для результата расчёта периода
    TextView depositView;
    TextView percentsView;
    TextView resultView;
    TextView enterTextView; // Поле ввода (выбираемое)
    static boolean enterPercents = false; // Флаг ввода (Ввод процентов - true, ввода вклада - false)

    static String headEnterText; // Вставка перед значением в поле ввода
    static String endEnterText; // Вставка после значения в поле ввода

    static String depositNumStr = ""; // Размер вклада в виде строки (для упрощения красивого вывода и расчёта)
    static String percentsNumStr = ""; // Размер процентной ставки в виде строки (для упрощения красивого вывода и расчёта)
    static String NumStr = ""; // Временное поле для depositNumStr или percentsNumStr

    @SuppressLint("DefaultLocale")
    @Override // Создание страницы
    protected void onCreate(Bundle savedInstanceState) {
        AppBase.currentActivity = new WeakReference<AppCompatActivity>(this);   // Установка текущей
        AppBase.currentPage = AppActivity.Calculate;                                    // активности

        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate_activity); // Подключение нужного интерфеса

        NavigationView navigationView = findViewById(R.id.calculate_nav_view);  // Подключение навигационной
        navigationView.setNavigationItemSelectedListener(this);                 // панели

        dateResultView = findViewById(R.id.data_result);

        depositView = findViewById(R.id.enterDeposit);
        percentsView = findViewById(R.id.enterPercents);
        resultView = findViewById(R.id.resultView);

        headEnterText = getString(R.string.headDeposit); // Загрузка строки из ресурсов

        enterTextView = depositView;

        // Вписывание в поля старых данных, если есть
        if (currency == null) {
            endEnterText = "";
        }
        else{
            endEnterText = shortList[currency];
            TextView currencyView = findViewById(R.id.listViewCurrency);
            String text = "Валюта\n" + listCurrency[currency];
            currencyView.setText(text);
        }

        if (!depositNumStr.isEmpty()){
            NumStr = depositNumStr;
            String text = headEnterText + depositNumStr + endEnterText;
            depositView.setText(text);
        }

        if (!percentsNumStr.isEmpty()){
            String text = getString(R.string.headPercents) + percentsNumStr + " %";
            percentsView.setText(text);
        }

        if (capitalization != null){
            TextView capitalizationView = findViewById(R.id.listViewCapitalization);
            String text = getString(R.string.capitalization) + "\n" + listCapitalization[capitalization];
            capitalizationView.setText(text);
        }

        if (period[0] != null){
            @SuppressLint("DefaultLocale")
            String text = format("%d.%d.%d", period[0].day, period[0].month + 1, period[0].year);
            TextView dateView1 =  findViewById(R.id.date1);
            dateView1.setText(text);

            if (period[1] != null){
                text = format("%d.%d.%d", period[1].day, period[1].month + 1, period[1].year);
                TextView dateView2 =  findViewById(R.id.date2);
                dateView2.setText(text);

                Calendar data1c = period[0].toCalendar();   // Перевод дат
                Calendar data2c = period[1].toCalendar();   // в календарь

                if (data1c.before(data2c)){                             //
                    long data1_millis = data1c.getTimeInMillis();       //
                    long data2_millis = data2c.getTimeInMillis();       //
                    long result = data2_millis - data1_millis;          //
                    long days = result / (24 * 60 * 60 * 1000);         // Подсчёт
                    text = days + " дней";                              // дней
                    dateResultView.setText(text);                           // между
                }                                                       // введёнными
                else{                                                   // датами
                    dateResultView.setText("Ошибка");                       //
                }                                                       //
            }
        }
        else{
            if (period[1] != null){
                @SuppressLint("DefaultLocale")
                String text = format("%d.%d.%d", period[1].day, period[1].month + 1, period[1].year);
                TextView dateView2 =  findViewById(R.id.date2);
                dateView2.setText(text);
            }
        }
    }

    @Override // Закрытие страницы
    public void onDestroy(){
        AppBase.stopServerTasks();
        if (enterPercents){
            percentsNumStr = NumStr;
        }
        else{
            depositNumStr = NumStr;
        }
        super.onDestroy();
    }

    @Override // Действия при выборе на панели навигации
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();  // Переход к выбранной
        AppBase.navigation(id);     // активности

        DrawerLayout drawer = findViewById(R.id.calculate_drawer_layout);   // Закрытие боковой
        drawer.closeDrawer(GravityCompat.START);                            // панели

        return true;
    }

    // T T T T  И     И   М     М     У     У   Р Р Р
    //    T     И   И И   М М М М      У  У     Р    Р
    //    T     И  И  И   М  М  М       У       Р Р Р
    //    T     И И   И   М     М      У        Р
    //    T     И     И   М     М     У         Р

    // Вывод диалогового окна с календарём
    public void dataPick(View view){
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.selectedView = (TextView) view;
        newFragment.resultView = dateResultView;
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    // Класс для диалога с датой
    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        TextView selectedView;
        TextView resultView;
        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Установка даты в диалоговое окно
            if (selectedView.getId() == R.id.date1){
                if (period[0] != null){
                    // Установка даты, введённой в первое поле
                    return new DatePickerDialog(Objects.requireNonNull(getActivity()), this, period[0].year, period[0].month, period[0].day);
                }
                else{
                    final Calendar c = Calendar.getInstance();  // Получение текущей даты
                    int year = c.get(Calendar.YEAR);            // Установка
                    int month = c.get(Calendar.MONTH);          // текущей
                    int day = c.get(Calendar.DAY_OF_MONTH);     // даты
                    return new DatePickerDialog(Objects.requireNonNull(getActivity()), this, year, month, day);
                }
            }
            else{
                if (period[1] != null){
                    // Установка даты, введённой во второе поле
                    return new DatePickerDialog(Objects.requireNonNull(getActivity()), this, period[1].year, period[1].month, period[1].day);
                }
                else{
                    final Calendar c = Calendar.getInstance();  // Получение текущей даты
                    int year = c.get(Calendar.YEAR);            // Установка
                    int month = c.get(Calendar.MONTH);          // текущей
                    int day = c.get(Calendar.DAY_OF_MONTH);     // даты
                    return new DatePickerDialog(Objects.requireNonNull(getActivity()), this, year, month, day);
                }
            }
        }

        @Override // Действия при установке даты
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

            @SuppressLint("DefaultLocale") // Атрибут, чтобы не ругался на формат
            String text = format("%d.%d.%d", dayOfMonth, month + 1, year);   // Вывод даты
            selectedView.setText(text);                                      // в нужное поле

            if (selectedView.getId() == R.id.date1){                            //
                period[0] = new Date(year, (byte) month, (byte) dayOfMonth);    // Сохранение
            }                                                                   // введённой
            else{                                                               // даты
                period[1] = new Date(year, (byte) month, (byte) dayOfMonth);    //
            }                                                                   //

            if (period[0] != null && period[1] != null){
                Calendar data1c = period[0].toCalendar();   // Перевод дат
                Calendar data2c = period[1].toCalendar();   // в календарь

                if (data1c.before(data2c)){                             //
                    long data1_millis = data1c.getTimeInMillis();       //
                    long data2_millis = data2c.getTimeInMillis();       //
                    long result = data2_millis - data1_millis;          //
                    long days = result / (24 * 60 * 60 * 1000);         // Подсчёт
                    text = days + " дней";                              // дней
                    resultView.setText(text);                           // между
                }                                                       // введёнными
                else{                                                   // датами
                    resultView.setText("Ошибка");                       //
                }                                                       //
            }                                                           //
            else{                                                       //
                resultView.setText("");                                 //
            }                                                           //
        }
    }

    // Вывод диалогового списка для выбора валюты
    public void listPickCurrency(View view){
        PickCurrency newFragment = new PickCurrency();
        newFragment.selectedView = (TextView) view;
        newFragment.depositView = depositView;
        newFragment.show(getSupportFragmentManager(), "listPicker");

    }
    // Класс диалогового списка для выбора вылюты
    public static class PickCurrency extends DialogFragment{
        TextView depositView;
        TextView selectedView;

        @NonNull@Override // Создание диалогового окна
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

            builder.setTitle("Валюты").setItems(listCurrency, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // При выборе из списка
                    String text = "Валюта\n" + listCurrency[which]; // Вставка валюты
                    selectedView.setText(text);                     // в нажатое поле

                    currency = which;
                    // Сложные манипуляции для корректного смены и отображения валюты в поле ввода
                    if (enterPercents){
                        if (!depositNumStr.isEmpty()){
                            text = headEnterText + depositNumStr + shortList[currency];
                            depositView.setText(text);
                        }
                    }
                    else{
                        if (NumStr.isEmpty()){
                            endEnterText = shortList[currency];
                        }
                        else{
                            endEnterText = shortList[currency];
                            text = headEnterText + NumStr + endEnterText;
                            depositView.setText(text);
                        }
                    }
                }
            });
            return builder.create();
        }
    }
    // Вывод диалогового списка для выбора типа капитилизаций
    public void listPickCapitalization(View view){
        PickCapitalization newFragment = new PickCapitalization();
        newFragment.selectedView = (TextView) view;
        newFragment.show(getSupportFragmentManager(), "listPicker");
    }
    // Класс диалогового списка для выбора типа капиталтзации
    public static class PickCapitalization extends DialogFragment{
        TextView selectedView;

        @NonNull@Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

            builder.setTitle(R.string.capitalization).setItems(listCapitalization, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Выбор из списка
                    capitalization = (byte) which;
                    String text = getString(R.string.capitalization) + "\n" + listCapitalization[which];

                    selectedView.setText(text);
                }
            });
            return builder.create();
        }
    }

    // Действия при нажатии кнопок
    public void onButtonsClick(View button){
        int id = button.getId();
        ImageButton clickedButton = (ImageButton) button;
        button.setClickable(false); // Включение/выключение кнопок (для кнопки расчёта, чтобы случайно запустить оправку данных ещё раз)
        String buttonText;

        switch (id) {
            case R.id.button0: // Ввод цифр и точки
                buttonText="0";
                writeToTextView(buttonText);
                break;
            case R.id.button1:
                buttonText="1";
                writeToTextView(buttonText);
                break;
            case R.id.button2:
                buttonText="2";
                writeToTextView(buttonText);
                break;
            case R.id.button3:
                buttonText="3";
                writeToTextView(buttonText);
                break;
            case R.id.button4:
                buttonText="4";
                writeToTextView(buttonText);
                break;
            case R.id.button5:
                buttonText="5";
                writeToTextView(buttonText);
                break;
            case R.id.button6:
                buttonText="6";
                writeToTextView(buttonText);
                break;
            case R.id.button7:
                buttonText="7";
                writeToTextView(buttonText);
                break;
            case R.id.button8:
                buttonText="8";
                writeToTextView(buttonText);
                break;
            case R.id.button9:
                buttonText="9";
                writeToTextView(buttonText);
                break;
            case R.id.button10:
                buttonText=".";
                writeToTextView(buttonText);
                break;

            case R.id.clear_button: // Кнопка отчиски всего поля
                NumStr = "";
                resultView.setText("");
                enterTextView.setText("");
                break;

            case R.id.delete_button: // Кнопка удаления одного символа

                int len = NumStr.length();

                if (len < 2){                   // Удаление всего содержимого поля,
                    NumStr = "";                // если оно пустое
                    resultView.setText("");     // или
                    enterTextView.setText("");  // содержит один
                    break;                      // символ числа
                }                               // размера вклада
                else{
                    NumStr = NumStr.substring(0, len - 1); // Удаление одного символа
                }

                String text = headEnterText + NumStr + endEnterText; // Форматирование
                enterTextView.setText(text); // Вывод в поле

                break;

            case R.id.compute_button: // Кнопка расчёта
                if (!AppBase.authorized){
                    Toast.makeText(this, "Вы не авторизованы", Toast.LENGTH_LONG).show();
                    break;
                }
                if (capitalization == null){
                    Toast.makeText(this, "Выбирите тип капитализации", Toast.LENGTH_LONG).show();
                    break;
                }

                if (period[0] == null || period[1] == null){
                    Toast.makeText(this, "Введите период", Toast.LENGTH_LONG).show();
                    break;
                }

                if (enterPercents){             //
                    percentsNumStr = NumStr;    //
                }                               // Сохранение введённых
                else{                           // значений
                    depositNumStr = NumStr;     //
                }                               //

                try {
                    CalculateActivity.deposit = Double.parseDouble(depositNumStr); // Перевод в число
                    CalculateActivity.percents = Double.parseDouble(percentsNumStr); // Перевод в число
                }
                catch (Exception e){
                    Toast.makeText(this, "Ошибка - не число", Toast.LENGTH_LONG).show();
                    break;
                }

                AppBase.serverTasks.start(Task.Calculate); // Запуск потока для отправки данных и получения расчёта

                break;

            case R.id.percents_button: // Кнопка переключения ввода
                if (enterPercents){
                    percentsNumStr = NumStr; // Сохранение ввода в проценты

                    enterTextView = depositView;    // Переключение ввода
                    NumStr = depositNumStr;         // во вклад

                    headEnterText = getString(R.string.headDeposit); // Получение строки из ресурсов
                    if (currency == null){
                        endEnterText = "";
                    }
                    else{
                        endEnterText = shortList[currency];
                    }
                    clickedButton.setImageResource(R.drawable.procent_select);
                }
                else{
                    depositNumStr = NumStr; // Сохранение ввода во вклад

                    enterTextView = percentsView;   // Перелючение ввода
                    NumStr = percentsNumStr;        // в проценты

                    headEnterText = getString(R.string.headPercents); // Получение строки из ресурсов
                    endEnterText = " %";
                    clickedButton.setImageResource(R.drawable.procent_press);
                }
                enterPercents = !enterPercents;
                break;
            default:
                break;
        }
        button.setClickable(true); // включение кнопки
    }

    void writeToTextView(String str){
        NumStr += str;
        String enterText = headEnterText + NumStr + endEnterText; // Форматирование
        enterTextView.setText(enterText);
    }
}