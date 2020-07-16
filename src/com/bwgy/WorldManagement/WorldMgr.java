package com.bwgy.WorldManagement;

import com.bwgy.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.entity.Player;

import java.io.File;

public class WorldMgr {
    public static World FarmWorld;
    public static File FarmWorldFolder;
    public static void resetFarmWorld(){
        FarmWorldFolder=Bukkit.getWorld("farmwelt").getWorldFolder();
        Bukkit.unloadWorld("farmwelt",false);
        for(File f:FarmWorldFolder.listFiles()){
            Main.getPlugin().getLogger().info("Deleting file "+f.getName());
            if(f.isDirectory()){
                for(File fl:f.listFiles()){
                    fl.getAbsoluteFile().delete();
                }
            }
            f.getAbsoluteFile().delete();
        }
        FarmWorldFolder.delete();
        WorldCreator farm=new WorldCreator("farmwelt");
        farm.type(WorldType.NORMAL);
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
