package com.bwgy.main;

import com.bwgy.WorldManagement.WorldMgr;
import com.bwgy.clansystem.Config;
import com.bwgy.clansystem.EconomySystem;
import com.bwgy.commands.*;
import java.io.File;
import java.nio.file.Files;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main plugin;
    public void onLoad(){
        plugin=this;

    }
    public void onEnable(){
        plugin=this;
        WhitelistSystem.getConfig();
        if(!Boot.getConfig().getBoolean("nofarmworldreset")) {
            WorldCreator farm = new WorldCreator("farmwelt");
            farm.createWorld();
            WorldMgr.FarmWorld = Bukkit.getWorld("farmwelt");
            WorldMgr.FarmWorldFolder = Bukkit.getWorld("farmwelt").getWorldFolder();
            WorldMgr.resetFarmWorld();
        }else{
            Boot.getConfigfile().delete();
        }
        this.saveDefaultConfig();
        Bukkit.getPluginCommand("reset").setExecutor(new ResetMap());
        Bukkit.getPluginCommand("world").setExecutor(new WorldCommand());
        Bukkit.getPluginCommand("clan").setExecutor(new ClanMgr());
        Bukkit.getPluginCommand("ban").setExecutor(new BanSystem());
        Bukkit.getPluginCommand("pay").setExecutor(new PayCommand());
        Bukkit.getPluginCommand("coins").setExecutor(new CoinsCommand());
        Bukkit.getPluginCommand("reboot").setExecutor(new RebootCommand());
        Bukkit.getPluginManager().registerEvents(new AntiBuildListener(),this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(),this);
        Bukkit.getPluginManager().registerEvents(new EconomySystem(),this);
        Bukkit.getPluginManager().registerEvents(new WhitelistSystem(),this);
        Bukkit.getPluginManager().registerEvents(new RebootCommand(),this);
        WorldMgr.FarmWorld=Bukkit.getWorld("farmwelt");
        WorldMgr.FarmWorldFolder=WorldMgr.FarmWorld.getWorldFolder();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                for(Player p:Bukkit.getOnlinePlayers()){
                    if(Config.getClan(p.getUniqueId())==null) {
                        p.setDisplayName("§7" + p.getName());
                    }else{
                        p.setPlayerListName("§8[§e"+ Config.getClan(p.getUniqueId())+"§] §7"+p.getName());
                    }
                    EconomySystem.checkPlayer(p);

                }
            }
        },0,100);
    }
    public void onDisable(){
        for(Player p:Bukkit.getOnlinePlayers()){
            p.kickPlayer("§cDer Server startet neu!");
        }
        Bukkit.getServer().shutdown();
    }
    public static Main getPlugin(){
        return plugin;
    }
}
