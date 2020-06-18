package com.bwgy.main;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
        e.setJoinMessage("§8[§a+§8] §7"+e.getPlayer().getName());
    }
}
