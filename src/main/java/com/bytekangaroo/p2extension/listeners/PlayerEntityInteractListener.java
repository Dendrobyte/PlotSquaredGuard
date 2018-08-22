package com.bytekangaroo.p2extension.listeners;

import com.bytekangaroo.p2extension.Main;
import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.List;
import java.util.UUID;

/**
 * Created by Mark on 8/22/2018
 * Written for project PlotSquaredGuard
 * Please do not use or edit this code unless permission has been given (or if it's on GitHub...)
 * Contact me on Twitter, @Mobkinz78, with any questions
 * §
 */
public class PlayerEntityInteractListener implements Listener {

    private PlotAPI plotAPI = Main.getPlotAPI();
    private String prefix = Main.getInstance().getPrefix();
    private FileConfiguration config = Main.getInstance().getConfig();

    @EventHandler
    public void playerInteractWithEntity(PlayerInteractEntityEvent event){
        if(event.getHand() == EquipmentSlot.OFF_HAND){
            return;
        }
        Player player = event.getPlayer();
        UUID playerID = player.getUniqueId();
        Entity entity = event.getRightClicked();
        Location location = entity.getLocation();

        System.out.println("*yeet*");
        System.out.println(entity.getType().toString());
        /* Yes I copied this from the other event class... Should make it have a single location? */
        Plot plot = plotAPI.getPlot(location);
        List<String> worldNames = config.getStringList("plot-worlds");
        if(!worldNames.contains(location.getWorld().getName())){
            // If the world is NOT in the configuration, then don't bother checking the event.
            return;
        } // Otherwise, continue onwards!

        // Get the prohibited entities from the configuration.
        List<String> prohibitedEntities = config.getStringList("prohibit-interact-entities");
        String entityType = entity.getType().toString();

        boolean isProhibited = false; // Change this if the interacted item is in the list
        for(String type : prohibitedEntities){
            if(entityType.equals(type)){
                isProhibited = true;
            } else {
                continue;
            }
        }

        // Entity is prohibited to be interacted with
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
            player.sendMessage(prefix + "You can not interact with that entity if you are not part of this plot!");
            Location plotSide = new Location(Bukkit.getWorld(plot.getSide().getWorld()), plot.getSide().getX(), plot.getSide().getY(), plot.getSide().getZ());
            player.teleport(plotSide);
        }

    }
}
