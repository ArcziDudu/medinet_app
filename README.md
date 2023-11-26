Medinet - Aplikacja do Umawiania Wizyt

Medinet to aplikacja umożliwiająca łatwe umawianie wizyt u specjalistów z różnych miast Polski. Dzięki Medinet użytkownicy mogą szybko znaleźć i zarezerwować termin wizyty u lekarza, dentysty, fizjoterapeuty czy innego specjalisty medycznego.

![img_1.png](readmeImages/img_1.png)

Funkcjonalności:

1. Przeglądanie dostępnych specjalistów: Użytkownicy mogą przeglądać listę specjalistów z różnych miast Polski, wybierając interesującą ich dziedzinę medycyny.
2. System oferuję paginacje strony dzięki czemu na jednej stronie wyświetlanych jest 15 lekarzy, na nastęne strony przechodzimy korzystając z przycisków
3. Wyszukiwanie specjalistów: Umożliwia szybkie wyszukanie konkretnego specjalisty po dziedzinie medycyny oraz mieście.
4. Rezerwacja wizyty: Po znalezieniu odpowiedniego specjalisty, użytkownik może dokonać rezerwacji wizyty, wybierając dogodny termin.
   Przy czym terminy są dostęne na 2 tygodnie do przodu, nie wliczająć weekendu. Wystarczy kliknąć w wybraną godzinę w danym dniu, po akceptacji wizyty, wolny termin zostanie usunięty lekarzowi i zarówno lekarz jak i pacjent będą mogli anulować wizytę dopóki się jeszcze nie odbyła
![img_2.png](readmeImages/img_2.png)
![img.png](readmeImages/img.png)
5. Wystawienie opinii lekarzowi - Pacjent ma możliwość wystawienia opinii lekarzowi która będzie widoczna na jego profilu po kliknięciu w przycisk szczegóły
![img_3.png](readmeImages/img_3.png) 
 
6. Pacjent może pobrać fakture z wizyty która się odbyła. Aby to zrobić należy zrobić poniższe kroki:
   - Wybieramy interesujący nas termin: Wizyty są dostępne na dwa tygodnie do przodu nie licząc weekendu, od godziny 8 do 15 - takie są zasady zatrudniania specjalistów w Medinet. Logika programu zakłada że rezerwować wizytę można zawsze na conajmniej następny dzień natomiast żeby umożliwić przetestowanie wszystkich możliwości, dodany jest również dzień dzisiejszy.
   - Dlatego w celu testu wybieramy godzine oraz date która już minęła np jeśli godzina testowania to czwartek 27 lipca godzina 17;00 to wybieramy termin wizyty czwartek 27 lipca godzina 10 rano - chodzi o to zebyśmy nie musieli czekać kilka dni na wizyte żeby lekarz mógł wystawić opinie, docelowo najbliższy termin jaki możemy zarezerwować to zawsze następny dzień roboczy. 
 - Po zarezerwowaniu wizyty, przechodzimy na profil specjalisty, jego email to jego Imie@medinet.com , dostępny jest też w  szczegółach lekarza u którego zarezerwowalismy wizyte, klikamy przycisk wyloguj i logujemy sie na profil lekarza skopiowanym emailem, hasło dla każdego specjalisty to: test
   , następnie klikamy w przycisk "Do zatwierdzenia" - program działa tak że sprawdza co 10 sekund czy wizyta się odbyła to znaczy czy wybrana godzina juz minęła wtedy otrzymuje status "pending" i czeka na potwierdzenie lekarza

   - wystawiamy zalecenia lub recepte i zatwierdzamy
   - w tym momencie wizyta jest również pobrana do bazy danych jako pdf i można ją pobrać jako pokwitowanie
   
  ![img_5.png](readmeImages/img_5.png)
     
- Wizyta w tym momencie ma status zakończonej, można ją sprawdzić klkając w przycisk "Zakończone"
- Wylogowujemy się i wracamy do konta które rezerwowało wizytę, wchodzimy na "moje konto"
- W tym momencie mamy możliwość pobrania faktury za wizyte, wystawianej przez zewnętrzne api ze strony https://yakpdf.p.rapidapi.com/pdf
- Wybieramy miejsce docelowe i zapisujemy plik
- Jest to darmowe api i pozwala na  200 darmowych wywołań

