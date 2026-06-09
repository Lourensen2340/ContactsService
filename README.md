REST API сервис для управления контактами с ролевой моделью доступа на базе Spring Security.

## Технологический стек
* **Backend:** Java 17+, Spring Boot 3.x (Spring MVC, Spring Data JPA, Spring Security)
* **Identity Provider:** Keycloak (OAuth2 Resource Server / JWT)
* **Database:** SQLite / Hibernate
* **Тестирование:** JUnit 5, Mockito, MockMvc

---

## Предварительные требования (Prerequisites)
Перед запуском приложения убедитесь, что у вас установлены:
1. **Java Development Kit (JDK)** версии 17.
2. **Maven** (или используйте встроенный `mvnw`).

## Сборка и запуск проекта
1. Клонируем репозиторий git clone https://github.com/Lourensen2340/ContactsService.git
2. Далее открываем проект в терминале и вводим docker-compose up --build (запускаем контейнер)
3. После того как всё прошло успешно останавливаем и вводим в терминале mvn install
4. Собираем проект mvn clean package
5. Перед запуском проекта нужно настроить Project Settings Ctrl+Alt+Shift+S 
в Project должны быть выбранны  SDK: ms-17
Language level: 17 - Sealed types, always-strict floating-point sematics 
6. запускаем проект( или через терминал mvn spring-boot:run)

## Гайд по работе с приложением 
1. преходим по адресу http://localhost:8080/swagger-ui/index.html#
2. в приложении нужно зарегестрировать пользователя в /api/v1/auth/register для получение токена или взять в /api/v1/auth/exchange для получения токена
3. далее полученный acsessToken вводим в autorize теперь мы можем создать и изменять удалять и просматривать контакт 
4. для того чтобы изметить роль пользователя используем acsessToken который мы получаем /api/v1/auth/exchange вводим id 1 или id 3
5. выбираем /api/v1/admin/assign-role вводим айдишник пользователя и ROLE_ADMIN или USER

   
