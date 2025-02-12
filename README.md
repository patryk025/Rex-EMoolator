# Rex EMoolator
Emulator silnika graficznego Piklib/BlooMoo autorstwa firmy Aidem Media oraz interpretera skryptów.

> [!WARNING]
> Na ten moment trwają prace nad poprawą interpretera oraz naprawieniem scen ARCADE i CUTSCENKI. 

Dla tabeli niżej zastosowano następujący podział grywalności:
- ![Grywalne](https://img.shields.io/badge/Grywalne-green) grę daje się ukończyć od początku do końca, bez błędów, które mogą ją zablokować.
- ![W grze](https://img.shields.io/badge/W%20grze-yellow) gra działa, jednak przez różne błędy i niedoróbki nie jest możliwa do ukończenia.
- ![W intrze](https://img.shields.io/badge/W%20intrze-orange) gra ładuje się, jednak nie przechodzi poza intro.
- ![Niegrywalne](https://img.shields.io/badge/Niegrywalne-red) gra nie inicjalizuje się poprawnie, ładuje się z błędem, wywołuje crash emulatora lub daje czarny obraz.

Szacowane poziomy grywalności są wyznaczane na podstawie ilości scen, które się odgrywają poprawnie zgodnie z przepływem narzuconym przez skrypty gry do pierwszego momentu, w którym błędy emulatora uniemożliwiają przejście dalej. Nie uwzględniam tutaj drobnych błędów, problemów z animacjami, a jedynie błędy, które powodują, że scena się nie kończy bądź nie zaczyna lub emulator się crashuje. Z racji, iż wymaga to przechodzenia tytułu przynajmniej od momentu, gdzie wszystko działało, a co któryś raz od początku informacja ta nie będzie aktualizowana na bieżąco.

Aktualne statusy gier opartych na silniku Piklib/BlooMoo:
<table>
<thead>
<tr>
<th>Nazwa gry</th>
<th>Status</th>
<th>Uwagi</th>
<th>Szacowany poziom grywalności</th>
</tr>
</thead>
<tbody>
<tr>
<td>Reksio i Skarb Piratów</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Gra działa bez poważniejszych błędów do momentu dotarcia do wioski. Sekwencja z rzeką jest dość trudna do przejścia (nie, żeby w oryginale było to proste). Cutscenka nieco się buguje oraz kończy się za wcześnie. Sekwencja z kokosami ma drobne różnice w detekcji kolizji w stosunku do oryginału (dodatkowo pojawia się w rogu kura lekko przesłaniająca przycisk menu, nie wiem czemu). Kury po odegraniu poprawnej sekwencji nie śpiewają same z siebie. Etap ze zbieraniem pereł działa, jednak nie pojawia się pasek z tlenem. Są problemy ze strażnikiem do świątyni Boga Twaroga (bugują się tam animacje, włącznie z momentem przejścia do posągu). Przy posągu podczas odpowiadania na pytania pojawiają się nieprawidłowe grafiki z odpowiedziami. Dodatkowo SFX w tle jest chyba za głośny, a Reksio się nie wyświetla. Na trzecim pytaniu zapętlają się animacje. Pomimo otwartych wrót nie przeskakuje dalej, stąd też dalsza rozgrywka jest niemożliwa.</td>
<td>ok. 45% (10/22 scen)</td>
</tr>
<tr>
<td>Reksio i Ufo</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Gra działa w większości poprawnie do momentu, gdzie składamy wajhadłowiec. Podczas sekwencji z inwazją ufo są problemy z laserem, który przebija się przez ufo i nie znika przy ziemi, dodatkowo kod interpretera zaczyna przymulać przy takiej ilości obiektów. Buguje się menu przedmiotów, wzięte przedmioty na powrót się pojawiają na swoich miejscach po przeładowaniu sceny. Podczas kalibrowania kreta elementy na ekranach się nie ruszają.</td>
<td></td>
</tr>
<tr>
<td>Reksio i Czarodzieje</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Działa cała sekwencja intra z przerzucaniem kartek i streszczeniem poprzednich części. Na Androidzie ten segment się nieco przymula przez częstą zmianę plików audio i grafik. Działa również plansza z pagórkami, ale po trzeciej planszy nie ładują się drzwi, tj. jest czarny prostokąt i dodatkowo nie działa. Poprawnie działają zrzuty ekranu podczas zapisywania. Po którejś zmianie przestały się wyświetlać obrazy przy zapisach, jak i tło.</td>
<td><5%</td>
</tr>
<tr>
<td>Reksio i Wehikuł Czasu</td>
<td><img alt="W intrze" src="https://img.shields.io/badge/W intrze-orange"/></td>
<td>Odpala się sekwencja z panoramą, odpala się muzyka w tle, ale sypie się pauzowanie i wznawianie sekwencji. Sekwencja z opowieścią babcia Kretesa działa prawie dobrze, obrazki na projektorze się przełączają. Dodatkowo pod koniec sekwencji z jakiegoś powodu (prawdopodobnie z powodów problemów ze wznawianiem sekwencji) zaczynają się przenikać wcześniejsze sekwencje z późniejszymi.</td>
<td>0%</td>
</tr>
<tr>
<td>Reksio i Kapitan Nemo</td>
<td><img alt="W intrze" src="https://img.shields.io/badge/W intrze-orange"/></td>
<td>Działa cutscenka z intro. Menu z szufladami również, jednak utyka na intrze opowieści, jedynie widać klatkę filmową oraz animację filmowego ziarna.</td>
<td>0%</td>
</tr>
<tr>
<td>Reksio i Kretes w Akcji!</td>
<td><img alt="W intrze" src="https://img.shields.io/badge/W intrze-orange"/></td>
<td>Częściowo działa startowe intro, jednakże utyka na momencie, w który powinna odezwać się Molly. Po kliknięciu myszą przenosi nas do menu. Wszystkie guziki działają i przenoszą od odpowiadających im minigierek. Są problemy z cutscenkami do gierek, brakuje elementów, albo Reksio szczeka, a Kogut się rusza. Po włączeniu Reksio i Skarb Piratów z powodu braku kolizji Reksio spada. Część elementów nie jest rysowana. Przy Reksio i Ufo zbugowane intro a gra rysuje tylko tło. Przy Reksio i Czarodzieje renderuje trasę, jednak całość jest jakby dopalona sterydami. Kolizje nie działają. Przy Reksio i Wehikuł czasu po zbugowanym intro gra się wywala. Przy Super Heros i Kapitan Nemo ładuje się zbugowana cutscenka, ale już nie mapa. Wejście w edytor poziomów zapętla emulator.</td>
<td>0%</td>
</tr>
<tr>
<td>Poznaj Mity: Wyprawa po Złote Runo</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Przechodzi do menu i przyciski działają. Minigierki na ten moment nie działają (bugują się, crashują grę itp.). Bajka prawie działa (są problemy pod Androidem).</td>
<td></td>
</tr>
<tr>
<td>Poznaj Mity: Wojna Trojańska</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Przechodzi do menu i przyciski działają. Minigierki na ten moment nie działają (głównie się bugują), czasami guziki znikają.</td>
<td></td>
</tr>
<tr>
<td>Poznaj Mity: Przygody Odyseusza</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Przechodzi do menu i przyciski działają. Statek Ozyrysa czasami znika. Odnoszę wrażenie, że bajka działa z błędami. Minigierki na ten moment nie działają lub wywalają grę.</td>
<td></td>
</tr>
<tr>
<td>Poznaj Mity: Herkules</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Pojawia się plansza startowa i wchodzi do menu, przyciski działają i udaje się przejść do minigierek, ale nie da się jeszcze grać. Bajka się buguje (grafiki się podmieniają jedynie podczas ręcznego przechodzenia między rozdziałami).</td>
<td></td>
</tr>
</tbody>
</table>

## Garść screenshotów
### Reksio i Skarb Piratów
![Screen z próby uruchomienia Reksio i Skarb Piratów](images/risp.jpg)
*Screen z próby uruchomienia Reksio i Skarb Piratów*

### Reksio i Ufo
![Screen z próby uruchomienia Reksio i Ufo](images/riu.jpg)
*Screen z próby uruchomienia Reksio i Ufo*

### Reksio i Czarodzieje
![Screen z próby uruchomienia Reksio i Czarodzieje](images/ric.jpg)
*Screen z próby uruchomienia Reksio i Czarodzieje*

### Reksio i Wehikuł Czasu
![Screen z próby uruchomienia Reksio i Wehikuł Czasu](images/riwc.jpg)
*Screen z próby uruchomienia Reksio i Wehikuł Czasu*

### Reksio i Kapitan Nemo
![Menu gry Reksio i Kapitan Nemo](images/rikn_menu.jpg)
*Menu gry Reksio i Kapitan Nemo*

![Intro po kliknięciu szuflady](images/rikn_gra_intro.jpg)
*Intro po kliknięciu szuflady*

### Reksio i Kretes w Akcji
![Intro gry Reksio i Kretes w Akcji](images/rikwa_intro.jpg)
*Intro gry Reksio i Kretes w Akcji*

![Menu gry Reksio i Kretes w Akcji](images/rikwa_menu.jpg)
*Menu gry Reksio i Kretes w Akcji*

![Intro gry Reksio i Skarb Piratów](images/rikwa_risp_intro.jpg)
*Intro gry Reksio i Skarb Piratów*

![Gra Reksio i Skarb Piratów](images/rikwa_risp_gra.jpg)
*Gra Reksio i Skarb Piratów*

![Intro gry Reksio i Ufo](images/rikwa_riu_intro.jpg)
*Intro gry Reksio i Ufo*

![Gra Reksio i Ufo](images/rikwa_riu_gra.jpg)
*Gra Reksio i Ufo*

![Intro gry Reksio i Czarodzieje](images/rikwa_ric_intro1.jpg)
*Intro gry Reksio i Czarodzieje*

![Intro gry Reksio i Czarodzieje](images/rikwa_ric_intro2.jpg)
*Intro gry Reksio i Czarodzieje*

![Gra Reksio i Czarodzieje](images/rikwa_ric_gra.jpg)
*Gra Reksio i Czarodzieje*

![Menu gry Reksio i Wehikuł Czasu](images/rikwa_riwc_menu.jpg)
*Menu gry Reksio i Wehikuł Czasu*

![Gra Reksio i Wehikuł Czasu na moment przed wywaleniem się.](images/rikwa_riwc_gra.png)
*Gra Reksio i Wehikuł Czasu na moment przed wywaleniem się.*

![Intro gry Super Heros i Kapitan Nemo](images/rikwa_shikn_intro.jpg)
*Intro gry Super Heros i Kapitan Nemo*

![Gra Super Heros i Kapitan Nemo](images/rikwa_shikn_gra.jpg)
*Gra Super Heros i Kapitan Nemo*

### Poznaj Mity: Wyprawa po Złote Runo
![Screen z próby uruchomienia Wyprawy po Złote Runo](images/wpzr.jpg)
*Screen z próby uruchomienia Wyprawy po Złote Runo*

### Poznaj Mity: Wojna Trojańska
![Screen z próby uruchomienia Wojny Trojańskiej](images/wt.jpg)
*Screen z próby uruchomienia Wojny Trojańskiej*

### Poznaj Mity: Przygody Odyseusza
![Screen z próby uruchomienia Przygód Odyseusza](images/po.jpg)
*Screen z próby uruchomienia Przygód Odyseusza*

### Poznaj Mity: Herkules
![Screen z próby uruchomienia Herkulesa](images/herc.jpg)
*Screen z próby uruchomienia Herkulesa*