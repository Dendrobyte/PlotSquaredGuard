package com.bytekangaroo.p2extension;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Mark on 8/22/2018
 * Written for project PlotSquaredGuard
 * Please do not use or edit this code unless permission has been given (or if it's on GitHub...)
 * Contact me on Twitter, @Mobkinz78, with any questions
 * §
 */
public class BaseCommand implements CommandExecutor {

    String prefix = Main.getInstance().getPrefix();

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(!sender.hasPermission("plotguard.reload")){
            sender.sendMessage(prefix + "Sorry, you don't have access to this command!");
            return true;
        }
        if(command.getName().equalsIgnoreCase("plotguardreload")){
            Main.getInstance().reloadConfig();
            sender.sendMessage(prefix + "Listing reloaded configuration...");
            sender.sendMessage(prefix + "Prohibited Items:");
            for(String string : Main.getInstance().getConfig().getStringList("prohibit-interact-items")){
                sender.sendMessage(ChatColor.GRAY + "- " + string);
            }
            sender.sendMessage(prefix + "Prohibited Entities:");
            for(String string : Main.getInstance().getConfig().getStringList("prohibit-interact-entities")){
                sender.sendMessage(ChatColor.GRAY + "- " + string);
            }
            sender.sendMessage(prefix + "Configuration has been reloaded!");
            return true;
        }
        return true;
    }

}
