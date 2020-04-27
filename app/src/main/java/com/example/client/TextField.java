package com.example.client;

import android.widget.TextView;

class TextField {
    private TextView textView;
    private String textHead; // Вставка перед значением в поле ввода
    private String numStr; // Строка с числовым значением (для упрощения расчёта)
    private String textEnd = ""; // Вставка после значения в поле ввода
    private boolean comma;

    TextField(TextView textView, String headEnterText, String num, boolean comma){
        this.textView = textView;
        this.textHead = headEnterText;
        this.numStr = num;
        this.comma = comma;
        rewrite();
    }
    void setTextColor(int C){
        textView.setTextColor(C);
    }

    void setFontSize(int FS){
        textView.setTextSize(FS);
    }

    private void rewrite(){
        String text = textHead + numStr + textEnd;
        textView.setText(text); // Вывод в поле
    }

    void setTextEnd(String textEnd){
        this.textEnd = textEnd;
        if (!numStr.isEmpty()){
            rewrite();
        }
    }

    void inputText(String inputText){
        if (inputText.compareTo(".") == 0){
            if (!comma){
                if (numStr.isEmpty()){
                    numStr = "0";
                }
                comma = true;
                numStr += inputText;
            }
        }
        else{
            numStr += inputText;
        }
        rewrite();
    }

    void delete(){
        int len = numStr.length();

        if (len < 2){                   // Удаление всего содержимого поля,
            numStr = "";                // если оно пустое
                                        // или
            textView.setText("");       // содержит один
                                        // символ числа
        }                               // размера вклада
        else{
            String ch = numStr.substring(len - 1, len);
            if (ch.compareTo(".") == 0){
                comma = false;
            }
            numStr = numStr.substring(0, len - 1); // Удаление одного символа
            rewrite();
        }
    }

    void clean(){
        textView.setText("");
        numStr = "";
    }

    String getNumStr(){
        return numStr;
    }

    Double getNum() throws NumberFormatException{
        return Double.parseDouble(numStr);
    }

    boolean hasComma(){
        return this.comma;
    }
}
