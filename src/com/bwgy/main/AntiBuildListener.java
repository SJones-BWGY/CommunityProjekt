package com.bwgy.main;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class AntiBuildListener implements Listener {


    @EventHandler
    public void onBreak(BlockBreakEvent e){
        if(Config.isInPvpArena(e.getPlayer().getLocation())) {


            if (!(e.getPlayer().hasPermission("build"))) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onPlace(BlockPlaceEvent e){
        if(Config.isInPvpArena(e.getPlayer().getLocation())) {
            if (!(e.getPlayer().hasPermission("build"))) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onDrop(PlayerDropItemEvent e){
        if(Config.isInPvpArena(e.getPlayer().getLocation())) {
            if (!(e.getPlayer().getGameMode().equals(GameMode.CREATIVE))) {
                e.setCancelled(true);
            }
        }
    }
    @EventHandler
    public void onInteract(InventoryClickEvent e){
        if(Config.isInPvpArena(e.getWhoClicked().getLocation())) {
            if (!(e.getWhoClicked().getGameMode().equals(GameMode.CREATIVE))) {
                e.setCancelled(true);
            }
        }

    }
    @EventHandler
    public void onHandItemSwap(PlayerSwapHandItemsEvent e){
        if(Config.isInPvpArena(e.getPlayer().getLocation())) {
            if (!(e.getPlayer().getGameMode().equals(GameMode.CREATIVE))) {
                e.setCancelled(true);
            }
        }

    }
    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        e.setDroppedExp(0);
        e.getEntity().getKiller();
        if(Config.isInPvpArena(e.getEntity().getLocation())) {
            e.getDrops().clear();
        }
        if(e.getEntity().getKiller()!=null){

            e.setDeathMessage("§e"+e.getEntity().getName()+"§7 wurde von §e"+e.getEntity().getKiller().getName()+" §7getötet");
            e.getEntity().getKiller().sendMessage("§7Du hast §e"+e.getEntity().getName()+"§7 getötet!");
            if(Config.isInPvpArena(e.getEntity().getLocation())) {
                e.getEntity().getKiller().setHealth(20);
                e.getEntity().getKiller().setSaturation(20);
            }
        }else{
            e.setDeathMessage("§e"+e.getEntity().getName()+" §7ist gestorben!");
        }

    }


}
