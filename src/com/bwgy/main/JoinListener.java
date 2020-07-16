package com.bwgy.main;

import com.bwgy.clansystem.EconomySystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinListener implements Listener {
    @EventHandler
    public void onLogin(PlayerLoginEvent e){
        if(BanSystem.getBanned(e.getPlayer())){
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER,"§cDu wurdest vom Projekt §c§nausgeschlossen!§r §4Grund: "+BanSystem.getBanReason(e.getPlayer()));
        }
    }
    @EventHandler
    public void onLeave(PlayerQuitEvent e){
        e.setQuitMessage("§8[§4-§8]§7 "+e.getPlayer().getName());
    }
    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        if(e.getPlayer().getWorld().getName().equalsIgnoreCase("farmwelt")){
            e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
        }
        if(!EconomySystem.isRegistered(e.getPlayer().getUniqueId())){
            EconomySystem.addMoney(e.getPlayer(),1000);
            e.getPlayer().sendMessage("§aDu hast §e1000€"+"§a als Startguthaben erhalten!");
        }
        e.setJoinMessage("§8[§a+§8] §7"+e.getPlayer().getName());
    }
    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent e){
        int sleeping=0;
        int players=0;
        for(Player p:Bukkit.getOnlinePlayers()){
            if(p.getWorld()==e.getPlayer().getWorld()){
                players+=1;
                if(p.isSleeping()) {
                    sleeping += 1;
                }

            }
        }
        if(e.getBedEnterResult()== PlayerBedEnterEvent.BedEnterResult.OK) {
            sleeping+=1;
            if (sleeping >= players / 2) {

                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage("§aDa §e" + sleeping + "§e/" + players + "§a Spielern schlafen wurde die Nacht übersprungen!");
                }
                e.getPlayer().getWorld().setTime(0);
            } else {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage("§aEs werden noch §e" + ((players / 2) - sleeping) + " §aSpieler benötigt, damit die Nacht übersprungen wird!");
                }
            }
        }
    }
}
