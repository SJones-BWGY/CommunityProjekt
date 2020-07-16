package com.bwgy.abilities;

import com.bwgy.main.Main;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.UUID;

public class Config {
    private static FileConfiguration config;
    private static File configfile;
    public static FileConfiguration getConfig(){
        if (configfile == null) {
            configfile = new File(Main.getPlugin().getDataFolder(), "abilities.yml");
        }
        config = YamlConfiguration.loadConfiguration(configfile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(Main.getPlugin().getResource("abilities.yml"), "UTF8");
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
    public static int getMobTakers(UUID player){
        return getConfig().getInt(String.valueOf(player)+".mobtakers");
    }
    public static void addMobTaker(UUID player, int count){
        FileConfiguration tmp=getConfig();
        tmp.set(String.valueOf(player)+".mobtakers",getMobTakers(player)+count);
        try {
            tmp.save(configfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void removeMobTakers(UUID player, int count){
        FileConfiguration tmp=getConfig();
        tmp.set(String.valueOf(player)+".mobtakers",getMobTakers(player)-count);
        try {
            tmp.save(configfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
