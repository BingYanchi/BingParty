package cn.yistars.party.bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import cn.yistars.party.bukkit.addon.*;
import cn.yistars.party.bukkit.command.PartyAdminCommand;
import cn.yistars.party.bukkit.command.PartyCommand;

public class Party extends JavaPlugin {
	public static Party instance;
	public static Plugin plugin;
	public static boolean DebugMode;
	public static boolean AlonsoLevelsHook = false;
	public static HashMap<String, String> Messages = new HashMap<>();
	public static HashMap<String, String> Gui = new HashMap<>();
	public static HashMap<String, Integer> Leaders = new HashMap<>();
	public static HashMap<String, String> Rank = new HashMap<>(); // 传递子服
	public static HashMap<String, Boolean> OnlineStat = new HashMap<>(); // 传递子服
	public static HashMap<String, String> PlayerUUID = new HashMap<>(); // 传递子服
	public static HashMap<String, Long> PlayerLastTime = new HashMap<>(); // 传递子服
	public static HashMap<String, String> PlayerServer = new HashMap<>(); // 传递子服
	public static HashMap<String, String> PlayerRole = new HashMap<>(); // 传递子服
	public static HashMap<Integer, String> PartyLeader = new HashMap<>(); // 传递子服
	public static HashMap<String, Integer> PlayerParty = new HashMap<>(); // 传递子服
	public static HashMap<Integer, ArrayList<String>> PartyMod = new HashMap<>(); // 传递子服
	public static HashMap<Integer, ArrayList<String>> PartyMember = new HashMap<>(); // 传递子服

	@Override
	public void onEnable() {
        instance = this;
        
        loadConfig();
        
        if (!this.checkIfBungee()) {
        	Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeChannelManager());
        
        this.getServer().getPluginManager().registerEvents(new PlayerEvent(), this);
        this.getServer().getPluginManager().registerEvents(new PartyListener(), this);
        //this.getServer().getPluginManager().registerEvents(new UpdateChecker(), this);
        
        Objects.requireNonNull(getCommand("party")).setExecutor(new PartyCommand());
        Objects.requireNonNull(getCommand("BingParty")).setExecutor(new PartyAdminCommand());

		// Hook
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPIHook().register();
            this.getLogger().info("Find PlaceholderAPI.");
        }
        if (Bukkit.getPluginManager().getPlugin("AlonsoLevels") != null) {
        	Party.AlonsoLevelsHook = true;
        	this.getLogger().info("Find AlonsoLevels.");
        }

		// Addon
		if (Bukkit.getPluginManager().getPlugin("BedWars1058") != null) {
			this.getServer().getPluginManager().registerEvents(new BedWars1058(), this);
			this.getLogger().info("Find BedWars1058.");
		}
		if (Bukkit.getPluginManager().getPlugin("Arcade") != null) {
			this.getServer().getPluginManager().registerEvents(new Arcade(), this);
			this.getLogger().info("Find Arcade.");
		}
		if (Bukkit.getPluginManager().getPlugin("HideAndSeek") != null) {
			this.getServer().getPluginManager().registerEvents(new HideAndSeek(), this);
			this.getLogger().info("Find HideAndSeek.");
		}
		if (Bukkit.getPluginManager().getPlugin("TowerDefense") != null) {
			this.getServer().getPluginManager().registerEvents(new TowerDefense(), this);
			this.getLogger().info("Find TowerDefense.");
		}
		if (Bukkit.getPluginManager().getPlugin("Skywars") != null) {
			this.getServer().getPluginManager().registerEvents(new SkyWarsReloadedHook(), this);
			this.getLogger().info("Find SkyWarsReloaded.");
		}
		if (Bukkit.getPluginManager().getPlugin("DragonEscape") != null) {
			this.getServer().getPluginManager().registerEvents(new DragonEscape(), this);
			this.getLogger().info("Find DragonEscape.");
		}

        new Metrics(this, 12252);
        
        this.getLogger().info("Enabled successfully.");
	}

	public void onDisEnable() {
		this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
	    this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
	    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PAPIHook().unregister();
        }
        this.getLogger().info("Disenabled successfully.");
    }
	
	private boolean checkIfBungee() {
		if (DebugMode) {
			this.getLogger().info("已启用调试模式, 将忽略 Bungee 检查并启用强制添加");
			return true;
		}
        if (!Objects.requireNonNull(this.getServer().spigot().getConfig().getConfigurationSection("settings")).getBoolean("bungeecord")) {
            this.getLogger().severe("This server is not BungeeCord.");
            this.getLogger().severe("If the server is already hooked to BungeeCord, please enable it into your spigot.yml aswell.");
            this.getLogger().severe("Plugin disabled!");
            this.getServer().getPluginManager().disablePlugin(this);
            return false;
        }
        return true;
    }
	
	public static void loadConfig() {
    	instance.saveDefaultConfig();
    	instance.reloadConfig();
    	
    	DebugMode = instance.getConfig().getBoolean("DebugMode");
	}
}
