package com.bwgy.main;

import com.bwgy.main.Main;
import com.bwgy.main.WhitelistMode;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;
import sun.security.pkcs.PKCS9Attribute;

import java.io.*;

public class WhitelistSystem implements CommandExecutor, Listener {
    public static WhitelistMode mode;
    private static FileConfiguration config;
    private static File configfile;
    private static String MOTD;
    public static FileConfiguration getConfig(){
        if (configfile == null) {

            configfile = new File(Main.getPlugin().getDataFolder(), "whitelist.yml");
        }
        config = YamlConfiguration.loadConfiguration(configfile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(Main.getPlugin().getResource("whitelist.yml"), "UTF8");
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
    public boolean isWhitelisted(Player p){
        if(getConfig().getInt("players."+String.valueOf(p.getUniqueId()))==0){
            return false;
        }else{
            return true;
        }
    }
    @EventHandler
    public void onServerListPing(ServerListPingEvent e){
        if(MOTD==null) {
            update();
        }
        e.setMotd(MOTD);

    }
    public static void update(){
        getConfig();
        mode=WhitelistMode.valueOf(getConfig().getString("mode").toUpperCase());
        MOTD="§4An error occured white updating the MOTD!";
            if(mode!=null) {
                if (mode.equals(WhitelistMode.OFF)) {
                    MOTD = "§c§lCommunityProjekt\n§aFree for all";
                } else if (mode.equals(WhitelistMode.ON)) {
                    MOTD = "§c§lCommunityProjekt\n§cAuthorized access only!";
                } else if (mode.equals(WhitelistMode.WAITING)) {
                    MOTD = "§c§lCommunityProjekt\n§aWhitelisting open: Waiting for " + (getConfig().getInt("waitfor") - getConfig().getConfigurationSection("players").getKeys(true).size() + "§2 players!");
                } else if (mode.equals(WhitelistMode.MAINTENANCE)) {
                    MOTD = "§c§lCommunityProjekt\n§4Maintenance mode!";
                } else {
                    MOTD = "§4An error occurred while updating the MOTD";
                }
            }else{
                MOTD = "§4An error occurred while updating the MOTD";
            }

    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        return false;
    }

}
