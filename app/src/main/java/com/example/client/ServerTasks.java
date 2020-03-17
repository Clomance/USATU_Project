package com.example.client;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

enum Task{
    Sign_in,
    Sign_up,
    Calculate,
}

// Класс для связи и обмена данными с сервером
class ServerTasks{
    private static Task task; // Задача для выполнения
    private static ServerTask serverTask; // Поток с выполняемой задачей
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

        @Override
        protected Boolean doInBackground(Object... objects) {
            try{
                Socket socket = new Socket(AppBase.serverIp, AppBase.serverPort); // Подключение к серверу
                socket.setSoTimeout(1000); // Время ожидания ответа от сервера

                ClientServerChannel channel = new ClientServerChannel(socket); // См. класс ниже

                switch (task){
                    case Sign_in:
                        channel.writeByte( 0);              //
                        channel.writeString(AppBase.login);     // Отправка
                        channel.writeString(AppBase.password);  // данных
                        channel.flush();                        //

                        if (channel.readBoolean()){ // Успешный вход
                            for (int i = 0; i < tokens.length; i++){
                                tokens[i] = channel.readInt();
                            }

                        }
                        else{ // Неверный логин или пароль
                            return false;
                        }

                        break;

                    case Sign_up:

                        break;
                    case Calculate:
                        for (int token: tokens){
                            channel.writeInt(token);
                        }
                        channel.flush();
                        break;
                }
            }
            catch (Exception e){
                return false;
            }
            return true;
        }

        @Override
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

        void writeAll(byte[] bytes) throws IOException{
            output.write(bytes);
        }

        void writeInt(int Int) throws IOException{
            output.writeInt(Int);
        }

        void writeString(String str) throws IOException{
            output.writeByte(str.length());
            output.writeBytes(str);
        }

        void flush() throws IOException{
            output.flush();
        }

        void readExact(byte[] bytes) throws IOException{
            input.readFully(bytes);
        }

        Boolean readBoolean() throws IOException{
            return input.readBoolean();
        }

        int readInt() throws IOException{
            return input.readInt();
        }
    }
}
