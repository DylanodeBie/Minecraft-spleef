package me.dylano.spleef;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.Map;

public class Scorboard {

    // HashMap om scores bij te houden voor elke speler
    private Map<Player, Integer> playerScores = new HashMap<>();
    private Scoreboard board; // Scoreboard instance
    private Objective objective; // Objective instance voor het scoreboard

    public Scorboard() {
        // Maak het scoreboard aan bij initialisatie
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.board = manager.getNewScoreboard();

        // Maak een doelstelling voor de scores
        objective = board.registerNewObjective("Wins", "dummy", ChatColor.GOLD + "Spleef Wins");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
    }

    // Methode om het scoreboard aan een speler toe te wijzen
    public void setPlayerScoreboard(Player player) {
        player.setScoreboard(board);
    }

    // Methode om de scores bij te werken
    public void updateScoreboard() {
        // Loop door de playerScores om de scores bij te werken op het scoreboard
        for (Map.Entry<Player, Integer> entry : playerScores.entrySet()) {
            Player player = entry.getKey();
            int score = entry.getValue();

            // Zorg ervoor dat de score voor de speler in het scoreboard bestaat
            Score scoreboardScore = objective.getScore(player.getName());
            scoreboardScore.setScore(score); // Stel de score in
        }
    }

    // Methode om de score van een speler te verhogen
    public void addWin(Player player) {
        // Verhoog de score van de speler in de HashMap
        int currentScore = playerScores.getOrDefault(player, 0) + 1;
        playerScores.put(player, currentScore);

        // Update het scoreboard
        updateScoreboard();

        // Informeer de speler over zijn winst
        player.sendMessage(ChatColor.GREEN + "Je hebt gewonnen! Je totaal aantal punten is nu: " + currentScore);
    }

    // Methode die wordt aangeroepen als een speler wint
    public void onSpleefWin(Player winner) {
        // Voeg een winst toe aan de winnaar
        addWin(winner);

        // Stuur een broadcast-bericht naar alle spelers
        Bukkit.broadcastMessage(ChatColor.GOLD + winner.getName() + " heeft gewonnen het Spleef-spel!");
    }
}