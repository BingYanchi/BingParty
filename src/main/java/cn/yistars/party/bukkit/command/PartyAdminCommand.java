package cn.yistars.party.bukkit.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cn.yistars.party.bukkit.Party;
import cn.yistars.party.bukkit.gui.MemberSort;

import java.util.ArrayList;

public class PartyAdminCommand implements CommandExecutor {
	
	@Override
	public boolean onCommand(CommandSender sender, Command commands, String label, String[] args) {
		if (!sender.hasPermission("partysystem.admin")) return false;
		if (args.length == 0) return false;

		switch (args[0]) {
			case "reload":
				System.out.println(Party.PlayerUUID);
				System.out.println(Party.PlayerLastTime);
				System.out.println(Party.PlayerRole);
				System.out.println(Party.PartyLeader);
				System.out.println(Party.PlayerParty);
				System.out.println(Party.PartyMod);
				System.out.println(Party.PartyMember);
				//System.out.println(PartyEvent.GetAllMember(0, true));
				System.out.println(MemberSort.MemberSort);
				System.out.println(Party.PlayerServer);
				System.out.println(Party.Gui);
				return true;
			case "add":
				if (args.length != 2) return false;
				Player player = Bukkit.getPlayer(args[1]);

				if (player == null) return false;

				Party.PlayerParty.put(sender.getName(), 0);
				Party.PlayerParty.put(args[1], 0);
				Party.PartyLeader.put(0, sender.getName());
				Party.PlayerRole.put(sender.getName(), "leader");
				Party.PartyMod.put(0, new ArrayList<>());
				ArrayList<String> Member = new ArrayList<>();
				if (Party.PartyMember.containsKey(0)) {
					Member = Party.PartyMember.get(0);
				}
				if (!Member.contains(args[1])) {
					Member.add(args[1]);
				}
				Party.PartyMember.put(0, Member);
				Party.PlayerRole.put(args[1], "member");
				System.out.println("[DEBUGMODE] 成功添加玩家" + args[1]);
				return true;
			default:
				return false;
		}
	}
}
