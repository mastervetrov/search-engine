
# **SEARCH_ENGINE**

## Поисковой движок с индексацией сайтов и краткой статистикой

## Запуск
Ссылка на скачивание архива с образами приложения и базы данных, инструкция внутри 

https://drive.google.com/file/d/1SedRLiaJainF7x_OcJayc-LmiKB2Y8L3/view?usp=sharing

<img width="419" alt="Снимок экрана 2024-12-04 212811" src="https://github.com/user-attachments/assets/539b8260-a2e2-4f7e-9f8d-c6b86baa2619">


## Использование
### 📃1. Статистика: DASHBOARD
- Отображает общую статистику и статистику по сайтам
<img width="943" alt="image" src="https://github.com/user-attachments/assets/9872bc95-beb2-4370-b2ef-41d941a9d3d4">

### 📊2. ИНДЕКСАЦИЯ: MANAGEMENT
- START INDEXING - индексация/переиндексация всех сайтов из настроек `application.yaml`
- ADD/UPDATE - индексация/переиндексация страницы принадлежащей одному из сайтов конфигурационного файла
<img width="869" alt="image" src="https://github.com/user-attachments/assets/4859ff59-bc04-4e12-8583-ad664027d24c">

### 🔍3. ПОИСК: SEARCH 
- SEARCH - поиск по всем сайтам
- Есть возможность выбрать один конкретный сайт для поиска
<img width="875" alt="image" src="https://github.com/user-attachments/assets/767a4c02-fcdb-4c78-9a5c-13419441dcd8">

## Создано с помощью
- org.apache.lucene.morphology лемматизатор для повышения качества поиска
- maven-shade-plugin для упаковки локальных зависимостей
- liquibase система управления миграциями баз данных
- Docker для создания образов приложения и базы данных
- Maven для управления зависимостями
- Spring фреймворк

  
