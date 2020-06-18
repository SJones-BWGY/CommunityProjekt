package com.bwgy.main;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.Objects;
import java.util.UUID;

public class BanSystem implements CommandExecutor {
    private static FileConfiguration config;
    private static File configfile;
    public static FileConfiguration getConfig(){
        if (configfile == null) {
            configfile = new File(Main.getPlugin().getDataFolder(), "bans.yml");
        }
        config = YamlConfiguration.loadConfiguration(configfile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(Main.getPlugin().getResource("bans.yml"), "UTF8");
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
    public static void BanPlayer(Player p,String reason){
        FileConfiguration cfg = getConfig();
        cfg.set(String.valueOf(p.getUniqueId()),reason);
        p.kickPlayer("§cDu wurdest vom Projekt §c§nausgeschlossen!§r §4Grund: "+BanSystem.getBanReason(p));
        try {
            cfg.save(configfile);
        } catch (IOException e) {
            Main.getPlugin().getLogger().severe("An error occurred while banning "+p.getName());
        }
    }
    public static void UnbanPlayer(Player p){
        FileConfiguration cfg = getConfig();
        cfg.set(String.valueOf(p.getUniqueId()),null);
        try {
            cfg.save(configfile);
        } catch (IOException e) {
            Main.getPlugin().getLogger().severe("An error occurred while banning "+p.getName());
        }
    }
    public static boolean getBanned(Player p){
        return getConfig().get(String.valueOf(p.getUniqueId()))!=null;
    }
    public static String getBanReason(Player p){
        return getConfig().getString(String.valueOf(p.getUniqueId()));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if(cmd.getName().equalsIgnoreCase("ban")) {
            if (sender.hasPermission("cmp.ban")) {
                if (args.length == 2) {
                    if (Bukkit.getPlayer(args[0]) != null) {
                        BanSystem.BanPlayer(Bukkit.getPlayer(args[0]), args[1]);
                    } else {
                        sender.sendMessage("§cDer Spieler §4" + args[0] + " §cexistiert nicht!");
                    }
                } else {
                    sender.sendMessage("§cUngültiger Syntax benutze: §4/ban <Spieler> <Grund>");
                }
            } else {
                sender.sendMessage("§cNice Try xD");
            }
        }else if(cmd.getName().equalsIgnoreCase("unban")){
            if(args.length==1){
                    if(BanSystem.getBanned((Player)Bukkit.getOfflinePlayer(args[0]))){
                        BanSystem.UnbanPlayer((Player)Bukkit.getOfflinePlayer(args[0]));
                        sender.sendMessage("§cDer Spieler wurde entbannt!");
                    }else{
                        sender.sendMessage("§cDer Spieler §4"+args[0]+" §cist nicht gebannt?");
                    }
                }else{
                    sender.sendMessage("§cIch kenne §4"+args[0]+ "§cnicht!");
                }
            }else{
                sender.sendMessage("§cUngültiger Syntax benutze: §4/unban <Spieler>");
            }

        return false;
    }
}
