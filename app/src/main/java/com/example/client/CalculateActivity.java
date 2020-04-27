package com.example.client;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
    // Для расчёта
    static Double deposit = null; // Размер вклада
    static Double percents = null; // Размер процентной ставки
    static Date[] period = new Date[2]; // Период
    static Byte capitalization = null; // Индекс капитализации
    static Byte currency = null; // Индекс валюты
    static Double result = null; // Расчётов

    // Для вывода
    TextView dateResultView; // Поле для результата расчёта периода
    TextField depositView;
    TextField percentsView;
    TextView resultView;
    TextField enterTextView; // Поле ввода (выбираемое)
    static boolean enterPercents = false; // Флаг ввода (Ввод процентов - true, ввода вклада - false)

    static String depositNumStr = ""; // Размер вклада в виде строки (для упрощения красивого вывода и расчёта)
    static String percentsNumStr = ""; // Размер процентной ставки в виде строки (для упрощения красивого вывода и расчёта)

    static boolean depositComma = false;
    static boolean percentsComma = false;

    @SuppressLint("DefaultLocale")
    @Override // Создание страницы
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate_activity); // Подключение нужного интерфеса

        NavigationView navigationView = findViewById(R.id.calculate_nav_view);  // Подключение навигационной
        navigationView.setNavigationItemSelectedListener(this);                 // панели

        dateResultView = findViewById(R.id.data_result);

        TextView depositTextView = findViewById(R.id.enterDeposit);
        TextView percentsTextView = findViewById(R.id.enterPercents);

        resultView = findViewById(R.id.resultView);

        String headEnterText = getString(R.string.headDeposit); // Загрузка строки из ресурсов
        depositView = new TextField(depositTextView, headEnterText, depositNumStr, depositComma);


        headEnterText = getString(R.string.headPercents);
        percentsView = new TextField(percentsTextView, headEnterText, percentsNumStr, depositComma);
        percentsView.setTextEnd("%");

        enterTextView = depositView;

        // Вписывание в поля старых данных, если есть
        if (currency != null) {
            depositView.setTextEnd(AppBase.shortList[currency]);
            TextView currencyView = findViewById(R.id.listViewCurrency);
            String text = "Валюта\n" + AppBase.listCurrency[currency];
            currencyView.setText(text);
        }

        if (capitalization != null){
            TextView capitalizationView = findViewById(R.id.listViewCapitalization);
            String text = getString(R.string.capitalization) + "\n" + AppBase.listCapitalization[capitalization];
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

    @Override // Действия при старте активности
    public void onStart(){
        AppBase.currentActivity = new WeakReference<AppCompatActivity>(this);   // Установка текущей
        AppBase.currentPage = AppActivity.Calculate;                                    // активности
        super.onStart();
    }

    @Override // Закрытие страницы
    public void onDestroy(){
        depositNumStr = depositView.getNumStr();
        percentsNumStr = percentsView.getNumStr();
        depositComma = depositView.hasComma();
        percentsComma = percentsView.hasComma();

        AppBase.stopServerTasks();
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
        TextField depositView;
        TextView selectedView;

        @NonNull@Override // Создание диалогового окна
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));

            builder.setTitle("Валюты").setItems(AppBase.listCurrency, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // При выборе из списка
                    String text = "Валюта\n" + AppBase.listCurrency[which]; // Вставка валюты
                    selectedView.setText(text);                     // в нажатое поле

                    currency = (byte) which;
                    // Сложные манипуляции для корректного смены и отображения валюты в поле ввода
                    if (enterPercents){
                        depositView.setTextEnd(AppBase.shortList[currency]);
                    }
                    else{
                        depositView.setTextEnd(AppBase.shortList[currency]);
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

            builder.setTitle(R.string.capitalization).setItems(AppBase.listCapitalization, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // Выбор из списка
                    capitalization = (byte) which;
                    String text = getString(R.string.capitalization) + "\n" + AppBase.listCapitalization[which];

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

        switch (id) {
            case R.id.button0: // Ввод цифр и точки
                enterTextView.inputText("0");
                break;
            case R.id.button1:
                enterTextView.inputText("1");
                break;
            case R.id.button2:
                enterTextView.inputText("2");
                break;
            case R.id.button3:
                enterTextView.inputText("3");
                break;
            case R.id.button4:
                enterTextView.inputText("4");
                break;
            case R.id.button5:
                enterTextView.inputText("5");
                break;
            case R.id.button6:
                enterTextView.inputText("6");
                break;
            case R.id.button7:
                enterTextView.inputText("7");
                break;
            case R.id.button8:
                enterTextView.inputText("8");
                break;
            case R.id.button9:
                enterTextView.inputText("9");
                break;
            case R.id.button10:
                enterTextView.inputText(".");
                break;

            case R.id.clear_button: // Кнопка отчиски всех полей
                percentsView.clean();
                depositView.clean();
                resultView.setText("");
                break;

            case R.id.delete_button: // Кнопка удаления одного символа
                enterTextView.delete();
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

                if (currency == null){
                    Toast.makeText(this, "Выбирите валюту", Toast.LENGTH_LONG).show();
                    break;
                }

                if (period[0] == null || period[1] == null){
                    Toast.makeText(this, "Введите период", Toast.LENGTH_LONG).show();
                    break;
                }

                try {
                    CalculateActivity.deposit = depositView.getNum(); // Перевод в число
                    CalculateActivity.percents = percentsView.getNum(); // Перевод в число
                }
                catch (Exception e){
                    Toast.makeText(this, "Ошибка - не число", Toast.LENGTH_LONG).show();
                    break;
                }

                AppBase.serverTasks.start(Task.Calculate); // Запуск потока для отправки данных и получения расчёта

                break;

            case R.id.percents_button: // Кнопка переключения ввода
                if (enterPercents){
                    enterTextView = depositView;
                    clickedButton.setImageResource(R.drawable.procent_press);
                    percentsView.setTextColor(Color.argb(100,130,130,130));
                    depositView.setTextColor(Color.rgb(0,0,0));
                    depositView.setFontSize(30);
                    percentsView.setFontSize(20);
                }
                else{
                    enterTextView = percentsView;   // Перелючение ввода
                    clickedButton.setImageResource(R.drawable.procent_select);
                    depositView.setTextColor(Color.argb(100, 130,130,130));
                    percentsView.setTextColor(Color.rgb(0, 0, 0  ));
                    percentsView.setFontSize(30);
                    depositView.setFontSize(20);
                }
                enterPercents = !enterPercents;

                break;
            default:
                break;
        }
        button.setClickable(true); // включение кнопки
    }
    public void onTextViewClick(View TV){ // Функция выделения выбранного поля ввода
        int id=TV.getId();
        if (R.id.enterPercents==id){
            enterPercents = true;
            depositView.setTextColor(Color.argb(100, 130,130,130)); // Выделение неиспользуемого поля серым цветом
            percentsView.setTextColor(Color.rgb(0, 0, 0  )); // Выделяем используемого поля чёрным
            percentsView.setFontSize(30);
            depositView.setFontSize(20);
            enterTextView = percentsView;
            ImageButton clickedButton=findViewById(R.id.percents_button);
            clickedButton.setImageResource(R.drawable.procent_select);
        }
        else{
            enterPercents = false;
            percentsView.setTextColor(Color.argb(100,130,130,130)); // Выделение неиспользуемого поля серым цветом
            depositView.setTextColor(Color.rgb(0,0,0)); // Выделяем используемого поля чёрным
            depositView.setFontSize(30);
            percentsView.setFontSize(20);
            enterTextView = depositView;
            ImageButton clickedButton=findViewById(R.id.percents_button);
            clickedButton.setImageResource(R.drawable.procent_press);
        }

    }
}