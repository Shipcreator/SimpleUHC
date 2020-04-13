package com.Emile2250.SimpleUHC;

import com.Emile2250.SimpleUHC.Commands.CommandHandler;
import com.Emile2250.SimpleUHC.Listeners.LeaveEvent;
import com.Emile2250.SimpleUHC.Listeners.UHCDeath;
import com.Emile2250.SimpleUHC.Listeners.UHCPvp;
import com.Emile2250.SimpleUHC.UHC.Game;
import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SimpleUHC extends JavaPlugin {

    private static File settingsFile;
    private static FileConfiguration settingsConfig;
    private static ArrayList<Game> games;
    private static SimpleUHC instance;

    public void onEnable() {

        // Variable setup

        createSettingsConfig();
        games = new ArrayList<>();
        instance = this;
        deleteWorlds();

        // Commands

        getCommand("uhc").setExecutor(new CommandHandler()); // Hub to navigate sub commands to appropriate class

        // Events

        Bukkit.getPluginManager().registerEvents(new LeaveEvent(), this);
        Bukkit.getPluginManager().registerEvents(new UHCDeath(), this);
        Bukkit.getPluginManager().registerEvents(new UHCPvp(), this);
    }

    // Getters

    public static FileConfiguration getSettings() {
        return settingsConfig;
    }

    public static ArrayList<Game> getGames() {
        return games;
    }

    public static SimpleUHC getInstance() {
        return instance;
    }

    // Other stuff?

    public static void saveSettings() {
        try {
            settingsConfig.save(settingsFile);
        } catch (IOException e) {
            System.out.println("Uh oh! You had an issue saving your settings configuration.");
            e.printStackTrace();
        }
    }

    private void createSettingsConfig() {
        settingsFile = new File(getDataFolder(), "settings.yml"); // Initializes the settingsFile

        if (!settingsFile.exists()) { // Checks if the file exists
            settingsFile.getParentFile().mkdirs(); // Creates the directories if they don't exist
            saveResource("settings.yml", false); // Saves the settings.yml file without replacing it.
        }

        settingsConfig = new YamlConfiguration(); // Initializes the base config object.
        try {
            settingsConfig.load(settingsFile); // Actually tries to load the configuration
        } catch (IOException | InvalidConfigurationException e) {
            System.out.println("Uh oh! Your configuration loaded incorrectly.");
            e.printStackTrace();
        }
    }

    private void deleteWorlds() {
        String[] directories = this.getServer().getWorldContainer().list(); // Grabs a list of all files in the server

        if (directories != null) {
            for (String folder : directories) { // Loops through all the files
                if (folder.contains("UHC-")) { // Checks if we had any left over UHC crashes in case of shutdown mid game
                    try {
                        FileUtils.deleteDirectory(new File(this.getServer().getWorldContainer(), folder)); // Tries to delete the world.
                    } catch (IOException e) {
                        System.out.println("Oh no! We had an issue deleting left over worlds");
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
