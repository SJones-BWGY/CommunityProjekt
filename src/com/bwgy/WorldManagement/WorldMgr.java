package com.bwgy.WorldManagement;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.io.File;

public class WorldMgr {
    public static World FarmWorld;
    public static File FarmWorldFolder;
    public static void resetFarmWorld(){
        DisconnectPlayers();
        Bukkit.unloadWorld("farmwelt",false);
        World farmworld=WorldMgr.FarmWorld;
        Bukkit.unloadWorld("farmwelt",false);
        for(File a: WorldMgr.FarmWorldFolder.listFiles()){
            a.delete();
        }
        WorldMgr.FarmWorldFolder.delete();
        WorldCreator farm=new WorldCreator("farmwelt");
        farm.createWorld();
    }
    public static void PortToFarm(Player p){
        p.teleport(Bukkit.getWorld("farmwelt").getSpawnLocation());
    }
    public static void DisconnectPlayers(){
        for (Player p: Bukkit.getOnlinePlayers()){
            p.kickPlayer("§cDie Farmwelt wird zurückgesetzt!");
        }
    }
}
