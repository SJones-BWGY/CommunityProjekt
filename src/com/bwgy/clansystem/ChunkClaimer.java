package com.bwgy.clansystem;

import com.bwgy.main.Main;
import org.bukkit.Chunk;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;

public class ChunkClaimer {
    private static FileConfiguration config;
    private static File configfile;
    public static FileConfiguration getConfig(){
        if (configfile == null) {
            configfile = new File(Main.getPlugin().getDataFolder(), "chunks.yml");
        }
        config = YamlConfiguration.loadConfiguration(configfile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(Main.getPlugin().getResource("chunks.yml"), "UTF8");
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
    public static boolean isClaimed(Chunk chunk){
        return (getConfig().getString(String.valueOf(chunk.getX())+"."+String.valueOf(chunk.getZ())))!=null;
    }
    public static void claimChunk(String clan, Chunk chunk){
        FileConfiguration tmp=getConfig();
        tmp.set(String.valueOf(chunk.getX())+"."+String.valueOf(chunk.getZ()),clan);
        try {
            tmp.save(configfile);
        } catch (IOException e) {
            Main.getPlugin().getLogger().severe("Something wen't wrong (X_X)");
            Main.getPlugin().getLogger().severe(e.getMessage());
            Main.getPlugin().getServer().getPluginManager().disablePlugin(Main.getPlugin());
        }
    }
    public static String getOwner(Chunk chunk){
        return (getConfig().getString(String.valueOf(chunk.getX())+"."+String.valueOf(chunk.getZ())));
    }
    public static boolean hasChunkPermission(Player player){
        if(getOwner(player.getLocation().getChunk())!=null){
            return (getOwner(player.getLocation().getChunk()).equals(Config.getClan(player.getUniqueId())));
        }else{
            return true;
        }
    }

}
