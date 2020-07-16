package com.bwgy.commands;


import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class TpaCommand implements CommandExecutor {
    HashMap<UUID, List<UUID>> requests = new HashMap<UUID, List<UUID>>();
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {

            if(args.length==1){
                if(sender instanceof Player) {
                    Player p = (Player) sender;
                    if (requests.get(p.getUniqueId()) == null) {
                        requests.put(p.getUniqueId(), new ArrayList<UUID>());
                    }
                    if (Bukkit.getPlayer(args[0]) != null) {
                        Player target = Bukkit.getPlayer(args[0]);
                        if (!requests.get(p.getUniqueId()).contains(target.getUniqueId())) {
                            if (!args[0].equals(p.getName())) {
                                List<UUID> current_tpas = requests.get(p.getUniqueId());
                                current_tpas.add(target.getUniqueId());
                                requests.replace(p.getUniqueId(), current_tpas);
                                target.sendMessage("§aDu hast eine Teleportanfrage von " + p.getName() + "§a erhalten!");
                                target.sendMessage("§aAkzeptiere mit §e/tpa accept " + p.getName());
                            } else {
                                p.sendMessage("§4§l[X]§c Du kannst dir selber keine Teleport-Anfrage schicken!");
                            }
                        } else {
                            p.sendMessage("§4§l[X]§c Du hast diesem Spieler schon eine Anfrage gesendet!");
                        }
                    } else {
                        p.sendMessage("§4§l[X]§c Dieser Spieler kann nicht gefunden werden!");
                    }
                }
            }else if(args.length==2&&args[0].equalsIgnoreCase("accept")){
                if(sender instanceof Player){
                    Player p=(Player)sender;
                    if (requests.get(p.getUniqueId()) == null) {
                        requests.put(p.getUniqueId(), new ArrayList<UUID>());
                    }
                    if(Bukkit.getPlayer(args[1])!=null){
                        Player target=Bukkit.getPlayer(args[1]);
                        if(requests.get(target.getUniqueId()).contains(p.getUniqueId())){
                            List<UUID> current=requests.get(p.getUniqueId());
                            current.remove(p.getUniqueId());
                            requests.replace(p.getUniqueId(),current);
                            p.sendMessage("§aDu hast §e"+target.getName()+"§es §aAnfrage angenommen!");
                            target.teleport(p.getLocation());

                        }else{
                            p.sendMessage("§4§l[X]§r§c Dieser Spieler hat dir keine Anfrage geschickt!");
                        }
                    }else{
                        p.sendMessage("§4§l[X]§r§c Dieser Spieler konnte nicht gefunden werden!");
                    }
                }
            }else{
                if(sender instanceof Player){
                    Player p=(Player)sender;
                    if(requests.get(p.getUniqueId())==null){
                        requests.put(p.getUniqueId(),new ArrayList<UUID>());
                    }
                p.sendMessage("§4§l[X]§r§c Ungültiger Syntax! Benutze:§4 /tpa <Spieler> oder /tpa accept <Spieler>");
            }}
            return false;
    }
}
