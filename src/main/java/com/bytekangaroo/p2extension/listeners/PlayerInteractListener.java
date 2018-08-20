package com.bytekangaroo.p2extension.listeners;

import com.bytekangaroo.p2extension.Main;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerInteractListener implements Listener {

    private PlotAPI plotAPI = Main.getPlotAPI();
    private String prefix = Main.getInstance().getPrefix();
    private FileConfiguration config = Main.getInstance().getConfig();

    @EventHandler
    public void onPlayerInteractWithOtherPlot(PlayerInteractEvent event){
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();
        Block block = event.getClickedBlock();
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK){
            return;
        }
        Location interactedLoc = block.getLocation();
        // TODO: Do a general check to see if a player is in a plot, versus checking the worlds. (Temporary plots?) Works otherwise.

        // DEBUG: System.out.println("1 - Player: " + player.getName() +"\nBlock Name/Type: " + block.getType() + "\ntoString: " + block.getType().toString());

        // Check if the plot is in one of the worlds that should be checked
        Plot plot = plotAPI.getPlot(interactedLoc);
        List<String> worldNames = config.getStringList("plot-worlds");
        if(!worldNames.contains(interactedLoc.getWorld().getName())){
            // If the world is NOT in the configuration, then don't bother checking the event.
            return;
        } // Otherwise, continue onwards!

        // Get the prohibited items from the configuration.
        List<String> prohibitedItemNames = config.getStringList("prohibit-interact-items");
        String blockName = block.getType().toString();

        boolean isProhibited = false; // Change this if the interacted item is in the list
        for(String name : prohibitedItemNames){
            if(blockName.equals(name)){
                isProhibited = true;
            }
        }

        // Item is prohibited to be interacted with
        if(isProhibited){
            // If the player interacting is an owner or is trusted, we don't have anything to worry about.
            // If it's a member, I figure they're still trusted enough. Can be updated if users request owners to be online.
            if(plot.getOwners().contains(playerID) || plot.getTrusted().contains(playerID) || plot.getMembers().contains(playerID)){
                event.setCancelled(false);
                return;
            }

            // If they are added to the plot, make sure the owner is also online (separate block for readability purposes){

            // If the player has the bypass permission, don't cancel the interact event.
            // TODO: Should I bother checking for the specific bypass perm (since this plugin technically handles all of the things...)? plots.admin.interact.other
            if(player.hasPermission("plotguard.interact.bypass") || player.isOp()){
                event.setCancelled(false);
                return;
            }

            // Cancel the event if checks could not be met.
            event.setCancelled(true);
            player.sendMessage(prefix + "You can not interact with that item if you are not part of this plot!");
            Location plotSide = new Location(Bukkit.getWorld(plot.getSide().getWorld()), plot.getSide().getX(), plot.getSide().getY(), plot.getSide().getZ());
            player.teleport(plotSide);
        }

    }

}
