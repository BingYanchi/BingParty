package cn.yistars.party.bungee;

import java.util.ArrayList;

import cn.yistars.party.bungee.channel.ChannelSender;
import cn.yistars.party.bungee.command.PartyCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;

public class PartyEvent {
	
	// 更新所有信息
	public static void SendAllPartyUpdate (ProxiedPlayer player) {
		ChannelSender.SendUpdateRank(player);
		ChannelSender.SendUpdateOnlineStat(player);
		ChannelSender.SendUpdatePlayerUUID(player);
		ChannelSender.SendUpdatePlayerLastTime(player);
		ChannelSender.SendUpdatePlayerServer(player);
		ChannelSender.SendUpdatePartyLeader(player);
		ChannelSender.SendUpdatePlayerParty(player);
		ChannelSender.SendUpdatePartyMod(player);
		ChannelSender.SendUpdatePartyMember(player);
		ChannelSender.SendUpdatePlayerRole(player);
	}
	
	// 向所有服务器更新信息
	public static void SendAllServerUpdate() {
		ArrayList<String> ServerList = new ArrayList<>();
		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			String Server = player.getServer().getInfo().getName();
			if (!ServerList.contains(Server)) {
				SendAllPartyUpdate(player);
				ServerList.add(Server);
			}
		}
	}
	
	public static void UpdatePlayerServer (ProxiedPlayer player) {
		// 填入所在服务器
    	String Servername = player.getServer().getInfo().getName();
    	if (Party.Server.containsKey(Servername)) {
    		Party.PlayerServer.put(player.getName(), Party.Server.get(Servername));
    	} else {
    		Party.PlayerServer.put(player.getName(), Party.Server.get("Default").replace("%server%", Servername));
    	}
	}
	
	// 受限服务器
	public static boolean CheckGameServer(ProxiedPlayer player, String servername) {
		String playername = player.getName();
		if (Party.PlayerParty.containsKey(playername)) {
    		// 如果在受限服务器
    		if (Party.GameServer.containsKey(servername)) {
    			if (Party.PlayerRole.get(playername).equals("leader")) {
    				Integer PartyID = Party.PlayerParty.get(playername);
    				if (PartyEvent.GetAllMember(PartyID, true).size() > Party.GameServer.get(servername)) {
    					// 发送超过
    					PartyCommand.SendMessage(player, "PartyTooLarge", Party.GameServer.get(servername).toString());
    					return true;
    				} else {
    					// 传送所有成员
    					for (String membername : PartyEvent.GetAllMember(PartyID, false)) {
    						ProxiedPlayer member = ProxyServer.getInstance().getPlayer(membername);
    						ServerInfo server = ProxyServer.getInstance().getServerInfo(servername);
    						member.connect(server, ServerConnectEvent.Reason.PLUGIN);
    					}
    					return false;
    				}
    			} else {
    				// 提示只有队长可以加入
    				PartyCommand.SendMessage(player, "OnlyLeaderJoin");
    				return true;
    			}
    		}
    	}
		return false;
	}
	
	// 组队传送
	public static String WarpParty(Integer PartyID, String servername, Boolean Command) {
		if (Party.BlackWarp.contains(servername)) {
			if (Command) {
				ProxiedPlayer leaderplayer = ProxyServer.getInstance().getPlayer(Party.PartyLeader.get(PartyID));
				PartyCommand.SendMessage(leaderplayer, "Line");
				PartyCommand.SendMessage(leaderplayer, "NoWarp");
				PartyCommand.SendMessage(leaderplayer, "Line");
			}
			return null;
		}
		ArrayList<String> members = PartyEvent.GetAllMember(PartyID, false);
		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(Party.PartyLeader.get(PartyID));

    	ServerInfo warpServer = ProxyServer.getInstance().getServerInfo(servername);
    	if (warpServer == null) return null;
    	
    	StringBuilder warpmembers = new StringBuilder();
    	// 传送操作
    	for (String member : members) {
    		ProxiedPlayer warpplayer = ProxyServer.getInstance().getPlayer(member);
    		if (warpplayer != null) {
    			// 通知被传送
    			if (Command) {
    				PartyCommand.SendMessage(warpplayer, "Line");
    		    	PartyCommand.SendMessage(warpplayer, "PartyWarp", SupiryRank.RankFormat(player) + player.getName());
    		    	PartyCommand.SendMessage(warpplayer, "Line");
    			}
    			warpmembers.append(" ").append(SupiryRank.RankFormat(warpplayer)).append(warpplayer.getName());
		    	// 执行传送
	    		if (!(warpplayer.getServer().getInfo().getName().equals(servername))) {
	    			warpplayer.connect(warpServer, ServerConnectEvent.Reason.PLUGIN);
	    		}
    		}
    	}
    	return warpmembers.toString();
	}
	// 玩家邀请
	public static void SendPartyInvite(ProxiedPlayer player, String inviteplayername) {
		String playername = player.getName();
		ProxiedPlayer inviteplayer = ProxyServer.getInstance().getPlayer(inviteplayername);
		if (inviteplayer != null) {
			if (inviteplayer.getName().equals(playername)) {
				PartyCommand.SendMessage(player, "Line");
				PartyCommand.SendMessage(player, "NoInviteSelf");
				PartyCommand.SendMessage(player, "Line");
				return;
			}
			
			if (!(Party.PlayerParty.containsKey(playername))) {
				// 如果玩家本身没有组队
				Party.PartyLeader.put(Party.PartyID, playername);
				Party.PlayerParty.put(playername, Party.PartyID);
				Party.PartyMember.put(Party.PartyID, new ArrayList<>());
				Party.PartyMod.put(Party.PartyID, new ArrayList<>());
				Party.PlayerRole.put(playername, "leader");
				Party.PartyInviteNumber.put(Party.PartyID, 0);
				Party.PartyAllInvite.put(Party.PartyID, false);
				Party.PartyMute.put(Party.PartyID, false);
				// 触发创建组队
				ChannelSender.SendAddParty(Party.PartyID, player);
				// 添加 UUID
				Party.PlayerUUID.put(playername, player.getUniqueId().toString());
				// 添加玩家所在服务器
				PartyEvent.UpdatePlayerServer(player);
				// 添加在线状态
				Party.OnlineStat.put(playername, true);
				// 为下一个组队做准备
				Party.PartyID += 1;
			} else {
				Integer PartyID = Party.PlayerParty.get(playername);
				// 如果身份是普通队员
				if (Party.PlayerRole.get(playername).equals("member") && !Party.PartyAllInvite.get(PartyID) && !Party.Staff.contains(playername)) {
					PartyCommand.SendMessage(player, "Line");
					PartyCommand.SendMessage(player, "NoPermissionInvite");
					PartyCommand.SendMessage(player, "Line");
					return;
				}
				// 判断是否有邀请
				if (Party.PartyInvite.containsKey(inviteplayername)) {
					ArrayList<String> inviters = Party.PartyInvite.get(inviteplayername);
    				for (String inviter : inviters) {
    					Integer inviterPartyID = Party.PlayerParty.get(inviter);
    					if (inviterPartyID.equals(PartyID)) {
    						PartyCommand.SendMessage(player, "Line");
    						PartyCommand.SendMessage(player, "AlreadyInviter", Party.Rank.get(inviteplayername) + inviteplayername);
    						PartyCommand.SendMessage(player, "Line");
    						return;
    					}
    				}
				}
				// 如果该玩家有组队, 则判断是否在相同的队伍
				if (Party.PlayerParty.containsKey(inviteplayer.getName())) {
					if (Party.PlayerParty.get(inviteplayer.getName()).equals(PartyID)) {
						PartyCommand.SendMessage(player, "Line");
						PartyCommand.SendMessage(player, "AlreadyInParty", SupiryRank.RankFormat(inviteplayer) + inviteplayer.getName());
						PartyCommand.SendMessage(player, "Line");
						return;
					}
				}
			}

			// 添加邀请列表
			ArrayList<String> invites = new ArrayList<>();
			if (Party.PartyInvite.containsKey(inviteplayer.getName())) {
				invites = Party.PartyInvite.get(inviteplayer.getName());
			}
			invites.add(playername);
			Party.PartyInvite.put(inviteplayer.getName(), invites);
			// 添加邀请等待时间
			PartyTimer.InvitePlayer(playername, inviteplayer.getName());
			// 添加邀请数量
			Integer PartyID = Party.PlayerParty.get(playername);
			Integer InviteNumber = Party.PartyInviteNumber.get(PartyID) + 1;
			Party.PartyInviteNumber.put(PartyID, InviteNumber);
			// 发送消息给所有成员
			PartyCommand.SendAllMember(PartyID, "Line");
			PartyCommand.SendAllMember(PartyID, "SuccessInvite", SupiryRank.RankFormat(player) + player, SupiryRank.RankFormat(inviteplayer) + inviteplayer.getName(), Party.PartyInviteWait.toString());
			PartyCommand.SendAllMember(PartyID, "Line");
			// 发送可点击的指令给被邀请人
			PartyCommand.SendMessage(inviteplayer, "Line");
			PartyCommand.SendMessage(inviteplayer, "GetInviteInfo", SupiryRank.RankFormat(player) + player);
			PartyCommand.SendHoverMessage(inviteplayer, "GetInvite", "GetInviteHonver", playername, Party.PartyInviteWait.toString());
			PartyCommand.SendMessage(inviteplayer, "Line");
		} else {
			PartyCommand.SendMessage(player, "Line");
			PartyCommand.SendMessage(player, "NoPlayer");
			PartyCommand.SendMessage(player, "Line");
		}
	}
	
	// 检测玩家是否在某个组队中
	public static boolean CheckInParty(Integer PartyID, String playername, Boolean leader) {
		ArrayList<String> members = GetAllMember(PartyID, leader);
    	// 逐个检查
    	for (String member : members) {
    		if (member.equals(playername)) {
    			return true;
    		}
    	}
    	return false;
	}
	
	// 组队聊天
	public static void PartyChat(ProxiedPlayer player, String message) {
		Integer PartyID = Party.PlayerParty.get(player.getName());

		PartyCommand.SendAllMember(PartyID, "PartyChat", SupiryRank.RankFormat(player) + player.getName(), message);
	}
	
	// 获取所有成员
	public static ArrayList<String> GetAllMember(Integer PartyID, Boolean leader) {
		ArrayList<String> members = new ArrayList<>();
		// 组队成员添加
    	if (Party.PartyMember.get(PartyID).size() != 0) {
			members.addAll(Party.PartyMember.get(PartyID));
    	}
    	// 组队管理添加
    	if (Party.PartyMod.get(PartyID).size() != 0) {
			members.addAll(Party.PartyMod.get(PartyID));
    	}
    	// 如果包括队长
    	if (leader) {
    		members.add(Party.PartyLeader.get(PartyID));
    	}
    	return members;
	}
	
	// 踢出某位成员
	public static void KickMember(Integer PartyID, String member) {
		switch (Party.PlayerRole.get(member)) {
			case "member":
				ArrayList<String> memberlist= Party.PartyMember.get(PartyID);
				memberlist.remove(member);
				Party.PartyMember.put(PartyID, memberlist);
				// 传递移除成员
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(Party.PartyLeader.get(PartyID));
				if (player != null) {
					ChannelSender.SendRemoveMember(PartyID, player, member);
				}
				break;
			case "mod":
				ArrayList<String> modlist= Party.PartyMod.get(PartyID);
				modlist.remove(member);
				Party.PartyMod.put(PartyID, modlist);
				// 传递移除成员
				player = ProxyServer.getInstance().getPlayer(Party.PartyLeader.get(PartyID));
				if (player != null) {
					ChannelSender.SendRemoveMember(PartyID, player, member);
				}
				break;
			case "leader":
				modlist= Party.PartyMod.get(PartyID);
				memberlist = Party.PartyMember.get(PartyID);
				if (modlist.size() != 0) {
					int index = (int) (Math.random()* modlist.size());
					// 移除旧队长
					Party.PlayerParty.remove(member);
					Party.PlayerRole.remove(member);
					// 添加新队长
					Party.PartyLeader.put(PartyID, modlist.get(index));
					Party.PlayerRole.put(modlist.get(index), "leader");
					// 移除旧身份
					modlist.remove(modlist.get(index));
					Party.PartyMod.put(PartyID, modlist);
					// 传递挪动队长
					player = ProxyServer.getInstance().getPlayer(modlist.get(index));
					if (player != null) {
						ChannelSender.SendNewLeader(PartyID, player, member);
						// 传递移除成员
						ChannelSender.SendRemoveMember(PartyID, player, member);
					}
				} else if (memberlist.size() != 0){
					int index = (int) (Math.random()* memberlist.size());
					// 移除旧队长
					Party.PlayerParty.remove(member);
					Party.PlayerRole.remove(member);
					// 添加新队长
					Party.PartyLeader.put(PartyID, memberlist.get(index));
					Party.PlayerRole.put(memberlist.get(index), "leader");
					// 移除旧身份
					memberlist.remove(memberlist.get(index));
					Party.PartyMember.put(PartyID, memberlist);
					// 传递挪动队长
					player = ProxyServer.getInstance().getPlayer(memberlist.get(index));
					if (player != null) {
						ChannelSender.SendNewLeader(PartyID, player, member);
					// 传递移除成员
						ChannelSender.SendRemoveMember(PartyID, player, member);
					}
				} else {
					// 移除旧队长
					Party.PlayerParty.remove(member);
					Party.PlayerRole.remove(member);
					// 解散组队
					DisbandParty(PartyID);
				}
				break;
		}
		Party.PlayerRole.remove(member);
		Party.PlayerParty.remove(member);
		// 移除 UUID
		Party.PlayerUUID.remove(member);
		// 移除 最后上线时间
		Party.PlayerLastTime.remove(member);
		// 移除在线状态
		Party.OnlineStat.remove(member);
		// 传递每个服务器更新
		SendAllServerUpdate();
	}
	
	// 检测是否无邀请, 且队伍仅剩一人
	public static void CheckNoMemberParty(Integer PartyID) {
		ArrayList<String> members = GetAllMember(PartyID, true);
		if (members.size() <= 1 && Party.PartyInviteNumber.get(PartyID) <= 0) {
			// 通知最后的人
			for (String member : members) {
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(member);
				if (player != null) {
					PartyCommand.SendMessage(player, "Line");
					PartyCommand.SendMessage(player, "NoPlayerDisband");
					PartyCommand.SendMessage(player, "Line");
				}
			}
			// 解散组队
			DisbandParty(PartyID);
		}
	}
	// 解散组队
	public static void DisbandParty(Integer PartyID) {
		// 传递已移除组队
		ProxiedPlayer player = ProxyServer.getInstance().getPlayer(Party.PartyLeader.get(PartyID));
		if (player != null) {
			ChannelSender.SendRemoveParty(PartyID, player, player.getName());
		}
		// 执行解散操作
		ArrayList<String> members = PartyEvent.GetAllMember(PartyID, true);
    	// 清除个人信息
    	for (String member : members) {
    		Party.PlayerParty.remove(member);
    		Party.PlayerRole.remove(member);
    	}
    	// 清除队伍信息
    	Party.PartyLeader.remove(PartyID);
    	Party.PartyMember.remove(PartyID);
    	Party.PartyMod.remove(PartyID);
    	Party.PartyInviteNumber.remove(PartyID);
    	Party.PartyAllInvite.remove(PartyID);
    	Party.PartyMute.remove(PartyID);
    	// 通知所有服务器更新
		PartyEvent.SendAllServerUpdate();
	}
	
	public static void ChangeRole(Integer PartyID, String member, String role) {
		String nowRole = Party.PlayerRole.get(member);
		if (nowRole.equals(role)) return;
		
		switch (nowRole) {
			case "member":
				ArrayList<String> memberlist= Party.PartyMember.get(PartyID);
				memberlist.remove(member);
				Party.PartyMember.put(PartyID, memberlist);
				break;
			case "mod":
				ArrayList<String> modlist= Party.PartyMod.get(PartyID);
				modlist.remove(member);
				Party.PartyMod.put(PartyID, modlist);
				break;
		}
		
		switch (role) {
			case "member":
				ArrayList<String> memberlist= Party.PartyMember.get(PartyID);
				memberlist.add(member);
				Party.PartyMember.put(PartyID, memberlist);
				Party.PlayerRole.put(member, "member");
				break;
			case "mod":
				ArrayList<String> modlist= Party.PartyMod.get(PartyID);
				modlist.add(member);
				Party.PartyMod.put(PartyID, modlist);
				Party.PlayerRole.put(member, "mod");
				break;
			case "leader":
				// 先挪动原队长
				modlist= Party.PartyMod.get(PartyID);
				modlist.add(Party.PartyLeader.get(PartyID));
				String OldLeader = Party.PartyLeader.get(PartyID);
				Party.PlayerRole.put(OldLeader, "mod");
				Party.PartyMod.put(PartyID, modlist);
				// 再操作新队长
				Party.PartyLeader.put(PartyID, member);
				Party.PlayerRole.put(member, "leader");
				// 传递挪动队长
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(member);
				if (player != null) {
					ChannelSender.SendNewLeader(PartyID, player, OldLeader);
				}
				break;
		}
		SendAllServerUpdate();
	}
}
