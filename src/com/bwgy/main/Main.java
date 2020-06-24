package com.bwgy.main;

import com.bwgy.WorldManagement.WorldMgr;
import com.bwgy.clansystem.EconomySystem;
import com.bwgy.commands.ClanMgr;
import com.bwgy.commands.ResetMap;
import com.bwgy.commands.WorldCommand;
import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main plugin;
    public void onEnable(){
        plugin=this;
        WorldCreator farm=new WorldCreator("farmwelt");
        farm.createWorld();
        this.saveDefaultConfig();
        Bukkit.getPluginCommand("reset").setExecutor(new ResetMap());
        Bukkit.getPluginCommand("world").setExecutor(new WorldCommand());
        Bukkit.getPluginCommand("clan").setExecutor(new ClanMgr());
        Bukkit.getPluginCommand("ban").setExecutor(new BanSystem());
        Bukkit.getPluginManager().registerEvents(new AntiBuildListener(),this);
        Bukkit.getPluginManager().registerEvents(new JoinListener(),this);
        Bukkit.getPluginManager().registerEvents(new EconomySystem(),this);
        WorldMgr.FarmWorld=Bukkit.getWorld("farmwelt");
        WorldMgr.FarmWorldFolder=WorldMgr.FarmWorld.getWorldFolder();
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
            @Override
            public void run() {
                Main.getPlugin().getLogger().info("Refreshing Economy");
                for(Player p:Bukkit.getOnlinePlayers()){

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
