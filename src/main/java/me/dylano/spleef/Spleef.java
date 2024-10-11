package me.dylano.spleef;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


import java.util.ArrayList;
import java.util.List;

public class Spleef extends JavaPlugin implements Listener {

    private List<Player> spleefPlayers = new ArrayList<>();
    private boolean gameRunning = false;

    @Override
    public void onEnable() {
        Bukkit.getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("SpleefPlugin is geactiveerd!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SpleefPlugin is gedeactiveerd!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("startspleef")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                if (gameRunning) {
                    player.sendMessage("Het spel is al gestart!");
                } else {
                    startSpleef(player);
                }
            }
            return true;
        }
        return false;
    }

    private void startSpleef(Player player) {
        gameRunning = true;
        spleefPlayers.add(player);

        // Teleporteer alle spelers naar de spleefarena (verander de coördinaten naar jouw arena)
        for (Player p : spleefPlayers) {
            p.teleport(Bukkit.getWorld("world").getSpawnLocation()); // Stel arena spawn in
            p.setGameMode(GameMode.SURVIVAL);
            p.getInventory().clear();
            ItemStack diamondShovel = new ItemStack(Material.DIAMOND_SHOVEL);
            ItemMeta meta = diamondShovel.getItemMeta();
            if (meta != null) {
                // Voeg de Efficiency V enchantment toe
                meta.addEnchant(Enchantment.DIG_SPEED, 5, true);
                diamondShovel.setItemMeta(meta); // Stel de aangepaste metadata in
            }
            p.getInventory().addItem(diamondShovel);
        }
        Bukkit.broadcastMessage("Spleef-spel gestart door " + player.getName() + "!");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (gameRunning && spleefPlayers.contains(player)) {
            if (event.getBlock().getType() == Material.SNOW_BLOCK) {
                event.setCancelled(false);  // Sta het breken van de blokken toe
            } else {
                event.setCancelled(true);  // Voorkom dat andere blokken worden gebroken
            }
        }
    }

    // Voeg methoden toe voor als spelers verliezen of vallen, bijv. als ze onder een bepaalde Y-coördinaat komen
    @EventHandler
    public void onPlayerFall(org.bukkit.event.player.PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (gameRunning && spleefPlayers.contains(player)) {
            if (player.getLocation().getY() < 10) {  // Verander de Y-waarde naar waar ze uit het spel vallen
                Bukkit.broadcastMessage(player.getName() + " is uitgeschakeld!");
                spleefPlayers.remove(player);
                player.setGameMode(GameMode.SPECTATOR);

                // Check of er nog maar één speler over is
                if (spleefPlayers.size() == 1) {
                    Player winner = spleefPlayers.get(0);
                    Bukkit.broadcastMessage(winner.getName() + " heeft gewonnen!");
                    endGame();
                }
            }
        }
    }

    private void endGame() {
        gameRunning = false;
        spleefPlayers.clear();
    }
}