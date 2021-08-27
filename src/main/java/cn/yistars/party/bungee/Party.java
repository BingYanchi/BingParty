package cn.yistars.party.bungee;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import cn.yistars.party.bungee.channel.ChannelListener;
import cn.yistars.party.bungee.command.PartyAdminCommand;
import cn.yistars.party.bungee.command.PartyChat;
import cn.yistars.party.bungee.command.PartyCommand;
import cn.yistars.party.bungee.listener.PlayerEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Party extends Plugin {
	public static Party instance;
	public static boolean DebugMode;
	public static Integer PartyID = 0;
	public static Integer OfflineID = 0;
	public static Integer PartyInviteWait = 60;
	public static Integer OfflineKick = 5;
	public static Integer LeaderOfflineKick = 5;
	public static ArrayList<String> Staff = new ArrayList<>();
	public static List<String> BlackWarp = new ArrayList<>();
	public static HashMap<String, Integer> GameServer = new HashMap<>();
	public static HashMap<String, Integer> PlayerOffline = new HashMap<>();
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
	public static HashMap<String, ArrayList<String>> PartyInvite = new HashMap<>();
	public static HashMap<Integer, Integer> PartyInviteNumber = new HashMap<>();
	public static HashMap<Integer, Boolean> PartyAllInvite = new HashMap<>();
	public static HashMap<Integer, Boolean> PartyMute = new HashMap<>();
	public static HashMap<String, String> Messages = new HashMap<>();
	public static HashMap<String, String> Gui = new HashMap<>(); // 传递子服
	public static HashMap<String, String> Server = new HashMap<>();
	public static Logger logger = Logger.getLogger("BingParty");
	
	@Override
	public void onEnable() {
		instance = this;
		ProxyServer.getInstance().registerChannel("BingParty");
		
		readConfig();
		
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new PartyAdminCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new PartyCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new PartyChat());
		
		getProxy().getPluginManager().registerListener(this, new PlayerEvent());
		getProxy().getPluginManager().registerListener(this, new ChannelListener());
		//getProxy().getPluginManager().registerListener(this, new UpdateChecker());
		
		new Metrics(this,12128);
        
		Party.logger.info(Messages.get("Enabled"));
	}
	
	public void onDisable() {
		ProxyServer.getInstance().getPluginManager().unregisterCommands(this);
		getProxy().getPluginManager().unregisterListener(new PlayerEvent());
		getProxy().getPluginManager().unregisterListener(new ChannelListener());
		//getProxy().getPluginManager().unregisterListener(new UpdateChecker());
		ProxyServer.getInstance().unregisterChannel("BingParty");
		Party.logger.info(Messages.get("Disenabled"));
	}
	
	public static Party getInstance() {
        return instance;
    }
	
	public static void checkConfig() {
		if (!getInstance().getDataFolder().exists()) {
			getInstance().getDataFolder().mkdir();
		}

        File file = new File(getInstance().getDataFolder(), "config.yml");

        if (!file.exists()) {
            try (InputStream in = getInstance().getResourceAsStream("bungee_config.yml")) {
                Files.copy(in, file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        File msg_file = new File(getInstance().getDataFolder(), "messages.yml");
        if (!msg_file.exists()) {
            try (InputStream in = getInstance().getResourceAsStream("messages.yml")) {
                Files.copy(in, msg_file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        File gui_file = new File(getInstance().getDataFolder(), "gui.yml");
        if (!gui_file.exists()) {
            try (InputStream in = getInstance().getResourceAsStream("gui.yml")) {
                Files.copy(in, gui_file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        File server_file = new File(getInstance().getDataFolder(), "server.yml");
        if (!server_file.exists()) {
            try (InputStream in = getInstance().getResourceAsStream("server.yml")) {
                Files.copy(in, server_file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

	public static void loadConfig() {
		try {
			Configuration config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getInstance().getDataFolder(), "config.yml"));
			
			DebugMode = config.getBoolean("DebugMode");
			PartyInviteWait = config.getInt("PartyInviteWait");
			OfflineKick = config.getInt("OfflineKick");
			LeaderOfflineKick = config.getInt("LeaderOfflineKick");
			BlackWarp = config.getStringList("BlackWarp");
			
			for (String num : config.getSection("GameServer").getKeys()) {
				for (String servername : config.getStringList("GameServer." + num)) {
					GameServer.put(servername, Integer.parseInt(num));
				}
			}

			Configuration msg_config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getInstance().getDataFolder(), "messages.yml"));
			Configuration gui_config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getInstance().getDataFolder(), "gui.yml"));
			Configuration server_config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getInstance().getDataFolder(), "server.yml"));
			
			for (String key : msg_config.getKeys()) {
				Messages.put(key, msg_config.getString(key));
			}
			
			for (String key : gui_config.getKeys()) {
				Gui.put(key, gui_config.getString(key));
			}
			
			for (String key : server_config.getKeys()) {
				Server.put(key, server_config.getString(key));
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to load configuration file", e);
		}
	}
	
	public static void readConfig() {
		
		GameServer.clear();
		Messages.clear();
		Gui.clear();
		Server.clear();
		
		checkConfig();
		loadConfig();
		
		if (DebugMode) {
			Party.logger.info("以下为调试信息 (Messages, Gui, Staff, Rank, PlayerRole, OnlineStat, PartyLeader, PlayerParty, PartyMod, PartyMember, PartyInvite, PartyInviteNumber, BlackWarp):");
			System.out.println(Messages);
			System.out.println(Gui);
			System.out.println(Staff);
			System.out.println(Rank);
			System.out.println(PlayerRole);
			System.out.println(OnlineStat);
			System.out.println(PartyLeader);
			System.out.println(PlayerParty);
			System.out.println(PartyMod);
			System.out.println(PartyMember);
			System.out.println(PartyInvite);
			System.out.println(PartyInviteNumber);
			System.out.println(BlackWarp);
		}
		Party.logger.info(Messages.get("Loaded"));
	}
}
