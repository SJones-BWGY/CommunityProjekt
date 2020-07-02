package com.bwgy.clansystem;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;

import com.bwgy.main.Main;
import com.sun.corba.se.spi.ior.ObjectKey;
import com.sun.istack.internal.NotNull;
import com.sun.org.apache.xpath.internal.operations.Bool;
import io.netty.util.internal.IntegerHolder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Config {
    private static FileConfiguration config;
    private static File configfile;
    public static FileConfiguration getConfig(){
        if (configfile == null) {
            configfile = new File(Main.getPlugin().getDataFolder(), "clans.yml");
        }
        config = YamlConfiguration.loadConfiguration(configfile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(Main.getPlugin().getResource("clans.yml"), "UTF8");
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
    public static Integer createClan(@NotNull String name, @NotNull Player leader){
        Integer code=99;
        Main.getPlugin().getLogger().info("Checking if the clan already exists...");
        if(PlayerManagement.getClan(leader.getUniqueId())==null){
            if(getConfig().getConfigurationSection("clan."+name.toUpperCase())==null) {

                Main.getPlugin().getLogger().info("Setting up the config...");
                FileConfiguration tmp_cfg = getConfig();
                tmp_cfg.set("clan." + name.toUpperCase() + ".players." + String.valueOf(leader.getUniqueId()) + ".rank", "CREATOR");
                try {
                    tmp_cfg.save(configfile);
                    PlayerManagement.setClan(leader.getUniqueId(), name);
                    code = 0;
                } catch (IOException e) {
                    code = 2;
                }
                Main.getPlugin().getLogger().info("Config created and saved!");
            }else{
                code=1;
            }
        }else{
            code=3;
        }

        return code;

    }
    public static Boolean isLeader(String clan,Player player){
        return getConfig().getString("clan."+clan.toUpperCase()+".players."+String.valueOf(player.getUniqueId()+".rank")).equalsIgnoreCase("CREATOR")||getConfig().getString("clan."+clan.toUpperCase()+".players."+String.valueOf(player.getUniqueId()+".rank")).equalsIgnoreCase("LEADER");
    }
    public static Boolean isUnranked(String clan,Player player){
        if(getConfig().get(("clan."+clan.toUpperCase()+".players.")+".rank")=="MEMBER"){
            return true;
        }else{
            return false;
        }
    }
    public static Boolean isModerator(String clan,Player player){
        if(getConfig().get("clan."+clan.toUpperCase()+"."+player.getName()+".rank")=="MODERATOR"){
            return true;
        }else{
            return false;
        }
    }
    public static Boolean isInClan(String clan, Player player){
        Boolean value=false;
        if(getConfig().getConfigurationSection(("clan."+clan.toUpperCase()+".players.")).contains(String.valueOf(player.getUniqueId()))){
            value=true;
        }
        return value;
    }
    public static Integer kickPlayer(String clan, OfflinePlayer player){
        Integer code=0;
        if(getConfig().getConfigurationSection(("clan."+clan.toUpperCase()+".players.")).getKeys(true).contains(String.valueOf(player.getUniqueId()))){
            if(getConfig().getString(("clan."+clan.toUpperCase()+".players."+player.getUniqueId())+".rank").equalsIgnoreCase("CREATOR")) {
                FileConfiguration tmp=getConfig();
                tmp.set(("clan."+clan.toUpperCase()), null);
                PlayerManagement.kickOutOfClan(player.getUniqueId());
                try {
                    tmp.save(configfile);
                } catch (IOException e) {
                    Main.getPlugin().getLogger().severe("Something wen't wrong (X_X)");
                    Main.getPlugin().getLogger().severe(e.getMessage());
                    Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
                }
            }else{
                code=2;
            }
        }else{
            code=1;
        }
        return code;
    }
    public static Integer promotePlayer(String clan, Player player){
        Integer code=0;
        if(getConfig().getConfigurationSection(("clan."+clan.toUpperCase()+".players.")).contains(player.getName())){
            if(getConfig().getString(("clan."+clan.toUpperCase()+".players.")).equalsIgnoreCase("CREATOR")||getConfig().getString("clans."+clan.toUpperCase()+"."+player.getName()).equalsIgnoreCase("LEADER")) {
                if(getConfig().getString(("clan."+clan.toUpperCase()+".players.")).equalsIgnoreCase("MEMBER")){
                    getConfig().set(("clan."+clan.toUpperCase()+".players.")+".rank","MODERATOR");
                }else if(getConfig().getString("clans."+clan.toUpperCase()+"."+player.getName()+".rank").equalsIgnoreCase("MODERATOR")){
                    getConfig().set(("clan."+clan.toUpperCase()+".players.")+".rank","LEADER");
                }else{
                    Main.getPlugin().getLogger().severe("Something really weired happened trying to promote "+player.getName()+"!");
                    Main.getPlugin().getLogger().severe("We don't know what to do :/");
                    Main.getPlugin().getLogger().severe(" Maybe your clan config is broken or something like this...");
                    Main.getPlugin().getLogger().severe(getConfig().saveToString());
                    code=1;
                }
            }else{
                code=2;
            }
        }else{
            code=1;
        }
        return code;
    }
    public static List<String> getClanMembers(String clan){
        List<String> value = null;
        for (Object obj:getConfig().getConfigurationSection("clans."+clan+".players").getKeys(false).toArray()){
            if(obj instanceof String){
                value.add((String)obj);
            }
            
        }
        return value;

    }
    public static String getClan(UUID playeruid) {
        return PlayerManagement.getClan(playeruid);
    }
    public static boolean hasClan(Player player){
        return PlayerManagement.getClan(player.getUniqueId())!=null;
    }
    public static Integer getPoints(String clan){
        return getConfig().getInt("clan."+clan+".points");
    }
    public static void addPoints(String clan, Integer val){
        FileConfiguration tmp=getConfig();
        tmp.set("clan."+clan+".points",(getConfig().getInt("clan."+clan+".points")+val));
        try {
            tmp.save(configfile);
        } catch (IOException e) {
            Main.getPlugin().getLogger().severe("Something wen't wrong (X_X)");
            Main.getPlugin().getLogger().severe(e.getMessage());
            Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
        }
    }
    public static void removePoints(String clan, Integer val){
        FileConfiguration tmp=getConfig();
        tmp.set("clan."+clan+".points",getConfig().getInt("clan"+clan+".points")-val);
        try {
            tmp.save(configfile);
        } catch (IOException e) {
            Main.getPlugin().getLogger().severe("Something wen't wrong (X_X)");
            Main.getPlugin().getLogger().severe(e.getMessage());
            Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
        }
    }
    public static void setClan(UUID player,String clan){
        FileConfiguration tmp=getConfig();
        tmp.set("clan."+clan+".players."+String.valueOf(player)+".rank","MEMBER");
        try {
            tmp.save(configfile);
        } catch (IOException e) {
            Main.getPlugin().getLogger().severe("Something wen't wrong (X_X)");
            Main.getPlugin().getLogger().severe(e.getMessage());
            Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
        }
    }


}
