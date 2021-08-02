package cn.yistars.party.bungee.command;

import cn.yistars.party.bungee.Party;
import cn.yistars.party.bungee.PartyEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.Arrays;
import java.util.Collections;

public class PartyAdminCommand
extends Command 
implements TabExecutor {

	public PartyAdminCommand() {
        super("partysystem", "partysystem.admin");
   }
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length == 1) {
				//ProxiedPlayer player = (ProxiedPlayer) sender;
				if (args[0].equalsIgnoreCase("reload")) {
					Party.readConfig();
					PartyEvent.SendAllServerUpdate("lang");
					String msg = Party.Messages.get("Reload");
					if (!(msg == null || msg.length() <= 0)) {
		        		msg = ChatColor.translateAlternateColorCodes('&', msg);
		                sender.sendMessage(new TextComponent(msg));
		        	}
					return;
				} else if (args[0].equalsIgnoreCase("help")) {
					String msg = Party.Messages.get("HelpTitle");
					msg = msg.replace("%version%", Party.getInstance().getDescription().getVersion());
					if (!(msg.length() <= 0)) {
						msg = ChatColor.translateAlternateColorCodes('&', msg);
		                sender.sendMessage(new TextComponent(msg));
					}
					msg = Party.Messages.get("HelpHelp");
					if (!(msg == null || msg.length() <= 0)) {
						msg = ChatColor.translateAlternateColorCodes('&', msg);
		                sender.sendMessage(new TextComponent(msg));
					}
					msg = Party.Messages.get("HelpCheck");
					if (!(msg == null || msg.length() <= 0)) {
						msg = ChatColor.translateAlternateColorCodes('&', msg);
		                sender.sendMessage(new TextComponent(msg));
					}
					msg = Party.Messages.get("HelpReload");
					if (!(msg == null || msg.length() <= 0)) {
						msg = ChatColor.translateAlternateColorCodes('&', msg);
		                sender.sendMessage(new TextComponent(msg));
					}
					return;
				}
			}
		
		String msg = "&aPartySystem v%version% By Bing_Yanchi".replace("%version%", Party.getInstance().getDescription().getVersion());
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		sender.sendMessage(new TextComponent(msg));
			
		msg = "&ePlugin Page: https://www.spigotmc.org/resources/bungeekick.89845/";
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		TextComponent message = new TextComponent(msg);
		message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/bungeekick.89845/"));
			
		sender.sendMessage(new TextComponent(message));
		//sender.sendMessage(new ComponentBuilder ("BungeeHub v%version% By Bing_Yanchi").color(ChatColor.GREEN).create());	
	}
	
	public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
		if(args.length == 1) {
			return Arrays.asList("help", "check", "reload");
		}
		return Collections.emptyList();
	}
}