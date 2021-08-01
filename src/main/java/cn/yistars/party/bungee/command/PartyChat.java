package cn.yistars.party.bungee.command;

import cn.yistars.party.bungee.Party;
import cn.yistars.party.bungee.PartyEvent;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PartyChat extends Command{
	
	public PartyChat() {
    	super("pchat", null, "pc");
    }
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		ProxiedPlayer player = (ProxiedPlayer) sender;
		String playername = player.getName();
		
		if (args.length != 0) {
			if (Party.PlayerParty.containsKey(playername)) {
				Integer PartyID = Party.PlayerParty.get(playername);
				if (Party.PartyMute.get(PartyID)) {
					if (Party.PlayerRole.get(playername).equals("member") && !Party.Staff.contains(playername)) {
						PartyCommand.SendMessage(player, "Line");
						PartyCommand.SendMessage(player, "PartyChatMute");
						PartyCommand.SendMessage(player, "Line");
						return;
					}
				}
				// 处理
				StringBuilder message = new StringBuilder();
	    		for (int i =0; i<=(args.length-1); i++) {
	    			message.append(args[i]).append(" ");
	    		}
	    		
	    		message = new StringBuilder(message.substring(0, message.length() - 1));
	    		PartyEvent.PartyChat(player, message.toString());
				
			} else {
				PartyCommand.SendMessage(player, "Line");
				PartyCommand.SendMessage(player, "NoInParty");
				PartyCommand.SendMessage(player, "Line");
			}
		} else {
			// TODO 用法

		}
	}
}
