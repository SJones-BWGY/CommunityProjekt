package com.bwgy.commands;

import com.bwgy.clansystem.EconomySystem;
import com.sun.istack.internal.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class PayCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String lbl, @NotNull String[] args) {
        if(sender instanceof Player) {
            Player p = (Player) sender;
            if(args.length==2){
                if(Bukkit.getPlayer(args[0])!=null){
                    if(Integer.valueOf(args[1])!=null) {
                        EconomySystem.removeMoney(p,Integer.valueOf(args[1]));
                        EconomySystem.addMoney(Bukkit.getPlayer(args[0]),Integer.valueOf(args[1]));
                        p.sendMessage("§e"+args[1]+"§e€§a wurden an §e"+args[0]+"§a überwiesen!");
                    }else{
                        p.sendMessage("§4"+args[1]+"§c konnte nicht als gültige Zahl  erkannt werden!");
                    }
                }
            }else{
                p.sendMessage("§cUngültiger Syntax!");
            }
        }
        return false;
    }
}
