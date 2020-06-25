package com.bwgy.main;

import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

public class Config {
    public static FileConfiguration getConfig(){
        Main.getPlugin().saveDefaultConfig();
        Main.getPlugin().getConfig().options().copyDefaults(true);
        Main.getPlugin().reloadConfig();
        return Main.getPlugin().getConfig();
    }
    public static Boolean isInPvpArena(Location loc) {
        Location maxlocation = new Location(Bukkit.getWorld("world"), getConfig().getInt("pvparena.firstx"), getConfig().getInt("pvparena.firsty"), getConfig().getInt("pvparena.firstz"));
        Location minlocation = new Location(Bukkit.getWorld("world"), getConfig().getInt("pvparena.secondx"), getConfig().getInt("pvparena.secondy"), getConfig().getInt("pvparena.secondz"));
        return LocationUtils.isInside(maxlocation, minlocation, loc);
    }
}
