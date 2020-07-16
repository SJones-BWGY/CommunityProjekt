package com.bwgy.utils;

import com.bwgy.main.Main;
import com.sun.istack.internal.NotNull;
import net.minecraft.server.v1_15_R1.Item;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class GUIApi implements Listener {
    private static List<UUID> in_menu=new ArrayList<>();
    private static String _name_="§4Default Name";
    private static int _slots_=9;
    private static HashMap<Integer,ItemStack> items =new HashMap();
    public GUIApi(String name, int slots){
        Main.getPlugin().getServer().getPluginManager().registerEvents(this,Main.getPlugin());
        _slots_=slots;
        _name_=name;
    }

    public void addOption(@NotNull String name, @NotNull Material type, @Nullable List<String> description, int slot,ItemMeta... meta){
        ItemStack stack=new ItemStack(type);
        if(meta==null){
            ItemMeta stack_meta=stack.getItemMeta();
            stack_meta.setDisplayName(name);
            stack_meta.setLore(description);
            stack.setItemMeta(stack_meta);
        }else{
            stack.setItemMeta(meta[0]);
        }
        items.put(slot,stack);
    }

    public boolean isInMenu(Player player){ return in_menu.contains(player.getUniqueId()); }

    public void open(Player player){
        in_menu.add(player.getUniqueId());
        Inventory inv= Bukkit.createInventory(null, _slots_,_name_);
        for(Integer obj:items.keySet()){
            inv.setItem(obj,items.get(obj));
        }
        player.openInventory(inv);
    }
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(isInMenu((Player) e.getPlayer())){
            in_menu.remove(e.getPlayer().getUniqueId());
        }
    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(isInMenu((Player) e.getWhoClicked())){
            e.setCancelled(true);
        }
    }
}
