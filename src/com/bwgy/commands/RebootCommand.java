package com.bwgy.commands;

import com.bwgy.main.Boot;
import com.bwgy.main.Main;
import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;


import java.io.IOException;
import java.io.File;

public class RebootCommand implements CommandExecutor, Listener {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String lbl, @NotNull String[] args) {
        if(sender.hasPermission("reboot")) {
            if (args.length == 0) {
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.kickPlayer("§cDer Server startet neu...");
                }
                Bukkit.getServer().shutdown();
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("lockdown")) {
                    FileConfiguration tmp = Boot.getConfig();
                    tmp.set("lockdown", true);
                    try {
                        tmp.save(Boot.getConfigfile());
                    } catch (IOException e) {

                    }
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.kickPlayer("§cThe Server is rebooting in lockdown mode!\n§4NO one will be able to join after the reboot!\n§4This is to protect the server and the world");
                    }
                    Bukkit.getServer().shutdown();
                } else if (args[0].equalsIgnoreCase("hardreset")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.kickPlayer("§cThe Server is running a hardreset!\n§4The Economy- and Clansystem are being reset!");
                    }
                    for (File file : Main.getPlugin().getDataFolder().listFiles()) {
                        file.delete();
                    }
                    Bukkit.getServer().shutdown();

                } else if (args[0].equalsIgnoreCase("noworldreset")) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        p.kickPlayer("§cDer Server startet neu...");
                    }
                    FileConfiguration tmp = Boot.getConfig();
                    tmp.set("nofarmworldreset", true);
                    try {
                        tmp.save(Boot.getConfigfile());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }
    //Listeners
    @EventHandler
    public void onLogin(PlayerLoginEvent e){
        if(Boot.getConfig().getBoolean("lockdown")){
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER,"§4The server is in lockdown mode!\n§cYou won't be able to join until the admin decides to unlock the Server!");
        }
    }
}
