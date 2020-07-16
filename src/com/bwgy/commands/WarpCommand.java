package com.bwgy.commands;

import com.bwgy.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WarpCommand implements CommandExecutor {
    private static FileConfiguration config;
    private static File configfile;
    public static FileConfiguration getConfig(){
        if (configfile == null) {
            configfile = new File(Main.getPlugin().getDataFolder(), "warps.yml");
        }
        config = YamlConfiguration.loadConfiguration(configfile);

        // Look for defaults in the jar
        Reader defConfigStream = null;
        try {
            defConfigStream = new InputStreamReader(Main.getPlugin().getResource("warps.yml"), "UTF8");
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
    public static Location getWarp(String warp) {
        warp=warp.toUpperCase();
        Integer x;
        Integer y;
        Integer z;
        String world;
        x=getConfig().getInt(warp+".x");
        y=getConfig().getInt(warp+".y");
        z=getConfig().getInt(warp+".z");
        world=getConfig().getString(warp+".world");
        if(x==null||y==null||z==null||world==null|| Bukkit.getWorld(world)==null){
            Main.getPlugin().getLogger().severe("Cant get Warp "+warp+"!");
            Main.getPlugin().getLogger().severe("x:"+x);
            Main.getPlugin().getLogger().severe("y:"+y);
            Main.getPlugin().getLogger().severe("z:"+z);
            Main.getPlugin().getLogger().severe("world:"+world);
            return null;
        }else{
            return new Location(Bukkit.getWorld(world),x,y,z);
        }
    }
    public static void setWarp(String warp, Location loc){
        warp=warp.toUpperCase();
        FileConfiguration tmp=getConfig();
        tmp.set(warp+".x",loc.getBlockX());
        tmp.set(warp+".y",loc.getBlockY());
        tmp.set(warp+".z",loc.getBlockZ());
        tmp.set(warp+".world",loc.getWorld().getName());
        try {
            tmp.save(configfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static List<String> getWarps(){
        List<String> value=new ArrayList<>();
        for(Object obj:getConfig().getKeys(false)){
            if(String.valueOf(obj)!=null){
                value.add(String.valueOf(obj));
            }
        }
        return value;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String lbl, String[] args) {
        if(command.getName().equalsIgnoreCase("warp")) {

            if (args.length == 1) {
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if (getWarp(args[0]) != null) {
                        p.teleport(getWarp(args[0]));
                        p.sendMessage("§a'Du wurdest teleportiert!");
                    } else {
                        p.sendMessage("§4§l[X]§r§c Dieser Warp konnte nicht gefunden werden! §4(/warps)");
                    }
                }
            }else if(args.length==2&&args[0].equalsIgnoreCase("set")){
                if (sender instanceof Player) {
                    Player p = (Player) sender;
                    if(p.hasPermission("setwarp")) {
                        setWarp(args[1], p.getLocation());
                        p.sendMessage("§aDer Warp wurde gesetzt!");
                    }else{
                        p.sendMessage("§4§l[X]§r§c Dazu hast du keine Rechte!");
                    }
                }
            }else{
                sender.sendMessage("§4§l[X]§r§c Ungültiger Syntax!");
            }
        }else if(command.getName().equalsIgnoreCase("warps")){
            sender.sendMessage("§aExistierende Warps:§e ");
            for(String obj:getWarps()) {
                sender.sendMessage("§e- "+obj);
            }
        }
        return false;
    }


}
