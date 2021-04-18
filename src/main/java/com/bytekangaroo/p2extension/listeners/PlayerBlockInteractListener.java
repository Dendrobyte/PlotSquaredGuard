package com.bytekangaroo.p2extension.listeners;

import com.bytekangaroo.p2extension.Main;
import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.plot.Plot;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;
import java.util.UUID;

public class PlayerBlockInteractListener implements Listener {

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
        String worldName = interactedLoc.getWorld().getName();
        // TODO: Do a general check to see if a player is in a plot, versus checking the worlds. (Temporary plots?) Works otherwise.

        // Check if the plot is in one of the worlds that should be checked
        List<String> worldNames = config.getStringList("plot-worlds");
        if(!worldNames.contains(worldName)){
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
            } else {
                continue;
            }
        }

        // Grab the plot object based on player's current
        com.plotsquared.core.location.Location plotLocation = new com.plotsquared.core.location.Location(worldName, interactedLoc.getBlockX(), interactedLoc.getBlockY(), interactedLoc.getBlockZ());
        Plot plot = Plot.getPlot(plotLocation);

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
        }

    }

}
