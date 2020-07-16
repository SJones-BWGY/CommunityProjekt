package com.bwgy.commands;

import com.bwgy.WorldManagement.WorldMgr;
import com.bwgy.main.Config;
import com.bwgy.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WorldCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {

            if(args.length==1){
                if(sender instanceof Player){
                    if(Bukkit.getWorld(args[0])!=null){
                        if(!Config.getConfig().getList("blockedworlds").contains(args[0])) {
                            ((Player) sender).teleport(Bukkit.getWorld(args[0]).getSpawnLocation());
                            sender.sendMessage("§cDu wurdest in die Welt §e" + args[0] + "§c gesendet!");
                        }else{
                            sender.sendMessage("§4Diese Welt ist blockiert!");
                        }
                    }else{
                        sender.sendMessage("§cDie Welt §e"+args[0]+" §c existiert nicht!");
                    }
                }else{
                    sender.sendMessage("§cBenutze §e/world [Spieler] <Welt>");
                }
            }else if (args.length==2){
                if(Bukkit.getPlayer(args[0])!=null){
                    if(Bukkit.getWorld(args[1])!=null){
                        if(!Config.getConfig().getList("blockedworlds").contains(args[1])) {
                            Bukkit.getPlayer(args[0]).teleport(Bukkit.getWorld(args[1]).getSpawnLocation());

                            sender.sendMessage("§e" + args[0] + "§c wurde in die Welt§e " + args[1] + "§c telportiert!");
                        }else{
                            sender.sendMessage("§4Diese Welt ist blockiert!");
                        }
                    }else{
                        sender.sendMessage("§cDie Welt §e"+args[0]+" §c existiert nicht!");
                    }
                }else{
                    sender.sendMessage("§cDer Spieler §e"+args[0]+" §ckonnte nicht gefunden werden!");
                }
            }else{
                sender.sendMessage("§cUngültiger Syntax! Benutze: §e/world [Spieler] <Welt>§c!");
            }

        return false;
    }
}
