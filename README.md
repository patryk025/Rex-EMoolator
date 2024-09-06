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
<td>Gra działa bez poważniejszych błędów do momentu dotarcia do wioski (sekwencja z kokosami nie działa poprawnie, nie wykrywa kolizji). Buguje się menu wyboru poziomu. Nie działa powrót z mapy.</td>
</tr>
<tr>
<td>Reksio i Ufo</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Gra działa w większości poprawnie do momentu, gdzie składamy wajhadłowiec. Podczas sekwencji z inwazją ufo są problemy z laserem, który przebija się przez ufo i nie znika przy ziemi, dodatkowo kod interpretera zaczyna przymulać przy takiej ilości obiektów. Buguje się menu przedmiotów, wzięte przedmioty na powrót się pojawiają na swoich miejscach po przeładowaniu sceny. Podczas kalibrowania kreta elementy na ekranach się nie ruszają.</td>
</tr>
<tr>
<td>Reksio i Czarodzieje</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Działa cała sekwencja intra z przerzucaniem kartek i streszczeniem poprzednich części. Na Androidzie ten segment się nieco przymula przez częstą zmianę plików audio i grafik. Działa również plansza z pagórkami, ale po trzeciej planszy zamiast do kolejnej planszy przenosi do chatki Spielmaustera.</td>
</tr>
<tr>
<td>Reksio i Wehikuł Czasu</td>
<td><img alt="W intrze" src="https://img.shields.io/badge/W intrze-orange"/></td>
<td>Odpala się sekwencja z panoramą, ale nie odpala się muzyka w tle i jeden plik audio. Niespodziewanie zoomuje się na kanapę i stoi w miejscu.</td>
</tr>
<tr>
<td>Reksio i Kapitan Nemo</td>
<td><img alt="Niegrywalne" src="https://img.shields.io/badge/Niegrywalne-red"/></td>
<td>Czarny ekran, uruchamia się muzyka w tle, jednak nie przechodzi dalej.</td>
</tr>
<tr>
<td>Reksio i Kretes w Akcji!</td>
<td><img alt="Niegrywalne" src="https://img.shields.io/badge/Niegrywalne-red"/></td>
<td>Czarny ekran, uruchamia się muzyka w tle, jednak nie przechodzi dalej.</td>
</tr>
<tr>
<td>Poznaj Mity: Wyprawa po Złote Runo</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Nie wyświetla się nazwa gry podczas ładowania. Przechodzi do menu i przyciski działają, chociaż czasami się nie wyświetlają. Minigierki na ten moment nie działają.</td>
</tr>
<tr>
<td>Poznaj Mity: Wojna Trojańska</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Przechodzi do menu i przyciski działają, chociaż czasami się nie wyświetlają. Minigierki na ten moment nie działają lub wywalają grę.</td>
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

![Screen z próby uruchomienia Wyprawy po Złote Runo](images/wpzr.jpg)
*Screen z próby uruchomienia Wyprawy po Złote Runo*

![Screen z próby uruchomienia Wojny Trojańskiej](images/wt.jpg)
*Screen z próby uruchomienia Wojny Trojańskiej*

![Screen z próby uruchomienia Przygód Odyseusza](images/po.jpg)
*Screen z próby uruchomienia Przygód Odyseusza*

![Screen z próby uruchomienia Herkulesa](images/herc.jpg)
*Screen z próby uruchomienia Herkulesa*