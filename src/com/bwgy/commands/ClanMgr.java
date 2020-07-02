package com.bwgy.commands;

import com.bwgy.clansystem.ChunkClaimer;
import com.bwgy.clansystem.Config;
import com.bwgy.clansystem.EconomySystem;
import com.bwgy.clansystem.PlayerManagement;
import org.bukkit.Bukkit;
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
                        if(EconomySystem.getMoney(p)>=100000) {
                            Integer code = Config.createClan(args[1], p);
                            if (code == 0) {
                                EconomySystem.removeMoney(p, 100000);
                                p.sendMessage("§4 -100000€");
                                p.sendMessage("§aDer Clan §r§e" + args[1].toUpperCase() + " §awurde erstellt!");
                            } else if (code == 1) {
                                p.sendMessage("§4[Fehler] §cDieser Clan existiert bereits!");
                            } else if (code == 3) {
                                p.sendMessage("§4[Fehler] §cDu bist bereits in einem Clan!");
                            } else {
                                p.sendMessage("§4[Fehler] §cEin unbekannter Fehler ist aufgetreten: " + code);
                            }
                        }else{
                            p.sendMessage("§4[Fehler] §cDazu hast du zu wenig Geld!");
                        }

                    }else{
                        sender.sendMessage(ChatColor.DARK_RED+"Du musst ein Spieler sein!");
                    }
                }else if(args[0].equalsIgnoreCase("invite")){
                    if(sender instanceof Player){
                        Player p = (Player)sender;
                        if(Config.getClan(p.getUniqueId())!=null){
                            if(Bukkit.getPlayer(args[1])!=null){
                                PlayerManagement.inviteToClan(Bukkit.getPlayer(args[1]).getUniqueId(),Config.getClan(p.getUniqueId()));
                            }else{
                                p.sendMessage("§4[Fehler] §cDer Spieler §e"+args[1]+"§c konnte nicht gefunden werden!");
                            }
                        }else{
                            p.sendMessage("§4[Fehler] §cDu musst in einem Clan sein!");
                        }
                    }else{
                        sender.sendMessage(ChatColor.DARK_RED+"Du musst ein Spieler sein!");
                    }
                }else{
                    sender.sendMessage(ChatColor.RED+"Ungültiger Syntax!");
                }

        }else if(args.length==1){
            if(args[0].equalsIgnoreCase("accept")){
                if(sender instanceof Player) {

                    PlayerManagement.acceptClanInvite(((Player) sender).getUniqueId());

                }else{
                    sender.sendMessage("§4Du musst ein Spieler sein!");
                }
            }else if(args[0].equalsIgnoreCase("claimchunk")){
                if(sender instanceof Player) {
                    if(Config.getPoints(Config.getClan(((Player)sender).getUniqueId()))>999) {
                        ChunkClaimer.claimChunk(Config.getClan(((Player) sender).getUniqueId()), ((Player) sender).getLocation().getChunk());
                        ((Player)sender).sendMessage("§aDer Chunk wurde geclaimed!");
                    }else{
                        ((Player)sender).sendMessage("§cDein Clan hat zu wenige Punkte! Du benötigst mindestens §41000§c!");
                    }
                }else{
                    sender.sendMessage("§4Du musst ein Spieler sein!");
                }
            }
        }else{
            sender.sendMessage(ChatColor.RED+"Ungültiger Syntax!");
        }
        return false;
    }
}
