package com.example.client;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

enum Task{
    Sign_in,
    Sign_up,
    Calculate
}

// Класс для связи и обмена данными с сервером
class ServerTasks{
    private static Task task = Task.Sign_in; // Задача для выполнения
    private static ServerTask serverTask = new ServerTask(); // Поток с выполняемой задачей
    private static int[] tokens = new int[2]; // Токены для быстрой авторизации

    // Запуск выполнения задачи
    void start(Task server_task, Object... objects){
        task = server_task;
        serverTask = new ServerTask();
        serverTask.execute(objects);
    }

    // Остановка задачи
    void stop(){
        serverTask.cancel(true);
    }

    // Статус задачи: выполняется - true
    boolean running(){
        return serverTask.getStatus() != AsyncTask.Status.FINISHED;
    }

    private static class ServerTask extends AsyncTask<Object,Void,Boolean>{

        @Override // Действия в дополнительном потоке
        protected Boolean doInBackground(Object... objects) {
            try{
                Socket socket = new Socket(AppBase.serverIp, AppBase.serverPort); // Подключение к серверу
                socket.setSoTimeout(1000); // Время ожидания ответа от сервера

                ClientServerChannel channel = new ClientServerChannel(socket); // См. класс ниже

                if (task == Task.Calculate){
                    channel.writeByte( 2); // Отправка номера задачи

                    for (int token: tokens){        //
                        channel.writeInt(token);    // Отправка токенов
                    }
                    channel.writeRequest(); // Отправка данных для ресчёта

                    channel.flush();

                    // Получение ответа
                    if (channel.readBoolean()){
                        CalculateActivity.result = channel.readDouble();
                    }
                    else{
                        return false;
                    }
                }
                else{
                    if (task == Task.Sign_in){
                        channel.writeByte( 0); // Отправка номера задачи
                    }
                    else{
                        channel.writeByte( 1); // Отправка номера задачи
                    }

                    channel.writeString(AppBase.login);     // Отправка
                    channel.writeString(AppBase.password);  // данных
                    channel.flush();                        //

                    // Получение ответа
                    if (channel.readBoolean()){
                        // Успех
                        for (int i = 0; i < tokens.length; i++){    //
                            tokens[i] = channel.readInt();          // Получение токенов
                        }                                           //

                        channel.readHistory();
                    }
                    else{
                        // Ошибка
                        return false;
                    }
                }
            }
            catch (Exception e){
                return false;
            }
            return true;
        }

        @Override // Действия после выполения задачи (в текущем потоке)
        protected void onPostExecute(Boolean state) {
            super.onPostExecute(state);
            switch (task) {
                case Sign_in:
                case Sign_up:
                    StartActivity startActivity = (StartActivity) AppBase.currentActivity.get();
                    if (state) {
                        startActivity.startMainActivity();
                    }
                    else {
                        startActivity.setButtonsEnabled(true);
                        Toast.makeText(startActivity, "Ошибка", Toast.LENGTH_LONG).show();
                    }
                    break;
                case Calculate:
                    CalculateActivity calculateActivity = (CalculateActivity) AppBase.currentActivity.get();
                    if (state){
                        // Вывод результата TODO
                    }
                    else{
                        Toast.makeText(calculateActivity, "Ошибка", Toast.LENGTH_LONG).show();
                    }
                    break;
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

        void writeRequest() throws IOException{
            output.writeDouble(CalculateActivity.deposit);
            // TODO
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
            int month = input.readInt();
            int day = input.readInt();
            return new AppBase.Date(year, month, day);
        }

        AppBase.Request readRequest() throws  IOException{
            double deposit = input.readDouble();
            double percents = input.readDouble();
            AppBase.Date[] period = new AppBase.Date[2];

            period[0] = readDate();
            period[1] = readDate();

            double result = readDouble();

            return  new AppBase.Request(deposit, percents, period, result);

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
