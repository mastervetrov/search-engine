# search_engine
Поисковой движок с индексацией сайтов и краткой статистикой
## Условия запуска
### 1. Подготовка
- скачать и установить IntelliJ Idea Community Edition (Windows, MacOS)
- скачать и установить Docker (Windows, MacOS)
- скачать и распаковать проект "searchengine" (Yandex drive)
- скачать и распаковать контейнер Docker "searche_engine_db" (Yandex drive)

### 2. Запуск базы данных
- запускаем командную строку и переходим в директорию с распакованным "searche_engine_db", вводим команду
  `docker start searche_engine_db`
- с помощью команды `docker ps` удостоверимся, что докер контейнер с базой данных запущен
<img width="959" alt="image" src="https://github.com/user-attachments/assets/e8d3d9ac-4509-4798-9be1-2bde93ee211e">

### 3. Настройка
- открываем проект в IntelliJ Idea и указываем сайты, которые нужно проиндексировать в файле `application.yaml`
<img width="674" alt="image" src="https://github.com/user-attachments/assets/91086259-3a17-4147-b5f4-317fbeae35a3">

### 4. Запуск
- Запускаем программу и открываем любой браузер с адресом `localhost:8080`

## Использование
### 1. Статистика: DASHBOARD
- Отображает общую статистику и статистику по сайтам
<img width="943" alt="image" src="https://github.com/user-attachments/assets/9872bc95-beb2-4370-b2ef-41d941a9d3d4">

### 2. ИНДЕКСАЦИЯ: MANAGEMENT
- START INDEXING - индексация/переиндексация всех сайтов из настроек `application.yaml`
- ADD/UPDATE - индексация/переиндексация страницы принадлежащей одному из сайтов конфигурационного файла
<img width="869" alt="image" src="https://github.com/user-attachments/assets/4859ff59-bc04-4e12-8583-ad664027d24c">

### 3. ПОИСК: SEARCH 
- SEARCH - поиск по всем сайтам
- Есть возможность выбрать один конкретный сайт для поиска
<img width="875" alt="image" src="https://github.com/user-attachments/assets/767a4c02-fcdb-4c78-9a5c-13419441dcd8">

## Создано с помощью
- Spring: фреймворк
- Maven: управление зависимостями
- org.apache.lucene.morphology: лемматизатор для повышения качества поиска
