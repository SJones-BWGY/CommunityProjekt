package com.bwgy.clansystem;

import java.io.*;
import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

import com.bwgy.main.Main;
import com.sun.istack.internal.NotNull;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.Bukkit;
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
        if(getConfig().getConfigurationSection("clan."+name.toUpperCase())==null){
            Main.getPlugin().getLogger().info("Checking if the user is already in a clan...");
            getClan(leader);
            if(getClan(leader).equals("UNKNOWN")) {
                Main.getPlugin().getLogger().info("Setting up the config...");
                FileConfiguration tmp_cfg = getConfig();
                tmp_cfg.set("clan." + name.toUpperCase() + ".players." + leader.getName() + ".rank", "CREATOR");
                try {
                    tmp_cfg.save(configfile);
                    code = 0;
                } catch (IOException e) {
                    code = 2;
                }
                Main.getPlugin().getLogger().info("Config created and saved!");
            }else{
                code=3;
            }
        }else{
            code=1;
        }
        return code;

    }
    public static Boolean isLeader(String clan,Player player){
        return getConfig().getString(("clan."+clan.toUpperCase()+".players.")+".rank").equalsIgnoreCase("CREATOR")||getConfig().getString("clan."+clan.toUpperCase()+".players."+player.getName()+".rank").equalsIgnoreCase("LEADER");
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
        if(getConfig().getConfigurationSection(("clan."+clan.toUpperCase()+".players.")).contains(player.getName())){
            value=true;
        }
        return value;
    }
    public static Integer kickPlayer(String clan, OfflinePlayer player){
        Integer code=0;
        if(getConfig().getConfigurationSection(("clan."+clan.toUpperCase()+".players.")).contains(player.getName())){
            if(getConfig().getString(("clan."+clan.toUpperCase()+".players.")+".rank").equalsIgnoreCase("CREATOR")) {
                getConfig().set(("clan."+clan.toUpperCase()+".players."), null);
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
                    code=-1;
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
        for (Object obj:getConfig().getConfigurationSection("clans."+clan).getKeys(false).toArray()){
            if(obj instanceof String){
                value.add((String)obj);
            }
            
        }
        return value;

    }
    public static String getClan(@NotNull Player player) {
        String value;
        for(Object cln : getConfig().getConfigurationSection("clan").getKeys(false).toArray()){
            String clan=String.valueOf(cln);
            Main.getPlugin().getLogger().info("For loop 1: Clan "+clan+" detected!");
            for (Object user:getConfig().getConfigurationSection("clan."+((String) clan).toUpperCase()+".players").getKeys(false).toArray()){
                String usr=String.valueOf(user);
                Main.getPlugin().getLogger().info("For loop 2: User "+usr+" detected!");
                if(UUID.fromString(usr).equals(player.getUniqueId())){
                    Main.getPlugin().getLogger().info("User found: "+usr+"!");
                    value=clan;
                    return value;
                }
            }
        }
        value="UNKNOWN";
        return value;
    }
    public static boolean hasClan(Player player){
        return getClan(player)!=null;
    }
    public static Integer getPoints(String clan){
        return getConfig().getInt("clan."+clan+".points");
    }
    public static void addPoints(String clan, Integer val){
        getConfig().set("clan."+clan+".points",getConfig().getInt("clan"+clan+".points")+val);
    }
    public static void removePoints(String clan, Integer val){
        getConfig().set("clan."+clan+".points",getConfig().getInt("clan"+clan+".points")-val);
    }
}
