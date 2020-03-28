package com.example.client;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

// Задачи для выполнения сервером
enum Task{
    Sign_in,
    Sign_up,
    Calculate
}

// Результат задач
enum TaskResult{
    Ok,
    ConnectionError,
    WrongLoginPassword,
    LoginAlreadyExists,
    WrongToken,
}

// Класс для связи и обмена данными с сервером
class ServerTasks{
    private static Task task = Task.Sign_in; // Задача для выполнения
    private static ServerTask serverTask = new ServerTask(); // Поток с выполняемой задачей
    private static int[] tokens = new int[2]; // Токены для быстрой авторизации

    // Запуск выполнения задачи
    void start(Task server_task){
        task = server_task;
        serverTask = new ServerTask();
        serverTask.execute();
    }

    // Остановка задачи
    void stop(){
        serverTask.cancel(true);
    }

    // Статус задачи: выполняется - true
    boolean running(){
        return serverTask.getStatus() != AsyncTask.Status.FINISHED;
    }

    private static class ServerTask extends AsyncTask<Void, Void, TaskResult>{

        @Override // Действия в дополнительном потоке
        protected TaskResult doInBackground(Void... objects) {
            try{
                Socket socket = new Socket(AppBase.serverIp, AppBase.serverPort); // Подключение к серверу
                socket.setSoTimeout(1000); // Время ожидания ответа от сервера

                ClientServerChannel channel = new ClientServerChannel(socket); // См. класс ниже

                switch (task){
                    case Sign_in:
                        channel.writeByte( 0); // Отправка номера задачи

                        channel.writeString(AppBase.login);     // Отправка
                        channel.writeString(AppBase.password);  // данных
                        channel.flush();                        //

                        // Получение ответа
                        if (channel.readBoolean()){
                            // Успех
                            for (int i = 0; i < tokens.length; i++){    //
                                tokens[i] = channel.readInt();          // Получение токенов
                            }                                           //

                            channel.readHistory(); // Получение истории
                        }
                        else{
                            // Ошибка
                            return TaskResult.WrongLoginPassword;
                        }
                        break;

                    case Sign_up:

                        channel.writeByte( 1); // Отправка номера задачи

                        channel.writeString(AppBase.login);     // Отправка
                        channel.writeString(AppBase.password);  // данных
                        channel.flush();                        //

                        // Получение ответа
                        if (channel.readBoolean()){
                            // Успех
                            for (int i = 0; i < tokens.length; i++){    //
                                tokens[i] = channel.readInt();          // Получение токенов
                            }                                           //
                        }
                        else{
                            // Ошибка
                            return TaskResult.LoginAlreadyExists;
                        }
                        break;

                    case Calculate:
                        channel.writeByte( 2); // Отправка номера задачи

                        for (int token: tokens){        //
                            channel.writeInt(token);    // Отправка токенов
                        }

                        channel.writeRequest(); // Отправка данных для ресчёта

                        channel.flush();

                        // Получение ответа
                        if (channel.readBoolean()){
                            CalculateActivity.result = channel.readDouble(); // Получение результата

                            AppBase.history.add(new AppBase.Request(CalculateActivity.deposit,
                                    CalculateActivity.percents, CalculateActivity.period,
                                    CalculateActivity.capitalization, CalculateActivity.result)); // Добавиление в историю
                        }
                        else{
                            return TaskResult.WrongToken;
                        }
                        break;
                }
            }
            catch (IOException e){
                return TaskResult.ConnectionError;
            }
            return TaskResult.Ok;
        }

        @Override // Действия после выполения задачи (в основном потоке)
        protected void onPostExecute(TaskResult result) {
            super.onPostExecute(result);
            switch (task) {
                case Sign_in:
                case Sign_up:
                    StartActivity startActivity = (StartActivity) AppBase.currentActivity.get();
                    switch (result){
                        case Ok:
                            startActivity.startMainActivity();
                            break;
                        case ConnectionError:
                            Toast.makeText(startActivity, "Проблемы с соединением", Toast.LENGTH_LONG).show();
                            break;
                        case WrongLoginPassword:
                            Toast.makeText(startActivity, "Неправильный логин или пароль", Toast.LENGTH_LONG).show();
                            break;
                        case LoginAlreadyExists:
                            Toast.makeText(startActivity, "Такой логин уже существует", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
                    startActivity.setButtonsEnabled(true);
                    break;
                case Calculate:
                    CalculateActivity calculateActivity = (CalculateActivity) AppBase.currentActivity.get();
                    switch (result) {
                        case Ok:
                            String text = "Результат: " + CalculateActivity.result;
                            calculateActivity.resultView.setText(text);
                            // Результат получен TODO
                            break;
                        case ConnectionError:
                            Toast.makeText(calculateActivity, "Проблемы с соединением", Toast.LENGTH_LONG).show();
                            break;
                        case WrongToken:
                            Toast.makeText(calculateActivity, "Неверный токен - перезаупустите приложение", Toast.LENGTH_LONG).show();
                            break;
                        default:
                            break;
                    }
            }
        }
    }

    // Класс, отвечающий за передачу данных между сервером и клиентом
    static class ClientServerChannel{
        DataOutputStream output;
        DataInputStream input;

        ClientServerChannel(Socket socket) throws IOException{
            output = new DataOutputStream(socket.getOutputStream());    // Получение каналов
            input = new DataInputStream(socket.getInputStream());       // связи
        }

        void writeByte(int b) throws IOException{
            output.writeByte(b);
        }

        void writeInt(int Int) throws IOException{
            output.writeInt(Int);
        }

        void writeString(String str) throws IOException{
            output.writeByte(str.length());
            output.writeBytes(str);
        }

        void writePeriod() throws IOException{
            output.writeInt(CalculateActivity.period[0].year);
            output.writeByte(CalculateActivity.period[0].month);
            output.writeByte(CalculateActivity.period[0].day);

            output.writeInt(CalculateActivity.period[1].year);
            output.writeByte(CalculateActivity.period[1].month);
            output.writeByte(CalculateActivity.period[1].day);
        }

        void writeRequest() throws IOException{
            output.writeDouble(CalculateActivity.deposit);
            output.writeDouble(CalculateActivity.percents);
            writePeriod();
            output.writeByte(CalculateActivity.capitalization);
        }

        void flush() throws IOException{
            output.flush();
        }

        Boolean readBoolean() throws IOException{
            return input.readBoolean();
        }

        int readInt() throws IOException{
            return input.readInt();
        }

        double readDouble() throws IOException{
            return input.readDouble();
        }

        AppBase.Date readDate() throws IOException{
            int year = input.readInt();
            byte month = input.readByte();
            byte day = input.readByte();
            return new AppBase.Date(year, month, day);
        }

        AppBase.Request readRequest() throws  IOException{
            double deposit = input.readDouble();
            double percents = input.readDouble();
            AppBase.Date[] period = new AppBase.Date[2];

            period[0] = readDate();
            period[1] = readDate();

            byte capitalization = input.readByte();

            double result = readDouble();

            return  new AppBase.Request(deposit, percents, period, capitalization, result);

        }

        void readHistory() throws IOException{
            int len = readInt();
            AppBase.history = new Vector<>();
            for (int i = 0; i < len; i++){
                AppBase.history.add(readRequest());
            }
        }
    }
}
