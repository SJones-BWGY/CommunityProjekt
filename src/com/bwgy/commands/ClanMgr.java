package com.bwgy.commands;

import com.bwgy.clansystem.Config;
import com.bwgy.clansystem.EconomySystem;
import com.bwgy.clansystem.PlayerManagement;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ClanMgr implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        if(args.length==2){

                if(args[0].equalsIgnoreCase("create")) {
                    if(sender instanceof Player) {

                        Player p = (Player) sender;

                            Integer code = Config.createClan(args[1], p);
                            if (code == 0) {
                                EconomySystem.removeMoney(p, 100000);
                                p.sendMessage("§4 -100000€");
                                p.sendMessage("§2§cDer Clan §r§e" + args[1].toUpperCase() + " §2§cwurde erstellt!");
                            } else if (code == 1) {
                                p.sendMessage("§4[Fehler] §cDieser Clan existiert bereits!");
                            } else if (code == 3) {
                                p.sendMessage("§4[Fehler] §cDu bist bereits in einem Clan!");
                            } else {
                                p.sendMessage("§4[Fehler] §cEin unbekannter Fehler ist aufgetreten: " + code);
                            }

                    }else{
                        sender.sendMessage(ChatColor.DARK_RED+"Du musst ein Spieler sein!");
                    }
                }else{
                    sender.sendMessage(ChatColor.RED+"Ungültiger Syntax!");
                }

        }else{
            sender.sendMessage(ChatColor.RED+"Ungültiger Syntax!");
        }
        return false;
    }
}
