# Rex EMoolator
Emulator silnika graficznego Piklib/BlooMoo autorstwa firmy Aidem Media oraz interpretera skryptów.

> [!WARNING]
> Na ten moment wysiłki są skoncentrowane na naprawieniu Reksio i Ufo, szczególnie etapów z kamieniami, wyścigami, platformami oraz "odkurzaniem Jaja z kury". 

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
<td><img alt="Grywalne" src="https://img.shields.io/badge/Grywalne-green"/></td>
<td>Gra w pełni grywalna z drobnymi bugami. Etapy z przejściem rzeki zaczęły się przymulać (do sprawdzenia). Sekwencja z kokosami ma drobne różnice w detekcji kolizji w stosunku do oryginału (dodatkowo pojawia się w rogu kura lekko przesłaniająca przycisk menu, nie wiem czemu). Podczas zagadek przy posągu Boga Twaroga sporadycznie grafiki odpowiedzi są zamienione miejscami (narrator jednak czyta poprawnie).W etapie z UFO sporadycznie są problemy z priorytetami animacji (winda przesłania Reksia, do weryfikacji). Poza tymi niekrytycznymi bugami wszystko działa poprawnie.</td>
<td>100%</td>
</tr>
<tr>
<td>Reksio i Ufo</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Gra działa poprawnie do momentu dotarcia do wąwozu na Indorze. Sekcja podkopu pod murem więzienia oraz burzenia muru na Indorze została zaimplementowana na tyle, żeby etapy w skryptach działały poprawnie, nie sprawdzałem już egzotycznych kombinacji, które na oryginalnych skryptach nie wystąpią. W dalszym etapie gra utyka w sekcji platformowej na Indorze. Odtwarza się animacja Reksia jak wchodzi, po czym zmienia się w animację spadania. Klawisze kierunkowe działają, jednak ten etap działa bardzo niestabilnie i lubi scrashować emulator</td>
<td>ok. 30% (26/86 scen)</td>
</tr>
<tr>
<td>Reksio i Czarodzieje</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Zbugowane menu. Prace w toku.</td>
<td></td>
</tr>
<tr>
<td>Reksio i Wehikuł Czasu</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Po ostatnich aktualizacjach jest problem z cutscenkami w intro.</td>
<td>0%</td>
</tr>
<tr>
<td>Reksio i Kapitan Nemo</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Po ostatnich aktualizacjach jest problem z cutscenkami w intro.</td>
<td>0%</td>
</tr>
<tr>
<td>Reksio i Kretes w Akcji!</td>
<td><img alt="W grze" src="https://img.shields.io/badge/W grze-yellow"/></td>
<td>Działa startowe intro i przechodzi poprawnie dalej. Po kliknięciu myszą przenosi nas do menu. Wszystkie guziki działają i przenoszą od odpowiadających im minigierek. Reksio i Skarb Piratów technicznie działa. Tło się nie przesuwa, ale kolizje działają. Przy Reksio i Ufo gra rysuje tylko tło, Sikora Millenium oraz jakieś efekty cząsteczkowe, ale przez brak implementacji Inertia etap nie działa. Przy Reksio i Czarodzieje renderuje trasę, jednak całość jest jakby dopalona sterydami. Kolizje działają, ale elementy się nie przesuwają. Przy Reksio i Wehikuł Czasu mapa się rysuje oraz daje się poruszać postaciami, jednak są błędy oraz problemy z grafikami i ich pozycjami. Przy Super Heros i Kapitan Nemo nie ładuje się mapa. Edytor map jest również niekompletny.</td>
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
