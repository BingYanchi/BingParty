package cn.yistars.party.bukkit.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import cn.yistars.party.bukkit.Party;
import cn.yistars.party.bukkit.gui.MemberSort;
import cn.yistars.party.bukkit.PartyEvent;

public class PartyAdminCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command commands, String label, String[] args) {
		if (!sender.hasPermission("partysystem.admin")) return false;
		System.out.println(Party.PlayerUUID);
		System.out.println(Party.PlayerLastTime);
		System.out.println(Party.PlayerRole);
		System.out.println(Party.PartyLeader);
		System.out.println(Party.PlayerParty);
		System.out.println(Party.PartyMod);
		System.out.println(Party.PartyMember);
		System.out.println(PartyEvent.GetAllMember(0, true));
		System.out.println(MemberSort.MemberSort);
		System.out.println(Party.PlayerServer);
		System.out.println(Party.Gui);
		return true;
	}

}
