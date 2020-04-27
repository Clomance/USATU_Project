# USATU_Project
Курсовая работа


# Минимальные требования
 - Версия Android 4.4 или выше
 - Разрешение для использавние интернета

# О приложении
Приложение состоит из четырёх страниц (активностей), каждая из которых выполняет свою функцию.
##### Страницы:
- StartActivity - стартовая активность, отвечает за авторизацию и получение разрешений.
- CalculateActivity - активность калькулятора, главная активность, в которой происходит ввод данных для расчёта.
- SettingsActivity - активность для смены адреса и порта сервера. Все изменения нужно обновлять при каждом запуске.
- InfoActivity - активность с информацией о пользователе и его историей.

Из стартовой страницы можно попасть только в калькулятор (путём аторизации) и в настройки (через кнопку перехода к настройкам),
при этом из настроек попать в другие активности нельзя.


После входа появляется выдвижная слева панель навигации, 
с помощью которой можно свободно переключаться между все страницами, кроме стартовой.


При запуске приложение подгружает сохранённые настройки и персональные данные, если они имеются.

# Работа с приложением

После запуска приложения открывается страница авторизации, в которой доступны доступны следующие действия:
- вход с помощью логина и пароля
- регистрация, достаточно просто ввести данные в поля и нажать кнопку
- вход в режиме гостя, с отключённой возможностью расчёта
- настройки

После входа открывается страница калькулятора (главная страница).
# Скриншоты
## Стартовая страница 
![screenshot1](https://c.radikal.ru/c42/2004/7e/41e2ee573394.png)

## Пустой калькулятор
![screenshot2](https://a.radikal.ru/a03/2004/ac/e8dcea276d7b.png)

## Калькулятор с введёнными данными
![screenshot3](https://b.radikal.ru/b34/2004/e9/deb745347c97.png)
