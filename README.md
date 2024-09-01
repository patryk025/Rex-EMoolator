# Rex EMoolator
Emulator silnika graficznego Piklib/BlooMoo autorstwa firmy Aidem Media oraz interpretera skryptów.

> [!WARNING]
> Na ten moment działa ładowanie plików graficznych, animacji oraz są postępu w odtwarzaniu intra w kilku grach, ale prace nad interpreterem oraz ładowaniem skryptów są w toku.

Dla tabeli niżej zastosowano następujący podział grywalności:
- ![Grywalne](https://img.shields.io/badge/Grywalne-green) grę daje się ukończyć od początku do końca, bez błędów, które mogą ją zablokować.
- ![W grze](https://img.shields.io/badge/W%20grze-yellow) gra działa, jednak przez różne błędy i niedoróbki nie jest możliwa do ukończenia.
- ![W intrze](https://img.shields.io/badge/W%20intrze-orange) gra ładuje się, jednak nie przechodzi poza intro.
- ![Niegrywalne](https://img.shields.io/badge/Niegrywalne-red) gra nie inicjalizuje się poprawnie, ładuje się z błędem, wywołuje crash emulatora lub daje czarny obraz.

Aktualne statusy gier opartych na silniku Piklib/BlooMoo:
<table>
<thead>
<tr>
<th>Nazwa gry</th>
<th>Status</th>
<th>Uwagi</th>
</tr>
</thead>
<tbody>
<tr>
<td>Reksio i Skarb Piratów</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Gra działa bez poważniejszych błędów do momentu dotarcia do wioski (sekwencja z kokosami nie działa poprawnie, nie wykrywa kolizji). Miejscami wyświetla obrazki, których powinno nie być widać.</td>
</tr>
<tr>
<td>Reksio i Ufo</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Gra działa w większości poprawnie do momentu, gdzie składamy wajhadłowiec. Podczas sekwencji z inwazją ufo są problemy z laserem, który przebija się przez ufo i nie znika przy ziemi, dodatkowo kod interpretera zaczyna przymulać przy takiej ilości obiektów. Miejscami wyświetla obrazki, których powinno nie być widać.</td>
</tr>
<tr>
<td>Reksio i Czarodzieje</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Działa cała sekwencji intra z przerzucaniem kartek i streszczeniem poprzednich części. Na Androidzie ten segment się nieco przymula przez częstą zmianę plików audio i grafik. Po zakończeniu intra poprawnie ładują się pagórki. Pojawiły się problemy z prawidłowym wykrywaniem, który pagórek został kliknięty.</td>
</tr>
<tr>
<td>Reksio i Wehikuł Czasu</td>
<td><img alt="W intrze" src="https://img.shields.io/badge/W intrze-orange"/></td>
<td>Odpala się sekwencja z panoramą, ale nie odpala się muzyka w tle i jeden plik audio, przez co gra staje w miejscu</td>
</tr>
<tr>
<td>Reksio i Kapitan Nemo</td>
<td><img alt="Niegrywalne" src="https://img.shields.io/badge/Niegrywalne-red"/></td>
<td>Czarny ekran, występują błędy z analizą skryptów</td>
</tr>
<tr>
<td>Reksio i Kretes w Akcji!</td>
<td><img alt="Niegrywalne" src="https://img.shields.io/badge/Niegrywalne-red"/></td>
<td>Czarny ekran, uruchamia się muzyka w tle, występują pewne problemy z ładowaniem skryptów</td>
</tr>
<tr>
<td>Poznaj Mity: Wyprawa po Złote Runo</td>
<td><img alt="W intrze" src="https://img.shields.io/badge/W intrze-orange"/></td>
<td>Pojawia się plansza startowa, nie przechodzi dalej</td>
</tr>
<tr>
<td>Poznaj Mity: Wojna Trojańska</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Pojawia się plansza startowa. Nic więcej nie daje się zrobić.</td>
</tr>
<tr>
<td>Poznaj Mity: Przygody Odyseusza</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Pojawia się plansza startowa. Nic więcej nie daje się zrobić.</td>
</tr>
<tr>
<td>Poznaj Mity: Herkules</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Pojawia się plansza startowa i wchodzi do menu, przyciski działają i udaje się przejść do minigierek, ale nie da się jeszcze grać.</td>
</tr>
</tbody>
</table>

## Garść screenshotów
![Screen z próby uruchomienia Reksio i Skarb Piratów](images/risp.jpg)
*Screen z próby uruchomienia Reksio i Skarb Piratów*

![Screen z próby uruchomienia Reksio i Ufo](images/riu.jpg)
*Screen z próby uruchomienia Reksio i Ufo*

![Screen z próby uruchomienia Reksio i Czarodzieje](images/ric.jpg)
*Screen z próby uruchomienia Reksio i Czarodzieje*

![Screen z próby uruchomienia Reksio i Wehikuł Czasu](images/riwc.jpg)
*Screen z próby uruchomienia Reksio i Wehikuł Czasu*