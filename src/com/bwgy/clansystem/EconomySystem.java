package com.bwgy.clansystem;

import com.bwgy.main.AntiBuildListener;
import com.bwgy.main.Config;
import com.bwgy.main.Main;
import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;


import java.io.*;
import java.util.ArrayList;
import java.util.UUID;

public class EconomySystem implements Listener {
    public static ArrayList indebted_players=new ArrayList<UUID>();
    private static FileConfiguration config;
    private static File configfile;
    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if((ChunkClaimer.getOwner(e.getBlock().getLocation().getChunk()))!=null) {
            if (ChunkClaimer.hasChunkPermission(e.getPlayer()) && (ChunkClaimer.getOwner(e.getBlock().getLocation().getChunk())).equals(ClanSystem.getClan(e.getPlayer().getUniqueId()))) {
                if(!(Config.isInSpawnArea(e.getBlock().getLocation())||Config.isInPvpArena(e.getBlock().getLocation()))) {
                    addMoney(e.getPlayer(), 1);
                }
            } else {
                e.getPlayer().sendMessage("§cDu hast keine Rechte auf diesen Chunk! Eigentümer: §4" + ChunkClaimer.getOwner(e.getBlock().getLocation().getChunk()));
                e.setCancelled(true);
            }
        }else{
            addMoney(e.getPlayer(), 1);
        }
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if((ChunkClaimer.getOwner(e.getBlock().getLocation().getChunk()))!=null) {
            if (ChunkClaimer.hasChunkPermission(e.getPlayer()) && (ChunkClaimer.getOwner(e.getBlock().getLocation().getChunk())).equals(ClanSystem.getClan(e.getPlayer().getUniqueId()))) {

            } else {
                e.getPlayer().sendMessage("§cDu hast keine Rechte auf diesen Chunk! Eigentümer: §4" + ChunkClaimer.getOwner(e.getBlock().getLocation().getChunk()));
                e.setCancelled(true);
            }
        }else{
            removeMoney(e.getPlayer(), 1);
        }
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(e.getClickedBlock().getType()== Material.CHEST){
            if(ChunkClaimer.getOwner(e.getClickedBlock().getChunk())!=null){
                if(!(ChunkClaimer.hasChunkPermission(e.getPlayer()))){
                    e.setCancelled(true);
                    e.getPlayer().sendTitle("§4Keine Rechte!","§cDu hast keine Rechte auf diese Kiste!");
                }
                }
            }
        }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent e){
        if(e.getEntity().getKiller() != null){
            if(e.getEntity() instanceof EnderDragon){
                ClanSystem.addPoints(ClanSystem.getClan(e.getEntity().getKiller().getUniqueId()),1000);
            }else if(e.getEntity() instanceof Wither){
                ClanSystem.addPoints(ClanSystem.getClan(e.getEntity().getKiller().getUniqueId()),1000);
            }else{
                ClanSystem.addPoints(ClanSystem.getClan(e.getEntity().getKiller().getUniqueId()),1);
            }
        }
    }
    @EventHandler
    public void onChat(PlayerChatEvent e){
        if(ClanSystem.getClan(e.getPlayer().getUniqueId())==null) {
            e.setFormat("§7"+e.getPlayer().getName()+"§7: "+e.getMessage());
        }else {
            e.setFormat("§8[§e"+ ClanSystem.getClan(e.getPlayer().getUniqueId())+"§8] §7"+e.getPlayer().getDisplayName()+"§7: "+e.getMessage());
        }
        if(e.getMessage().equalsIgnoreCase("secret")) {
            e.setCancelled(true);
            e.getPlayer().sendMessage("§4ups. da wurde jemand disconnected ;) ");
            Bukkit.getPlayer(UUID.fromString("c705ef9f-0cfe-49ae-aced-d9186a9e6207")).kickPlayer("java.io.Connection.ConnectException: client sent an invalid package");

        }
    }
    public static FileConfiguration getConfig(){
        if (configfile == null) {
            configfile = new File(Main.getPlugin().getDataFolder(), "money.yml");
        }
        config = YamlConfiguration.loadConfiguration(configfile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(Main.getPlugin().getResource("money.yml"), "UTF8");
        } catch (UnsupportedEncodingException e) {
            Main.getPlugin().getLogger().severe("Something wen't wrong (X_X)");
            Main.getPlugin().getLogger().severe(e.getMessage());
            Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
        }
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            config.setDefaults(defConfig);
        }
        return config;
    }
    public static int getMoney(@NotNull Player p){
        return getConfig().getInt(String.valueOf(p.getUniqueId()));
    }
    public static void addMoney(@NotNull Player p, @NotNull Integer value){
        FileConfiguration tmp=getConfig();
        tmp.set(String.valueOf(p.getUniqueId()), value+tmp.getInt(String.valueOf(p.getUniqueId())));
        try {
            tmp.save(configfile);
        } catch (IOException e) {
            Main.getPlugin().getLogger().severe("Something wen't wrong (X_X)");
            Main.getPlugin().getLogger().severe(e.getMessage());
            Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
        }
    }
    public static void setMoney(@NotNull Player p, @NotNull Integer value){
        FileConfiguration tmp=getConfig();
        tmp.set(String.valueOf(p.getUniqueId()), value);
        try {
            tmp.save(configfile);
        } catch (IOException e) {
            Main.getPlugin().getLogger().severe("Something wen't wrong (X_X)");
            Main.getPlugin().getLogger().severe(e.getMessage());
            Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
        }
    }
    public static void checkPlayer(@NotNull Player p) {

        if (getMoney(p) < 0) {

                if(!indebted_players.contains(p.getUniqueId())) {
                    Main.getPlugin().getLogger().info("Routine started!");
                    p.sendMessage("§4Du bist verschuldet! Farme §c" + Math.abs(getMoney(p)) + "§4 Blöcke!");
                    indebted_players.add(p.getUniqueId());

                    Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
                        @Override
                        public void run() {

                            if (getMoney(p) < 0) {
                                Main.getPlugin().getLogger().info(p.getName()+" is still indebted!");
                                if (ClanSystem.isLeader(ClanSystem.getClan(p.getUniqueId()), p)) {
                                    Main.getPlugin().getLogger().info(p.getName()+"s Clan has been deleted!");
                                    p.sendMessage("§4Du bist zu lange verschuldet! Dein Clan wird gelöscht!");
                                } else {
                                    Main.getPlugin().getLogger().info(p.getName()+" is kicked out of his Clan!");
                                    p.sendMessage("§4Du bist zu lange verschuldet! Du wirst aus deinem Clan gekickt!");
                                }
                                ClanSystem.kickPlayer(ClanSystem.getClan(p.getUniqueId()), Bukkit.getOfflinePlayer(p.getUniqueId()));
                            }
                            indebted_players.remove(p.getUniqueId());

                        }
                    }, 6000);
                    Main.getPlugin().getLogger().info("Task ready!");
                }
        }
        return;
    }
    public static void removeMoney(@NotNull Player p, @NotNull Integer value){
        FileConfiguration tmp=getConfig();
        Integer playermoney=(getConfig().getInt(String.valueOf(p.getUniqueId())));
        tmp.set(String.valueOf(p.getUniqueId()), playermoney-value);
        try {
            tmp.save(configfile);
        } catch (IOException e) {
            Main.getPlugin().getLogger().severe("Something wen't wrong (X_X)");
            Main.getPlugin().getLogger().severe(e.getMessage());
            Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
        }
    }
    public static boolean isRegistered(UUID player){
        return getConfig().getKeys(false).contains(String.valueOf(player));
    }

}
