package com.bwgy.abilities;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AbilitiesCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String lbl, String[] args) {
        if(args.length==0){
            if(sender instanceof Player){
                Player p=(Player)sender;
                GUI.openGUI(p);

            }else{
                sender.sendMessage("§4§l[X]§c Du musst ein Spieler sein!");
            }
        }else{
            sender.sendMessage("§4§l[X]§c Ungültiger Syntax!");
        }
        return false;
    }
}
