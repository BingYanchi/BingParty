package cn.yistars.party.bungee;

import java.util.ArrayList;

import cn.yistars.party.bungee.command.PartyCommand;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public interface PartyHook {
	// 获取所有成员
	public static ArrayList<String> getPartyAllMember(Integer PartyID, Boolean leader) {
		return PartyEvent.GetAllMember(PartyID, leader);
	}
	// 获取身份
	public static String getPlayerRole(String playername) {
		if (Party.PlayerRole.containsKey(playername)) {
			return Party.PlayerRole.get(playername);
		}
		return null;
	}
	// 获取队长
	public static String getPartyLeader(Integer PartyID) {
		if (Party.PartyLeader.containsKey(PartyID)) {
			return Party.PartyLeader.get(PartyID);
		}
		return null;
	}
	// 获取副队长
	public static ArrayList<String> getPartyMod(Integer PartyID) {
		if (Party.PartyMod.containsKey(PartyID)) {
			return Party.PartyMod.get(PartyID);
		}
		return null;
	}
	// 获取成员
	public static ArrayList<String> getPartyMember(Integer PartyID) {
		if (Party.PartyMember.containsKey(PartyID)) {
			return Party.PartyMember.get(PartyID);
		}
		return null;
	}
	// 获取玩家所在组队
	public static Integer getPlayerParty(String playername) {
		if (Party.PlayerParty.containsKey(playername)) {
			return Party.PlayerParty.get(playername);
		}
		return null;
	}
	// 获取组队禁言设置
	public static Boolean getPartyMute(Integer PartyID) {
		if (Party.PartyMute.containsKey(PartyID)) {
			return Party.PartyMute.get(PartyID);
		}
		return null;
	}
	// 获取组队是否允许所有人邀请
	public static Boolean getPartyAllMemberInvite(Integer PartyID) {
		if (Party.PartyAllInvite.containsKey(PartyID)) {
			return Party.PartyAllInvite.get(PartyID);
		}
		return null;
	}
	// 获取玩家是否在线
	public static Boolean getPlayerOnline(String playername) {
		if (Party.OnlineStat.containsKey(playername)) {
			return Party.OnlineStat.get(playername);
		} else {
			return null;
		}
	}
	// 获取是否为工作人员
	public static Boolean getPlayerStaff(String playername) {
		if (Party.Staff.contains(playername)) {
			return true;
		}
		return false;
	}
	// 获取 Rank 身份
	public static String getPlayerRank(String playername) {
		if (Party.Rank.containsKey(playername)) {
			return Party.Rank.get(playername);
		}
		return null;
	}
	// 操作类的
	// 踢出某位组队成员
	public static Boolean KickMember(Integer PartyID, String playername) {
		if (Party.PlayerParty.containsKey(playername)) {
			PartyEvent.KickMember(PartyID, playername);
			return true;
		}
		return false;
	}
	// 组队聊天
	public static Boolean PartyChat(ProxiedPlayer player, String message) {
		if (Party.PlayerParty.containsKey(player.getName())) {
			PartyEvent.PartyChat(player, message);
			return true;
		}
		return false;
	}
	// 解散组队
	public static Boolean DisbandParty(Integer PartyID) {
		if (Party.PartyLeader.containsKey(PartyID)) {
			PartyEvent.DisbandParty(PartyID);
			return true;
		}
		return false;
	}
	// 检测是否无邀请, 且队伍仅剩一人
	public static Boolean CheckNoMemberParty(Integer PartyID) {
		if (Party.PartyLeader.containsKey(PartyID)) {
			PartyEvent.CheckNoMemberParty(PartyID);
			return true;
		}
		return false;
	}
	// 发送消息
	public static Boolean SendMessage(ProxiedPlayer player, String name, String... args) {
		if (Party.Messages.containsKey(name)) {
			PartyCommand.SendMessage(player, name, args);
			return true;
		}
		return false;
	}
	// 发送所有成员消息
	public static Boolean SendAllMember(Integer PartyID, String name, String... args) {
		if (Party.Messages.containsKey(name)) {
			PartyCommand.SendAllMember(PartyID, name, args);
			return true;
		}
		return false;
	}
}
