package me.dylano.spleef;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.util.HashMap;
import java.util.Map;

public class Scorboard extends JavaPlugin {

    // HashMap om scores bij te houden voor elke speler
    private Map<Player, Integer> playerScores = new HashMap<>();

    @Override
    public void onEnable() {
        // Start het scoreboard wanneer de plugin wordt ingeschakeld
        createScoreboard();
    }

    // Methode om het scoreboard aan te maken en up-to-date te houden
    public void createScoreboard() {
        new BukkitRunnable() {
            @Override
            public void run() {
                // Scoreboard manager ophalen
                ScoreboardManager manager = Bukkit.getScoreboardManager();
                Scoreboard board = manager.getNewScoreboard();

                // Maak een doelstelling voor de scores
                Objective objective = board.registerNewObjective("Wins", "dummy", ChatColor.GOLD + "Spleef Wins");
                objective.setDisplaySlot(DisplaySlot.SIDEBAR);

                // Loop door alle online spelers om hun score te updaten
                for (Player player : Bukkit.getOnlinePlayers()) {
                    // Haal de score van de speler op uit de HashMap, of stel het in op 0 als het niet bestaat
                    int score = playerScores.getOrDefault(player, 0);
                    Score scoreboardScore = objective.getScore(player.getName());
                    scoreboardScore.setScore(score); // Stel de score in
                    player.setScoreboard(board); // Koppel het scoreboard aan de speler
                }
            }
        }.runTaskTimer(this, 0L, 20L); // Update het scoreboard elke seconde
    }

    // Methode om de score van een speler te verhogen
    public void addWin(Player player) {
        // Verhoog de score van de speler in de HashMap
        int currentScore = playerScores.getOrDefault(player, 0);
        playerScores.put(player, currentScore + 1);

        // Informeer de speler over zijn winst
        player.sendMessage(ChatColor.GREEN + "Je hebt gewonnen! Je totaal aantal punten is nu: " + (currentScore + 1));
    }

    // Event listener of command to trigger a win (example)
    // Dit kun je aanpassen aan je spelmodus-logica
    public void onSpleefWin(Player winner) {
        // Voeg een winst toe aan de winnaar
        addWin(winner);
    }
}
