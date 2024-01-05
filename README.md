# **Delivery app**

### *Описание*
Это микросервис для создания заказа на доставку посылки из точки А в точку Б. А также регистрации и корректировки пользователей и курьеров.

### *О проекте*
Язык программирования: **Java 17**

Сборщик: **Maven**

Используемые технологии:
- **Spring Boot v.3.1.4**
- **Spring Data JPA**
- **Spring Security** + **JWT**
- **PostgreSql**
- **FlyWay**
- **Swagger**
- **Junit** + **Mockito**
- **Docker**

Используемые паттерны проектирования:
- **Builder**
  
Настройки подключения к БД находятся по пути: 
- **src/main/resources/application.properties**

По умолчанию там выставлены настройки для подключения к локальной БД, которые можно изменить при необходимости.

Рабочая зона:
- **src/main/java/com/factglobal/delivery**

Содержит 6 java каталогов:
- config - файлы конфигурации security;
- controllers - 5 классов контроллеров;
- dto - набор объектов, передающих данные;
- models - модели объектов User, Role, Courier, Customer и Order;
- repositories - репозитории к моделям;
- services - бизнес логика.
- util - содержит классы обработчики ошибок и EntityValidors

### *Документация API*
- **http://localhost:8080/swagger-ui.html**
