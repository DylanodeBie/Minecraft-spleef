# Code Conventies voor Spleef Plugin

## 1. Algemene Richtlijnen
- Gebruik Engelse namen voor klassen, methoden en variabelen.
- Gebruik JavaDoc-commentaar voor alle publieke methoden en klassen.
- Houd je aan de **CamelCase** conventie voor methoden en variabelen.
- Houd klassen en methoden klein en overzichtelijk.
- Gebruik geen magische getallen of strings, definieer constanten.

## 2. Bestandsstructuur
Plaats je bestanden in de volgende mappen:
- **`me.dylano.spleef`** → Hoofdpackage voor de plugin.
- **`me.dylano.spleef.utils`** → Hulpmethoden en utilities.
- **`me.dylano.spleef.commands`** → Command-handlers.
- **`me.dylano.spleef.listeners`** → Event-listeners.
- **`plugin.yml`** → In de root, bevat plugin metadata.
- **`CODE_CONVENTIONS.md`** → In de root, beschrijft code standaarden.

## 3. Klassen
- Klassen hebben een duidelijke naam die hun functie beschrijft.
- Klassen starten met een hoofdletter (bijv. `SpleefGame` of `PlayerManager`).

## 4. Variabelen en Constanten
- Gebruik `camelCase` voor variabelen (bijv. `gameRunning` of `playerList`).
- Gebruik `UPPER_CASE` voor constanten (bijv. `MAX_PLAYERS`).
- Definieer constanten bovenaan de klasse als `private static final`.

## 5. Methoden
- Gebruik beschrijvende methodenamen in **camelCase**.
- Methoden moeten een enkele taak uitvoeren.
- Als een methode te lang wordt, splits deze op.
- Gebruik `@Override` annotatie bij het overschrijven van Bukkit-methoden.

## 6. Commentaar
- Gebruik JavaDoc (`/** ... */`) voor methoden en klassen.
- Gebruik `//` voor korte, inline toelichtingen.

## 7. Event-Handling
- Event-methoden beginnen met `on` (bijv. `onPlayerMove`, `onBlockBreak`).
- Gebruik `@EventHandler` annotatie voor event-methoden.
- Controleer of het event relevant is voordat je code uitvoert.

## 8. Commands
- Commands worden afgehandeld in een aparte klasse in `me.dylano.spleef.commands`.
- Controleer altijd of de `sender` een speler is voor speler-specifieke acties.

## 9. Indentatie en Spatiëring
- Gebruik **4 spaties** per niveau inspringing (geen tabs).
- Laat een lege regel tussen methoden voor leesbaarheid.
- Gebruik accolades `{}` ook voor eenregelige `if`-statements.

## 10. Logging en Debugging
- Gebruik `getLogger().info("Bericht")` voor informatieve logs.
- Gebruik `getLogger().warning("Waarschuwing")` voor potentieel problematische situaties.
- Gebruik geen `System.out.println()` in productiecode.

Met deze conventies zorg je voor een gestructureerde en onderhoudbare Bukkit-plugin!