6. Rezerwacja w serwisie:
   - użytkownik może się zarejestrować przy czym program waliduje dane wejściowe
   - użytkownik musi potwierdzić rejestracje wprowadzając kod otrzymany w wiadomosci email
     
![img_7.png](readmeImages/img_7.png)
7 Odzyskiwanie hasła:
  - użytkownik ma  możliwość odzyskania hasła o ile email istnieje w bazie danych oraz nie należy on do lekarza. Liczba lekarzy w serwisie jest z góry określona i nie można ich dodawać ale można logować się na ich konta.
  - Mechanizm działa w taki sposób, że jeżeli adres email istnieje w bazie danych to wysyłane jest na ten adres wygenerowane przez system hasło, przy którym można zostać lub zmienić je w zakładce Moje konto
![img_8.png](readmeImages/img_8.png)

# Medinet Application

## Uruchamianie w środowisku Docker

Aby uruchomić program w środowisku Docker, wykonaj poniższe kroki:

1. **Pobierz repozytorium:**
    ```bash
    git clone https://github.com/ArcziDudu/medinet_app.git
    ```

2. **Zbuduj plik JAR:**
    ```bash
    gradlew build
    ```

3. **Zbuduj obraz Dockera:**
    ```bash
    docker build -t medinet:latest .
    ```

4. **Uruchom kontener:**
    ```bash
    docker-compose up -d
    ```

5. **Włącz logi aplikacji:**
    ```bash
    docker logs medinet_app-master-backend-1
    ```

6. **Otwórz przeglądarkę i przejdź pod adres:**
    [http://localhost:8190/medinet/](http://localhost:8190/medinet/)

7. **Zaloguj się:**
   - Dla dostępnego konta admina: 
     - Email: `admin@admin.pl`
     - Hasło: `test`
   - Zarejestruj nowe konto.
   - Zaloguj się na konto lekarza, na przykład:
     - Email: `Krystian@medinet.com`
     - Hasło: `test`


Aplikacja wystawia Rest api, kontrakt open api jest dostępny w głównym katalogu aplikacji
Dostępny jest również diagram erd bazy danych
Pokrycie testami wynosi 82% - raport jacoco generowany jest podczas budowania projektu i znajduje sie w katalogu build/reports
# Zastosowane Technologie

Aplikacja Medinet została zbudowana przy użyciu następujących technologii:

- **Java**: Główny język programowania używany do implementacji aplikacji.

- **Spring Boot**: Framework Java do tworzenia aplikacji

- **Gradle**: Narzędzie do automatyzacji budowy projektu i zarządzania zależnościami.

- **Hibernate**: Narzędzie ORM (Object-Relational Mapping) do obsługi bazy danych.

- **Flyway**: Biblioteka do zarządzania migracjami bazy danych.

- **Java Mail Sender**: Biblioteka do obsługi wysyłania wiadomości e-mail.

- **Docker**: Platforma do konteneryzacji aplikacji.

- **OpenAPI**: Specyfikacja interfejsu API, używana do dokumentowania API.

- **[YakPDF](https://yakpdf.com/)**: Usługa do generowania plików PDF.

- **PostgreSQL**: Baza danych relacyjna.

- **Rest Assured**: Biblioteka do testowania RESTful API.

- **Wiremock**: Narzędzie do symulowania usług zewnętrznych podczas testów.

- **Spring Security**: Framework do zarządzania bezpieczeństwem w aplikacjach opartych na Spring.

- **Lombok**: Biblioteka do eliminowania rutynowego kodu Java.

- **HTML, CSS, Bootstrap**: Technologie do tworzenia interfejsu użytkownika.

Dodatkowe informacje dotyczące poszczególnych technologii i ich konfiguracji znajdziesz w odpowiednich plikach projektu.


