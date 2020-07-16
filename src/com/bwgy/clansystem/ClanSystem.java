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

public class ClanSystem {
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
                Main.getPlugin().getLogger().info("ClanSystem created and saved!");
            }else{
                code=1;
            }
        }else{
            code=3;
        }

        return code;

    }
    public static Boolean isLeader(String clan,Player player){
        if(getConfig().getString("clan."+clan.toUpperCase()+".players."+String.valueOf(player.getUniqueId()+".rank"))!=null) {
            return getConfig().getString("clan." + clan.toUpperCase() + ".players." + String.valueOf(player.getUniqueId() + ".rank")).equalsIgnoreCase("CREATOR") || getConfig().getString("clan." + clan.toUpperCase() + ".players." + String.valueOf(player.getUniqueId() + ".rank")).equalsIgnoreCase("LEADER");
        }else{
            return false;
        }
    }
    public static Boolean isUnranked(String clan,Player player){
        if(getConfig().getString("clan."+clan.toUpperCase()+".players."+String.valueOf(player.getUniqueId()+".rank"))!=null) {
            return getConfig().getString("clan." + clan.toUpperCase() + ".players." + String.valueOf(player.getUniqueId() + ".rank")).equalsIgnoreCase("MEMBER");
        }else{
            return false;
        }
    }
    public static Boolean isModerator(String clan,Player player){
        if(getConfig().getString("clan."+clan.toUpperCase()+".players."+String.valueOf(player.getUniqueId()+".rank"))!=null) {
            return getConfig().getString("clan." + clan.toUpperCase() + ".players." + String.valueOf(player.getUniqueId() + ".rank")).equalsIgnoreCase("MODERATOR");
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
    public static Integer promotePlayer(String clan, UUID player){
        Integer code=99;
        if(getConfig().getConfigurationSection("clan."+clan+".players").getKeys(true).contains(String.valueOf(player))){
            FileConfiguration tmp=getConfig();
            if(isUnranked(clan,Bukkit.getPlayer(player))){
                tmp.set("clan."+clan+".players."+String.valueOf(player)+".rank","MODERATOR");
                try {
                    tmp.save(configfile);
                    code=0;
                    return code;
                } catch (IOException e) {
                    e.printStackTrace();
                    code=4;
                }
            }else if(isModerator(clan,Bukkit.getPlayer(player))){
                tmp.set("clan."+clan+".players."+String.valueOf(player)+".rank","LEADER");
                try {
                    tmp.save(configfile);
                    code=0;
                    return code;
                } catch (IOException e) {
                    e.printStackTrace();
                    code=4;
                }
            }else if(isLeader(clan,Bukkit.getPlayer(player))){
                code=3;
            }else{
                code=2;
            }
        }else{
            code=1;
        }
        return code;
    }
    public static Integer demotePlayer(String clan, UUID player){
        Integer code=99;
        if(getConfig().getConfigurationSection("clan."+clan+".players").getKeys(true).contains(String.valueOf(player))){
            FileConfiguration tmp=getConfig();
            if(isUnranked(clan,Bukkit.getPlayer(player))){
                code=3;
            }else if(isModerator(clan,Bukkit.getPlayer(player))){
                tmp.set("clan."+clan+".players."+String.valueOf(player)+".rank","MEMBER");
                try {
                    tmp.save(configfile);
                    code=0;
                    return code;
                } catch (IOException e) {
                    e.printStackTrace();
                    code=4;
                }
            }else if(isLeader(clan,Bukkit.getPlayer(player))){
                if(!getConfig().getString("clan."+clan+".players."+String.valueOf(player)+".rank").equals("CREATOR")){
                    tmp.set("clan."+clan+".players."+String.valueOf(player)+".rank","MODERATOR");
                    try {
                        tmp.save(configfile);
                        code=0;
                        return code;
                    } catch (IOException e) {
                        e.printStackTrace();
                        code=4;
                    }
                }else{
                    code=5;
                }
            }else{
                code=2;
            }
        }else{
            code=1;
        }
        return code;
    }
    public static List<OfflinePlayer> getClanMembers(String clan){
        List<OfflinePlayer> value = new ArrayList<>();
        for (Object obj:getConfig().getConfigurationSection("clan."+clan+".players").getKeys(false).toArray()){
            if(String.valueOf(obj)!=null){
                value.add(Bukkit.getOfflinePlayer(UUID.fromString(String.valueOf(obj))));
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
        tmp.set("clan."+clan+".points",((getPoints(clan))-val));
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
    public static void setClanHome(String clan, Location loc){
        FileConfiguration tmp=getConfig();
        tmp.set("clan."+clan+".home.x",loc.getBlockX());
        tmp.set("clan."+clan+".home.y",loc.getBlockY());
        tmp.set("clan."+clan+".home.z",loc.getBlockZ());
        try {
            tmp.save(configfile);
        } catch (IOException e) {
            Main.getPlugin().getLogger().severe("Something wen't wrong (X_X)");
            Main.getPlugin().getLogger().severe(e.getMessage());
            Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
        }
    }
    public static Location getClanHome(String clan) {
        Integer x = getConfig().getInt("clan." + clan + ".home.x");
        Integer y = getConfig().getInt("clan." + clan + ".home.y");
        Integer z = getConfig().getInt("clan." + clan + ".home.z");
        if (y!=0) {
            return new Location(Bukkit.getWorld("world"), x, y, z);
        } else {
            return Bukkit.getWorld("world").getSpawnLocation();
        }
    }


}
