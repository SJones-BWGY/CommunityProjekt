package com.bwgy.main;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils {


    public static List<Location> getArea(Location pos1, Location pos2) {
        Location[] sorted = getMinMaxLocations(pos1, pos2);
        pos1 = sorted[0];
        pos2 = sorted[1];
        List<Location> locs = new ArrayList<Location>();
        for (int x = pos1.getBlockX(); x < pos2.getX(); x++) {
            for (int y = pos1.getBlockY(); y < pos2.getY(); y++) {
                for (int z = pos1.getBlockZ(); z < pos2.getZ(); z++) {
                    locs.add(new Location(pos1.getWorld(), x, y, z));
                }
            }
        }
        return locs;
    }


    public static boolean isInside(Location pos1, Location pos2, Location loc) {
        Location[] minMax = getMinMaxLocations(pos1, pos2);
        pos1 = minMax[0];
        pos2 = minMax[1];

        return pos1.getX() <= loc.getX() && pos2.getX() >= loc.getX() &&
                pos1.getY() <= loc.getY() && pos2.getY() >= loc.getY() &&
                pos1.getZ() <= loc.getZ() && pos2.getZ() >= loc.getZ();
    }


    public static Location[] getMinMaxLocations(Location pos1, Location pos2) {
        double minX = Math.min(pos1.getX(), pos2.getX());
        double minY = Math.min(pos1.getY(), pos2.getY());
        double minZ = Math.min(pos1.getZ(), pos2.getZ());

        double maxX = Math.max(pos1.getX(), pos2.getX());
        double maxY = Math.max(pos1.getY(), pos2.getY());
        double maxZ = Math.max(pos1.getZ(), pos2.getZ());

        pos1 = new Location(pos1.getWorld(), minX, minY, minZ);
        pos2 = new Location(pos2.getWorld(), maxX, maxY, maxZ);
        return new Location[] {pos1, pos2};
    }






}
