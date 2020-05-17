package com.example.client;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.SeekBar;
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
import java.util.Locale;
import java.util.Objects;

import static com.example.client.AppBase.Date;
import static java.lang.String.format;

enum Limits{
    No,
    EarlyClose,
    Exemption,
}

public class CalculateActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, SeekBar.OnSeekBarChangeListener {
    // Для расчёта
    static Double deposit = null; // Размер вклада
    static double percents = 0.0; // Размер процентной ставки
    static Date[] period = new Date[2]; // Период
    static Byte capitalization = 0; // Индекс капитализации
    static Byte currency = 0; // Индекс валюты
    static Double result = null; // Расчётов

    // Для вывода
    TextView dateResultView; // Поле для результата расчёта периода
    TextView depositView;
    SeekBar percentsSeekBar;
    TextView resultView;
    TextView BarView;

    static String textHead = ""; // Вставка перед значением в поле ввода
    static String numStr = ""; // Размер вклада в виде строки (для упрощения красивого вывода и расчёта)
    static String textEnd = ""; // Вставка после значения в поле ввода
    static boolean comma = false;
    static Long dateResult = null;

    static int maxPercents = 2000;
    static Limits limits = Limits.No;

    @Override // Создание страницы
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calculate_activity); // Подключение нужного интерфеса

        NavigationView navigationView = findViewById(R.id.calculate_nav_view);  // Подключение навигационной
        navigationView.setNavigationItemSelectedListener(this);                 // панели

        dateResultView = findViewById(R.id.data_result);

        resultView = findViewById(R.id.resultView);

        textHead = getString(R.string.headDeposit); // Загрузка строки из ресурсов
        depositView = findViewById(R.id.enterDeposit);

        String text = textHead + numStr + textEnd;
        depositView.setText(text); // Вывод в поле

        // Слайдер процентов
        percentsSeekBar = findViewById(R.id.enterPercentsBar);
        BarView = findViewById(R.id.BarView);
    }

    @Override // Действия при старте активности
    public void onStart(){
        AppBase.currentActivity = new WeakReference<AppCompatActivity>(this);   // Установка текущей
        AppBase.currentPage = AppActivity.Calculate;                                    // активности
        super.onStart();




        // Установка процентной ставка
        double pr = percents * 100.0;
        int progress = (int) pr;
        percentsSeekBar.setProgress(progress);
        percentsSeekBar.setMax(maxPercents);
        percentsSeekBar.setOnSeekBarChangeListener(this);

        String percents_text = "Процентная ставка: " + percents;
        BarView.setText(percents_text);

        switch (limits){
            case No: break;
            case EarlyClose:
                CheckBox b1 = findViewById(R.id.earlyClose);
                b1.setChecked(true);
                break;
            case Exemption:
                CheckBox b2 = findViewById(R.id.exemption);
                b2.setChecked(true);
                break;
        }

        // Вписывание в поля старых данных, если есть

            TextView currencyView = findViewById(R.id.listViewCurrency);
            String text1 = "Валюта\n" + AppBase.listCurrency[currency];
            currencyView.setText(text1);



            TextView capitalizationView = findViewById(R.id.listViewCapitalization);
            String text0 = getString(R.string.capitalization) + "\n" + AppBase.listCapitalization[capitalization];
            capitalizationView.setText(text0);


        if (period[0] != null){
            String text = format(Locale.getDefault(),"%d.%d.%d", period[0].day, period[0].month + 1, period[0].year);
            TextView dateView1 =  findViewById(R.id.date1);
            dateView1.setText(text);

            if (period[1] != null){
                text = format(Locale.getDefault(),"%d.%d.%d", period[1].day, period[1].month + 1, period[1].year);
                TextView dateView2 =  findViewById(R.id.date2);
                dateView2.setText(text);

                Calendar data1c = period[0].toCalendar();   // Перевод дат
                Calendar data2c = period[1].toCalendar();   // в календарь

                if (data1c.before(data2c)){
                    text = dateResult + " дней";
                    dateResultView.setText(text);
                }
                else{
                    dateResultView.setText("Ошибка");
                }
            }
        }
        else{
            if (period[1] != null){
                String text = format(Locale.getDefault(),"%d.%d.%d", period[1].day, period[1].month + 1, period[1].year);
                TextView dateView2 =  findViewById(R.id.date2);
                dateView2.setText(text);
            }
        }
    }

    @Override // Закрытие страницы
    public void onDestroy(){
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

            String text = format(Locale.getDefault(),"%d.%d.%d", dayOfMonth, month + 1, year);   // Вывод даты
            selectedView.setText(text);                                                                         // в нужное поле

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
                    long days = result / (24 * 60 * 60 * 1000);         //
                    dateResult = days;                                  // Подсчёт
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

            builder.setTitle("Валюты").setItems(AppBase.listCurrency, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // При выборе из списка
                    String text = "Валюта\n" + AppBase.listCurrency[which];
                    selectedView.setText(text);

                    currency = (byte) which;

                    textEnd = AppBase.shortList[currency];
                    if (!numStr.isEmpty()){
                        text = textHead + numStr + textEnd;
                        depositView.setText(text);
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

        switch (id) {
            case R.id.button0: // Ввод цифр и точки
                numStr += "0";
                String text0 = textHead + numStr + textEnd;
                depositView.setText(text0); // Вывод в поле
                break;
            case R.id.button1:
                numStr += "1";
                String text1 = textHead + numStr + textEnd;
                depositView.setText(text1); // Вывод в поле
                break;
            case R.id.button2:
                numStr += "2";
                String text2 = textHead + numStr + textEnd;
                depositView.setText(text2); // Вывод в поле
                break;
            case R.id.button3:
                numStr += "3";
                String text3 = textHead + numStr + textEnd;
                depositView.setText(text3); // Вывод в поле
                break;
            case R.id.button4:
                numStr += "4";
                String text4 = textHead + numStr + textEnd;
                depositView.setText(text4); // Вывод в поле
                break;
            case R.id.button5:
                numStr += "5";
                String text5 = textHead + numStr + textEnd;
                depositView.setText(text5); // Вывод в поле
                break;
            case R.id.button6:
                numStr += "6";
                String text6 = textHead + numStr + textEnd;
                depositView.setText(text6); // Вывод в поле
                break;
            case R.id.button7:
                numStr += "7";
                String text7 = textHead + numStr + textEnd;
                depositView.setText(text7); // Вывод в поле
                break;
            case R.id.button8:
                numStr += "8";
                String text8 = textHead + numStr + textEnd;
                depositView.setText(text8); // Вывод в поле
                break;
            case R.id.button9:
                numStr += "9";
                String text9 = textHead + numStr + textEnd;
                depositView.setText(text9); // Вывод в поле
                break;
            case R.id.button10: // Запятая
                if (!comma){
                    if (numStr.isEmpty()){
                        numStr = "0";
                    }
                    comma = true;
                    numStr += ".";
                }
                String text = textHead + numStr + textEnd;
                depositView.setText(text); // Вывод в поле
                break;

            case R.id.clear_button: // Кнопка отчиски всех поле
                depositView.setText("");
                comma = false;
                numStr = "";
                resultView.setText("");
                break;

            case R.id.delete_button: // Кнопка удаления одного символа
                int len = numStr.length();

                if (len < 2){                   // Удаление всего содержимого поля,
                    numStr = "";                // если оно пустое
                    // или
                    depositView.setText("");       // содержит один
                    // символ числа
                }                               // размера вклада
                else{
                    String ch = numStr.substring(len - 1, len);
                    if (ch.compareTo(".") == 0){
                        comma = false;
                    }
                    numStr = numStr.substring(0, len - 1); // Удаление одного символа

                    String text_ = textHead + numStr + textEnd;
                    depositView.setText(text_); // Вывод в поле
                }
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

                if (numStr.isEmpty()){
                    Toast.makeText(this, "Введите размер вклада", Toast.LENGTH_LONG).show();
                    break;
                }
                else{
                    CalculateActivity.deposit = Double.parseDouble(numStr); // Перевод депозита в число
                }

                AppBase.serverTasks.start(Task.Calculate); // Запуск потока для отправки данных и получения расчёта

                break;
            default:
                break;
        }
    }

    public void EarlyClose(View b) {
        CheckBox button = (CheckBox) b;

        if (button.isChecked()) {
            if (limits == Limits.No) {
                limits = Limits.EarlyClose;
                maxPercents = 500;
            }
            else {
                button.setChecked(false);
                Toast.makeText(this,"Есть другие ограничения",Toast.LENGTH_LONG).show();
            }
        }
        else{
            limits = Limits.No;
            maxPercents = 2000;
        }

        percentsSeekBar.setMax(maxPercents);
    }

    public void exemption(View b) {
        CheckBox button = (CheckBox) b;
        if (button.isChecked()) {
            if (limits == Limits.No) {
                limits = Limits.Exemption;
                maxPercents = 100;
            }
            else {
                button.setChecked(false);
                Toast.makeText(this,"Есть другие ограничения",Toast.LENGTH_LONG).show();
            }
        }
        else{
            limits = Limits.No;
            maxPercents = 2000;
        }

        percentsSeekBar.setMax(maxPercents);
    }

    @Override // При сдвиге кружочка слайдера
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        percents = ((double) i) / 100.0;
        String text = "Процентная ставка: " + percents;
        BarView.setText(text);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

}