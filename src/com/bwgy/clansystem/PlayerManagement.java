package com.bwgy.clansystem;

import com.bwgy.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

public class PlayerManagement {
    private static HashMap claninvites =new HashMap<UUID,String>();
    private static FileConfiguration config;
    private static File configfile;
    public static FileConfiguration getConfig(){
        if (configfile == null) {
            configfile = new File(Main.getPlugin().getDataFolder(), "playerdata.yml");
        }
        config = YamlConfiguration.loadConfiguration(configfile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(Main.getPlugin().getResource("playerdata.yml"), "UTF8");
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
    public static void registerPlayer(UUID player){
        FileConfiguration tmp=getConfig();
        tmp.set(String.valueOf(player)+".registered",true);
        tmp.set(String.valueOf(player)+".money",1000);
        tmp.set(String.valueOf(player)+".banned",false);
        tmp.set(String.valueOf(player)+".clan",null);
        tmp.set(String.valueOf(player)+".banreason","No Reason provided!");
        try {
            tmp.save(configfile);
        } catch (IOException e) {
            Main.getPlugin().getLogger().severe("Something wen't wrong (X_X)");
            Main.getPlugin().getLogger().severe(e.getMessage());
            Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());;
        }
    }
    public static boolean isRegistered(UUID player){
        return config.getBoolean(String.valueOf(player));
    }
    public static void kickOutOfClan(UUID player){
        FileConfiguration tmp=getConfig();
        tmp.set(String.valueOf(player)+".clan",null);
        try {
            tmp.save(configfile);
        } catch (IOException e) {
            Main.getPlugin().getLogger().severe("Something wen't wrong (X_X)");
            Main.getPlugin().getLogger().severe(e.getMessage());
            Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());;
        }
    }
    public static void setClan(UUID player,String clan){
        FileConfiguration tmp=getConfig();
        tmp.set(String.valueOf(player)+".clan",clan.toUpperCase());
        try {
            tmp.save(configfile);
        } catch (IOException e) {
            Main.getPlugin().getLogger().severe("Something wen't wrong (X_X)");
            Main.getPlugin().getLogger().severe(e.getMessage());
            Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());;
        }
    }
    public static String getClan(UUID player){
        if(getConfig().getString(String.valueOf(player)+".clan")==null){
            return null;
        }else{
            return getConfig().getString(String.valueOf(player)+".clan");
        }
    }
    public static void inviteToClan(UUID player, String clan){
        Bukkit.getPlayer(player).sendMessage("§aDu wurdest in den Clan §e"+clan+"§a eingeladen!");
        if(!(claninvites.containsKey(player))) {
            claninvites.put(player, clan);
        }else{
            claninvites.replace(player,clan);
        }
    }
    public static void acceptClanInvite(UUID player){
        if(claninvites.get(player)!=null){
            Config.setClan(player, String.valueOf(claninvites.get(player)));
            setClan(player, String.valueOf(claninvites.get(player)));
            Bukkit.getPlayer(player).sendMessage("§aWillkommen im §e"+getClan(player)+"§a Clan!");
        }else{
            Bukkit.getPlayer(player).sendMessage("§cLeider will dich keiner in seinem Clan haben :/");
        }
    }
}
