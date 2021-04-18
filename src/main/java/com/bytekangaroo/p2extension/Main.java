package com.bytekangaroo.p2extension;

import com.bytekangaroo.p2extension.listeners.PlayerBlockInteractListener;
import com.bytekangaroo.p2extension.listeners.PlayerEntityInteractListener;
import com.plotsquared.core.api.PlotAPI;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

/**
 * Created by Mark on 8/14/2018Bukk
 * Written for project PlotSquaredExtendedLimitations
 * Please do not use or edit this code unless permission has been given (or if it's on GitHub...)
 * Contact me on Twitter, @Mobkinz78, with any questions
 * §
 */
public class Main extends JavaPlugin {

    // This entire plugin pretty much relies on the fact that people CAN use on plots.
    // TODO: Add permission for admins or staff to bypass everything anyway. Perhaps make certain "groups" to be bypassed

    private String prefix = "§8[§6PlotSquaredGuard§8]§6 ";

    private static PlotAPI plotAPI;
    private static Main main;

    @Override
    public void onEnable(){
        // Get the plugin manager
        PluginManager pm = Bukkit.getServer().getPluginManager();
        final Plugin PLOTSQUARED = pm.getPlugin("PlotSquared");

        // Check for and enable PlotSquared
        if(PLOTSQUARED != null && !PLOTSQUARED.isEnabled()){
            getLogger().log(Level.WARNING, "PlotSquared Extension could not find the core PlotSquared plugin!");
            getLogger().log(Level.WARNING, "PlotSquared Extension has been disabled.");
            pm.disablePlugin(this);
            return;
        }

        plotAPI = new PlotAPI();

        // Create configuration
        createConfig();

        // Register the Main instance
        main = this;

        // Register events
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerBlockInteractListener(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerEntityInteractListener(), this);

        // Register commands
        getCommand("plotguardreload").setExecutor(new BaseCommand());

        getLogger().log(Level.INFO, "PlotSquared Extension has successfully been enabled!");
    }

    @Override
    public void onDisable(){

    }

    public void createConfig(){
        if(!getDataFolder().exists()){
            getDataFolder().mkdirs();
        }
        File file = new File(getDataFolder(), "config.yml");
        if(!file.exists()){
            getLogger().log(Level.INFO, "No configuration found for PlotSquared Extension " + getDescription().getVersion());
            saveDefaultConfig();
        } else {
            getLogger().log(Level.INFO, "Configuration found for PlotSquared Extension v" + getDescription().getVersion() + "!");
        }
    }

    public static Main getInstance() {
        return main;
    }

    public static PlotAPI getPlotAPI(){
        return plotAPI;
    }

    public String getPrefix(){
        return prefix;
    }
}
