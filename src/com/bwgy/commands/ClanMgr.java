package com.bwgy.commands;

import com.bwgy.clansystem.ChunkClaimer;
import com.bwgy.clansystem.ClanSystem;
import com.bwgy.clansystem.EconomySystem;
import com.bwgy.clansystem.PlayerManagement;
import com.bwgy.main.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import sun.font.DelegatingShape;

import java.util.UUID;

public class ClanMgr implements CommandExecutor {
    public static void sendHelp(Player p) {
        p.sendMessage("§aClan-System Hilfe");
        p.sendMessage("§e/clan create <Name> §a-> §eErstellt den Clan <Name> (100000€ benötigt).");
        p.sendMessage("§e/clan claimchunk §a-> §eClaimt den aktuellen Chunk. (1000P benötigt).");
        p.sendMessage("§e/clan invite <Spieler> §a-> §eInvited den Spieler <Spieler>.");
        p.sendMessage("§e/clan kick <Spieler> §a-> §eKickt den Spieler <Spieler> aus deinem Clan.");
        p.sendMessage("§e/clan accept §a-> Nimmt die aktuelle Clananfrage an.");
        p.sendMessage("§e/clan promote <Spieler> §a-> Erhöht den Rang von <Spieler>");
        p.sendMessage("§e/cc <Nachricht> §a-> §eSendet <Nachricht> in den Clanchat.");
    }
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args){
        if(cmd.getName().equalsIgnoreCase("clan")) {
            if (args.length == 2) {

                if (args[0].equalsIgnoreCase("create")) {
                    if (sender instanceof Player) {

                        Player p = (Player) sender;
                        if (EconomySystem.getMoney(p) >= 100000) {
                            Integer code = ClanSystem.createClan(args[1], p);
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
                        } else {
                            p.sendMessage("§4[Fehler] §cDazu hast du zu wenig Geld!");
                        }

                    } else {
                        sender.sendMessage(ChatColor.DARK_RED + "Du musst ein Spieler sein!");
                    }
                } else if (args[0].equalsIgnoreCase("invite")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (ClanSystem.getClan(p.getUniqueId()) != null) {
                            if(ClanSystem.isModerator(ClanSystem.getClan(p.getUniqueId()),p)||ClanSystem.isLeader(ClanSystem.getClan(p.getUniqueId()),p)) {
                                if (Bukkit.getPlayer(args[1]) != null) {
                                    PlayerManagement.inviteToClan(Bukkit.getPlayer(args[1]).getUniqueId(), ClanSystem.getClan(p.getUniqueId()));
                                } else {
                                    p.sendMessage("§4[Fehler] §cDer Spieler §e" + args[1] + "§c konnte nicht gefunden werden!");
                                }
                            }else{
                                p.sendMessage("§4[Fehler] §cDazu hast du leide keine Rechte :/");
                            }
                        } else {
                            p.sendMessage("§4[Fehler] §cDu musst in einem Clan sein!");
                        }
                    } else {
                        sender.sendMessage(ChatColor.DARK_RED + "Du musst ein Spieler sein!");
                    }
                } else if (args[0].equalsIgnoreCase("leave") && args[1].equalsIgnoreCase("confirm")) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (ClanSystem.getClan(p.getUniqueId()) != null) {
                            if (ClanSystem.getConfig().getString("clan."+ClanSystem.getClan(p.getUniqueId())+".players."+String.valueOf(((Player)sender).getUniqueId())+".rank").equals("CREATOR")) {
                                String clan = ClanSystem.getClan(p.getUniqueId());
                                ClanSystem.kickPlayer(ClanSystem.getClan(p.getUniqueId()), p);
                                p.sendMessage("§4Dein Clan, §c" + clan + "§4 wurde gelöscht!");

                            } else {
                                ClanSystem.kickPlayer(ClanSystem.getClan(p.getUniqueId()), p);
                                p.sendMessage("§4Du hast deinen Clan verlassen.");
                            }
                        } else {
                            p.sendMessage("§4[Fehler] §cIch weiß nicht, welchen Clan du verlassen willst...");
                        }

                    }
                } else if (args[0].equalsIgnoreCase("kick")) {
                    if (sender instanceof Player) {
                        if (Bukkit.getOfflinePlayer(args[1]) != null) {
                            if(ClanSystem.isModerator(ClanSystem.getClan(((Player)sender).getUniqueId()), (Player)sender)||ClanSystem.isLeader(ClanSystem.getClan(((Player)sender).getUniqueId()), (Player)sender)){
                                if (ClanSystem.getClan(Bukkit.getOfflinePlayer(args[1]).getUniqueId()).equals(ClanSystem.getClan(((Player) sender).getUniqueId()))) {
                                    if(ClanSystem.isModerator(ClanSystem.getClan(((Player)sender).getUniqueId()),((Player)sender))) {
                                        if(!ClanSystem.isLeader(ClanSystem.getClan(((Player)sender).getUniqueId()),((Player)sender))) {
                                            ClanSystem.kickPlayer(ClanSystem.getClan(((Player) sender).getUniqueId()), Bukkit.getOfflinePlayer(args[1]));
                                            sender.sendMessage("§aDer Spieler §e" + args[1] + "§a wurde gekickt!");
                                        }else{
                                            sender.sendMessage("§4[Fehler] §cDieser Spieler hat einen zu hohen Rang!");
                                        }
                                    }else{
                                        if(!ClanSystem.getConfig().getString("clan."+ClanSystem.getClan(((Player)sender).getUniqueId())+".players."+String.valueOf(Bukkit.getOfflinePlayer(args[1]).getUniqueId())+".rank").equals("CREATOR")) {
                                            ClanSystem.kickPlayer(ClanSystem.getClan(((Player) sender).getUniqueId()), Bukkit.getOfflinePlayer(args[1]));
                                            sender.sendMessage("§aDer Spieler §e" + args[1] + "§a wurde gekickt!");
                                        }else{
                                            sender.sendMessage("§4[Fehler] §cDieser Spieler hat einen zu hohen Rang!");
                                        }
                                    }
                                } else {
                                    sender.sendMessage("§4[Fehler] §cDieser Spieler ist nicht in deinem Clan.");
                                }
                            }else{
                                sender.sendMessage("§4[Fehler] §cDazu hast du keine Rechte!");
                            }
                        } else {
                            sender.sendMessage("§4Wer ist das? Den kenne ich net...");
                        }
                    }

                }else if(args[0].equalsIgnoreCase("promote")){
                    if(sender instanceof Player){
                        Player p=(Player)sender;
                        if(Bukkit.getPlayer(args[1])!=null){
                            Player target=Bukkit.getPlayer(args[1]);
                            if(ClanSystem.getClan(target.getUniqueId())!=null) {
                                if (ClanSystem.getClan(target.getUniqueId()).equals(ClanSystem.getClan(p.getUniqueId()))) {
                                    if(ClanSystem.isLeader(ClanSystem.getClan(p.getUniqueId()),p)) {
                                        int code = ClanSystem.promotePlayer(ClanSystem.getClan(target.getUniqueId()), target.getUniqueId());
                                        if (code == 0) {
                                            p.sendMessage("§aDer Spieler wurde befördert!");
                                        } else if (code == 3) {
                                            p.sendMessage("§4[Fehler]§c Dieser Spieler ist bereits Leader!");
                                        } else {
                                            p.sendMessage("§4[Fehler]§c Ein unbekannter Fehler ist aufgetreten.(§4" + code + "§c)");
                                        }
                                    }else{
                                        p.sendMessage("§4[Fehler]§c Dazu hast du keine Rechte!");
                                    }
                                }else{
                                    p.sendMessage("§4[Fehler] §cDieser Spieler ist nicht in deinem Clan!");
                                }
                            }else{
                                p.sendMessage("§4[Fehler] §cDieser Spieler ist in keinem Clan!");
                            }
                        }else{
                            p.sendMessage("§cDer Spieler §4"+args[1]+"§c konnte nicht gefunden werden!");
                        }
                    }else{
                        sender.sendMessage("ALTER VERPISS DICH!!!!!1");
                    }
                }
                else {
                    sendHelp((Player) sender);
                }

            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("accept")) {
                    if (sender instanceof Player) {
                        PlayerManagement.acceptClanInvite(((Player) sender).getUniqueId());

                    }
                }else if(args[0].equalsIgnoreCase("info")){
                    if(sender instanceof Player){
                        Player p=(Player)sender;
                        String clan=ClanSystem.getClan(p.getUniqueId());
                        if(clan!=null){
                            p.sendMessage("§aClan-Info für§e "+clan+":");
                            p.sendMessage("§aPoints: §e"+ClanSystem.getPoints(clan));

                        }else {
                            p.sendMessage("§4[Fehler]§c Du bist in keinem Clan!");
                        }
                    }else {
                        sender.sendMessage("§4Du musst ein Spieler sein!");
                    }
                } else if (args[0].equalsIgnoreCase("claimchunk")) {
                    if (sender instanceof Player) {
                        Player p=(Player)sender;
                        if(ClanSystem.isLeader(ClanSystem.getClan(p.getUniqueId()),p)||ClanSystem.isLeader(ClanSystem.getClan(p.getUniqueId()),p)) {
                            if(ChunkClaimer.getOwner(p.getLocation().getChunk())==null) {
                                if (ClanSystem.getPoints(ClanSystem.getClan(((Player) sender).getUniqueId())) > 999) {
                                    ChunkClaimer.claimChunk(ClanSystem.getClan(((Player) sender).getUniqueId()), ((Player) sender).getLocation().getChunk());
                                    ClanSystem.removePoints(ClanSystem.getClan(((Player) sender).getUniqueId()), 1000);
                                    ((Player) sender).sendMessage("§aDer Chunk wurde geclaimed!");
                                } else {
                                    ((Player) sender).sendMessage("§cDein Clan hat zu wenige Punkte! Du benötigst mindestens §41000§c!");
                                }
                            }else{
                                p.sendMessage("§4[Fehler]§c Dieser Chunk ist bereits geclaimed!");
                            }
                        }else{
                            p.sendMessage("§4[Fehler]§c Dazu hast du keine Rechte!");
                        }

                    } else {
                        sender.sendMessage("§4Du musst ein Spieler sein!");
                    }
                } else if (args[0].equalsIgnoreCase("leave")) {
                    if (sender instanceof Player) {
                        sender.sendMessage("§cBenutze: §4/clan leave confirm§c!");
                    } else {
                        sender.sendMessage("nö");
                    }
                } else if(args[0].equalsIgnoreCase("unclaimchunk")){
                    if (sender instanceof Player) {
                        Player p=(Player)sender;
                        if(ClanSystem.getClan(p.getUniqueId())!=null) {
                            if (ClanSystem.isLeader(ClanSystem.getClan(p.getUniqueId()), p) || ClanSystem.isModerator(ClanSystem.getClan(p.getUniqueId()), p)) {
                                if (ChunkClaimer.getOwner(p.getLocation().getChunk()) != null) {
                                    if (ChunkClaimer.getOwner(p.getLocation().getChunk()).equals(ClanSystem.getClan(p.getUniqueId()))) {

                                        ChunkClaimer.unclaimChunk(ClanSystem.getClan(((Player) sender).getUniqueId()), ((Player) sender).getLocation().getChunk());
                                        ClanSystem.addPoints(ClanSystem.getClan(((Player) sender).getUniqueId()), 1000);
                                        ((Player) sender).sendMessage("§aDer Claim wurde gelöscht und deinem Clan wurden §e1000P §ahinzugefügt!");

                                    } else {
                                        p.sendMessage("§4[Fehler]§c Dieser Chunk gehört nicht dir!!");
                                    }
                                } else {
                                    p.sendMessage("§4[Fehler]§c Dieser Chunk ist nicht geclaimed!");
                                }
                            } else {
                                p.sendMessage("§4[Fehler]§c Dazu hast du keine Rechte!");
                            }
                        }else{
                            p.sendMessage("§4[Fehler]§c Du bist in keinem Clan :/");
                        }
                    } else {
                        sender.sendMessage("§4Du musst ein Spieler sein!");
                    }
                } else if(args[0].equalsIgnoreCase("home") ){
                    if(sender instanceof Player){
                        Player p=(Player)sender;
                        if(ClanSystem.getClan(p.getUniqueId())!=null){
                            if(ClanSystem.getClanHome(ClanSystem.getClan(p.getUniqueId()))!=null){
                                p.teleport(ClanSystem.getClanHome(ClanSystem.getClan(p.getUniqueId())));
                                p.sendMessage("§aDu wurdest teleportiert!");
                            }else{
                                p.sendMessage("§4[Fehler]§c Dein Clan-Home ist nicht gesetzt!");
                            }
                        }else {
                            p.sendMessage("§4[Fehler] §cDu bist in keinem Clan");
                        }
                    }
                } else if(args[0].equalsIgnoreCase("sethome")){
                    if(sender instanceof Player){
                        Player p=(Player)sender;
                        if(ClanSystem.getClan(p.getUniqueId())!=null){
                            if (ClanSystem.isLeader(ClanSystem.getClan(p.getUniqueId()), p) || ClanSystem.isModerator(ClanSystem.getClan(p.getUniqueId()), p)) {
                                ClanSystem.setClanHome(ClanSystem.getClan(p.getUniqueId()),p.getLocation());
                                p.sendMessage("§aDein ClanHome wurde gesetzt!");
                            }else{
                                p.sendMessage("§4[Fehler]§c Dazu hast du keine Rechte!");
                            }
                        }else{
                            p.sendMessage("§4[Fehler]§c Du bist in keinem Clan!");
                        }
                    }
                } else {
                    sendHelp((Player) sender);
                }

            } else {
                sendHelp((Player) sender);
            }
        }else if(cmd.getName().equalsIgnoreCase("cc")){
            if(args.length==1){
                for(Player p:Bukkit.getOnlinePlayers()){
                    if(ClanSystem.getClanMembers(ClanSystem.getClan(p.getUniqueId())).contains(Bukkit.getPlayer(p.getUniqueId()))){
                        p.sendMessage("§eClan⇨ "+sender.getName()+":§7 "+args[0]);
                    }
                }
            }else{
                sendHelp((Player)sender);
            }
        }

        return false;

    }
}
