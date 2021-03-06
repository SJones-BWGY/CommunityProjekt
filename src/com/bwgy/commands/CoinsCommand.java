package com.bwgy.commands;

import com.bwgy.clansystem.EconomySystem;
import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class CoinsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String lbl, @NotNull String[] args) {
        if(sender instanceof Player){
            Player p=(Player) sender;
            if(args.length==0){
                p.sendMessage("§aDein Kontostand: §e"+EconomySystem.getMoney(p)+"§e€");
            }else if(args.length==1){
                if(Bukkit.getPlayer(args[0])!=null){
                    p.sendMessage("§aKontostand von §e"+args[0]+"§a: §e"+EconomySystem.getMoney(Bukkit.getPlayer(args[0])));
                }else{
                    p.sendMessage("§cDer Spieler§4 "+args[0]+"§c konnte nicht gefunden werden!");
                }
            }
        }
        return false;
    }
}

