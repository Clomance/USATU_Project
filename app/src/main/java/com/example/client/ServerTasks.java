package com.example.client;

import android.os.AsyncTask;
import android.widget.Toast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

enum Task{
    Sign_in,
    Sign_up,
}

// Класс для связи и обмена данными с сервером
public class ServerTasks{
    private static Task task;
    private static ServerTask serverTask;

    public void start(Task server_task, Object... objects){
        task = server_task;
        serverTask = new ServerTask();
        serverTask.execute(objects);
    }

    public void stop(){
        serverTask.cancel(true);
    }

    boolean running(){
        return serverTask.getStatus() != AsyncTask.Status.FINISHED;
    }

    private static class ServerTask extends AsyncTask<Object,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Object... objects) {
            try{
                Socket socket = new Socket(AppBase.serverIp, AppBase.serverPort); // Подключение к серверу
                socket.setSoTimeout(1000); // Время ожидания ответа от сервера

                OutputStream output = socket.getOutputStream(); // Получение каналов
                InputStream input = socket.getInputStream();    // связи

                switch (task){
                    case Sign_in:
                        String login_in = (String) objects[0];
                        String password_in = (String) objects[1];

                        output.write(new byte[0]);

                        output.flush();

                        byte[] bytes_in = new byte[1];
                        input.read(bytes_in,0,1);

                        break;

                    case Sign_up:
                        String login_up = (String) objects[0];
                        String password_up = (String) objects[1];

                        output.write(new byte[1]);

                        output.flush();

                        byte[] bytes_up = new byte[1];
                        input.read(bytes_up,0,1);
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
            }
        }
    }
}
