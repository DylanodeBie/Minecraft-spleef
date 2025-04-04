package me.dylano.spleef;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.enchantments.Enchantment;

import java.util.ArrayList;
import java.util.List;

public class Spleef extends JavaPlugin implements Listener {

    private final List<Location> originalBlocks = new ArrayList<>();
    private List<Player> spleefPlayers = new ArrayList<>(); // Lijst van spelers in de spleef match
    private boolean gameRunning = false; // Boolean om bij te houden of het spel al loopt
    private Scorboard scoreboard; // Scoreboard-object
    private BossBar queueBossBar; // BossBar voor de queue

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("SpleefPlugin is geactiveerd!");
        scoreboard = new Scorboard(); // Initialiseer de scoreboard

        // Maak de BossBar aan en stel de stijl en kleur in
        queueBossBar = Bukkit.createBossBar("Spleef Queue", BarColor.BLUE, BarStyle.SOLID);
        queueBossBar.setVisible(false); // Maak deze standaard onzichtbaar
    }

    @Override
    public void onDisable() {
        getLogger().info("SpleefPlugin is gedeactiveerd!");

        // Verwijder de BossBar bij uitschakeling van de plugin
        queueBossBar.removeAll();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("joinspleef")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (gameRunning) {
                    player.sendMessage(ChatColor.RED + "Het Spleef-spel is al gestart. Je kunt niet meer meedoen.");
                } else {
                    if (!spleefPlayers.contains(player)) {
                        spleefPlayers.add(player);
                        player.sendMessage(ChatColor.GREEN + "Je hebt je aangemeld voor het Spleef-spel!");

                        // Voeg speler toe aan de BossBar en update deze
                        queueBossBar.addPlayer(player);
                        updateQueueBossBar();
                    } else {
                        player.sendMessage(ChatColor.RED + "Je bent al aangemeld voor het Spleef-spel.");
                    }
                }
            }
            return true;
        }

        if (command.getName().equalsIgnoreCase("startspleef")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (gameRunning) {
                    player.sendMessage(ChatColor.RED + "Het Spleef-spel is al gestart!");
                    return true;
                }

                if (spleefPlayers.size() < 2) {
                    player.sendMessage(ChatColor.RED + "Er zijn niet genoeg spelers om het spel te starten! Minimaal 2 spelers zijn nodig.");
                } else {
                    startSpleef(player); // Start het spel
                }
            }
            return true;
        }

        // Add the new command for changing game modes
        if (command.getName().equalsIgnoreCase("gamemode")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("survival")) {
                        player.setGameMode(GameMode.SURVIVAL);
                        player.sendMessage(ChatColor.GREEN + "Je bent nu in Survival mode.");
                    } else if (args[0].equalsIgnoreCase("creative")) {
                        player.setGameMode(GameMode.CREATIVE);
                        player.sendMessage(ChatColor.GREEN + "Je bent nu in Creative mode.");
                    } else {
                        player.sendMessage(ChatColor.RED + "Ongeldige game mode. Gebruik 'survival' of 'creative'.");
                    }
                } else {
                    player.sendMessage(ChatColor.RED + "Geef een game mode op. Gebruik 'survival' of 'creative'.");
                }
            }
            return true;
        }

        return false;
    }

    private void updateQueueBossBar() {
        if (spleefPlayers.isEmpty()) {
            queueBossBar.setVisible(false); // Verberg de BossBar als er geen spelers in de wachtrij zijn
            queueBossBar.setTitle("Spleef Queue");
        } else {
            queueBossBar.setVisible(true); // Maak de BossBar zichtbaar als er spelers in de wachtrij zijn

            // Stel de titel in met de namen van de spelers in de wachtrij
            StringBuilder names = new StringBuilder("Spleef Queue: ");
            for (Player p : spleefPlayers) {
                names.append(p.getName()).append(" ");
            }
            queueBossBar.setTitle(names.toString().trim());
        }
    }

    private void startSpleef(Player player) {
        gameRunning = true;

        queueBossBar.setVisible(false); // Verberg de queueBossBar omdat het spel is begonnen

        // Define the starting location for the parkour
        Location parkourStartLocation = new Location(Bukkit.getWorld("world"), -1288, 189, 700);

        // Teleporteer spelers naar de arena en stel hun inventaris in
        for (Player p : spleefPlayers) {
            p.teleport(parkourStartLocation);
            p.setGameMode(GameMode.SURVIVAL);
            p.getInventory().clear();

            // Geef de speler een enchanted diamond shovel
            ItemStack diamondShovel = new ItemStack(Material.DIAMOND_SHOVEL);
            ItemMeta meta = diamondShovel.getItemMeta();
            if (meta != null) {
                meta.addEnchant(Enchantment.DIG_SPEED, 5, true);
                diamondShovel.setItemMeta(meta);
            }
            p.getInventory().addItem(diamondShovel);

            scoreboard.setPlayerScoreboard(p);
        }

        // Build the parkour structure at the defined starting location
        resetArena();

        Bukkit.broadcastMessage(ChatColor.GOLD + "Spleef-spel gestart door " + player.getName() + "!");
    }

    private void sendEndGameTitle(Player winner) {
        String title = winner != null ? winner.getName() + " heeft gewonnen!" : "Het Spleef-spel is beëindigd!";
        String subtitle = winner != null ? "Gefeliciteerd!" : "Probeer het opnieuw.";

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendTitle(title, subtitle, 10, 70, 20); // Stuur de title message naar elke speler
        }
    }

    @EventHandler
    public void onPlayerFall(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (gameRunning && spleefPlayers.contains(player)) {
            if (player.getLocation().getY() < 145) { // Controleer of de speler is gevallen
                spleefPlayers.remove(player);
                player.setGameMode(GameMode.SPECTATOR);
                Bukkit.broadcastMessage(ChatColor.RED + player.getName() + " is uitgeschakeld!");

                // Controleer hoeveel spelers er over zijn
                if (spleefPlayers.size() == 1) {
                    // Er is nog één winnaar
                    Player winner = spleefPlayers.get(0);

                    // Voeg een punt toe aan het scoreboard
                    scoreboard.onSpleefWin(winner);

                    // Lanceer vuurwerk en laat een titel zien
                    launchFireworks(winner);
                    sendEndGameTitle(winner);

                    // Stop het spel
                    endGame();
                } else if (spleefPlayers.isEmpty()) {
                    // Niemand heeft gewonnen
                    sendEndGameTitle(null); // Geen winnaar
                    endGame();
                }
            }
        }
    }


    // Methode voor vuurwerk
    private void launchFireworks(Player player) {
        Firework firework = (Firework) player.getWorld().spawnEntity(player.getLocation(), EntityType.FIREWORK);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        fireworkMeta.addEffect(FireworkEffect.builder()
                .withColor(Color.YELLOW)
                .withFade(Color.RED)
                .with(FireworkEffect.Type.BALL)
                .trail(true)
                .flicker(true)
                .build());

        fireworkMeta.setPower(1); // Kracht van vuurwerk
        firework.setFireworkMeta(fireworkMeta);
        firework.detonate(); // Ontsteek het vuurwerk onmiddellijk
    }

    private void buildParkour(Location startLocation) {
        World world = startLocation.getWorld();
        if (world == null) return;

        // **Reset de arena eerst**
        resetArena();

        originalBlocks.clear(); // **Maak de lijst leeg voordat je nieuwe blokken opslaat**

        int height = startLocation.getBlockY();
        int baseX = startLocation.getBlockX();
        int baseZ = startLocation.getBlockZ();

        for (int i = 0; i < 3; i++) {
            for (int y = 0; y < 1; y++) {
                for (int x = -3; x <= 3; x++) {
                    for (int z = -3; z <= 3; z++) {
                        Location blockLocation = new Location(world, baseX + x, height + (i * 6) + y, baseZ + z);
                        world.getBlockAt(blockLocation).setType(Material.SNOW_BLOCK);
                        originalBlocks.add(blockLocation); // **Sla nieuwe blokken op**
                    }
                }
            }
        }

        Bukkit.broadcastMessage(ChatColor.GREEN + "Het parkour is succesvol gegenereerd!");
    }


    private void endGame() {
        gameRunning = false;

        // Zet alle spelers in survival mode na het spel
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getGameMode() == GameMode.SPECTATOR) {
                player.setGameMode(GameMode.SURVIVAL);
            }
        }

        spleefPlayers.clear(); // Wis de spelerslijst
        queueBossBar.setVisible(false); // Verberg de BossBar als het spel is afgelopen
        Bukkit.broadcastMessage(ChatColor.GOLD + "Het Spleef-spel is beëindigd.");

        resetArena();
    }

    private void resetArena() {
        Location defaultLocation = new Location(Bukkit.getWorld("world"), -1288, 176, 700);
        resetArena(defaultLocation);
    }


    private void resetArena(Location startLocation) {
        if (startLocation == null) {
            startLocation = new Location(Bukkit.getWorld("world"), -1288, 176, 700); // Standaard locatie
        }

        World world = startLocation.getWorld();
        if (world == null) return;

        // **Stap 2: Bouw de arena opnieuw**
        int height = startLocation.getBlockY();
        int baseX = startLocation.getBlockX();
        int baseZ = startLocation.getBlockZ();

        for (int i = 0; i < 3; i++) { // Drie niveaus maken
            for (int y = 0; y < 1; y++) {
                for (int x = -3; x <= 3; x++) {
                    for (int z = -3; z <= 3; z++) {
                        Location blockLocation = new Location(world, baseX + x, height + (i * 6) + y, baseZ + z);
                        world.getBlockAt(blockLocation).setType(Material.SNOW_BLOCK);
                        //originalBlocks.add(blockLocation); // **Nieuwe blokken opslaan**
                    }
                }
            }
        }

        Bukkit.broadcastMessage(ChatColor.AQUA + "De spleef arena is opnieuw opgebouwd!");
    }
}