package cn.yistars.party.bungee;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import cn.yistars.party.bungee.command.PartyCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PartyTimer {
	// 组队成员离线检测
	public static void OfflinePlayer(String playername, Integer OfflineID, Integer time) {
		Party.instance.getProxy().getScheduler().schedule(Party.instance, () -> {
			if (Party.PlayerOffline.containsKey(playername)) {
				if (Party.PlayerOffline.get(playername).equals(OfflineID)) {
					Integer PartyID = Party.PlayerParty.get(playername);
					if (!Party.PlayerRole.get(playername).equals("leader")) {
						// 踢出成员
						PartyEvent.KickMember(PartyID, playername);
						// 通知所有人
						PartyCommand.SendAllMember(PartyID, "Line");
						PartyCommand.SendAllMember(PartyID, "PartyServerKick", Party.Rank.get(playername) + playername);
						PartyCommand.SendAllMember(PartyID, "Line");
						// 检查人数
						PartyEvent.CheckNoMemberParty(PartyID);
					} else {
						// 通知所有人
						PartyCommand.SendAllMember(PartyID, "Line");
						PartyCommand.SendAllMember(PartyID, "PartyLeaderServerKick");
						PartyCommand.SendAllMember(PartyID, "Line");
						// 解散组队
						PartyEvent.DisbandParty(PartyID);
					}
				}
			}
		}, time, TimeUnit.MINUTES);
	}
	// 邀请玩家
	public static void InvitePlayer(String playername, String inviteplayername) {
		Integer PartyID = Party.PlayerParty.get(playername);
		Party.instance.getProxy().getScheduler().schedule(Party.instance, () -> {
			if (!Party.PartyLeader.containsKey(PartyID)) {
				return;
			}
			if (Party.PartyInvite.containsKey(inviteplayername)) {
				// 还有存在的邀请
				ArrayList<String> inviters = Party.PartyInvite.get(inviteplayername);
				for (String inviter : inviters) {
					if (inviter.equals(playername)) {
						ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playername);
						// 通知本人
						if (player != null) {
							PartyCommand.SendMessage(player, "Line");
							PartyCommand.SendMessage(player, "InviteExpired", Party.Rank.get(inviteplayername) + inviteplayername);
							PartyCommand.SendMessage(player, "Line");
						}
						// 通知被邀请
						ProxiedPlayer inviteplayer = ProxyServer.getInstance().getPlayer(inviteplayername);
						if (inviteplayer != null) {
							PartyCommand.SendMessage(inviteplayer, "Line");
							PartyCommand.SendMessage(inviteplayer, "PartyExpired", Party.Rank.get(playername) + playername);
							PartyCommand.SendMessage(inviteplayer, "Line");
						}
						// 清除掉这个邀请
						inviters.remove(playername);
						// 减少队伍邀请数量
						Integer InviteNumber = Party.PartyInviteNumber.get(PartyID) - 1;
						Party.PartyInviteNumber.put(PartyID, InviteNumber);
						// 判断这个玩家还有没有别的邀请了
						if (inviters.size() == 0) {
							Party.PartyInvite.remove(inviteplayername);
						} else {
							Party.PartyInvite.put(inviteplayername, inviters);
						}
						// 检测组队是否仅剩一人
						PartyEvent.CheckNoMemberParty(PartyID);
						return;
					}
				}
				// 已经同意了
			}  // 如果没有邀请

		}, Party.PartyInviteWait, TimeUnit.SECONDS);
	}
}
