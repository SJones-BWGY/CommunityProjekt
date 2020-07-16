package com.bwgy.menu;

import com.bwgy.utils.GUIApi;
import com.mojang.authlib.BaseUserAuthentication;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;
import java.util.ArrayList;

public class GUI {

    public static void openGUI(Player p){
        GUIApi gui=new GUIApi("§9§lMenü",27);

    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){

    }
}
