package cn.yistars.party.bukkit.command;

import cn.yistars.party.bukkit.Party;
import cn.yistars.party.bukkit.gui.Anvil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cn.yistars.party.bukkit.BungeeChannelManager;
import cn.yistars.party.bukkit.gui.MemberSort;

public class PartyCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command commands, String label, String[] args) {
		if (args.length == 0) return false;
		Player player = (Player) sender;
		
		switch (args[0].toLowerCase()) {
			case "sort":
				MemberSort.ChangeSort(player);
				break;
			case "order":
				MemberSort.ChangeOrder(player);
				break;
			case "anvil":
				if (args.length != 2) return false;
				switch(args[1]) {
					case "invite":
						System.out.println(Party.Gui.get("AnvilInviteTitle"));
						System.out.println(Party.Gui.get("AnvilInviteText"));
						Anvil.CreateAnvil(player, "invite", Party.Gui.get("AnvilInviteTitle"), Party.Gui.get("AnvilInviteText"));
						break;
					case "kick":
						Anvil.CreateAnvil(player, "kick", Party.Gui.get("AnvilKickTitle"), Party.Gui.get("AnvilKickText"));
						break;
					case "search":
						Anvil.CreateAnvil(player, "search", Party.Gui.get("AnvilSearchTitle"), Party.Gui.get("AnvilSearchText"));
						break;
					default:
						return false;
				}
				break;
			default:
				String Command = "party ";
				for (int i=0; i<=(args.length-1); i++) {
	    			Command += args[i] + " ";
	    		}
				Command = Command.substring(0,Command.length()-1);
				BungeeChannelManager.sendPluginMessage(player, "PlayerCommand", Command);
		}
		return true;
	}
}
