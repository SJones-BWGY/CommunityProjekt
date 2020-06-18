package com.bwgy.commands;

import com.bwgy.WorldManagement.WorldMgr;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ResetMap implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
        if(sender.hasPermission("cmp.resetmap")){
            if(args.length==0){
                sender.sendMessage("§cGebe §e/reset confirm §cein!");
            }else if (args.length==1&&args[0].equalsIgnoreCase("confirm")){
                WorldMgr.resetFarmWorld();
                sender.sendMessage("§cDie §eFarmwelt§c wurde zurückgesetzt!");
            }else{
                sender.sendMessage("§cUngültiger Syntax!");
            }
        }else{
            sender.sendMessage("§cDazu hast du keine Rechte!");
        }
        return false;
    }
}
