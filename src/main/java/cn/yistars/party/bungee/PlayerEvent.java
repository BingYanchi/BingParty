package cn.yistars.party.bungee;

import cn.yistars.party.bungee.channel.ChannelSender;
import cn.yistars.party.bungee.command.PartyCommand;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerEvent implements Listener {
	
	@EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
    	Party.Rank.put(event.getPlayer().getName(), SupiryRank.RankFormat(event.getPlayer()));
    	if (event.getPlayer().hasPermission("partysystem.admin")) {
    		Party.Staff.add(event.getPlayer().getName());
    	}
    	if (Party.PlayerParty.containsKey(event.getPlayer().getName())) {
    		Party.OnlineStat.put(event.getPlayer().getName(), true);
    		Party.PlayerOffline.remove(event.getPlayer().getName());
    		PartyEvent.UpdatePlayerServer(event.getPlayer());
    		PartyEvent.SendAllServerUpdate();
    	}
    }
    
	@EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {
    	if (event.getPlayer().hasPermission("partysystem.admin")) {
    		Party.Staff.remove(event.getPlayer().getName());
    	}
    	// 如果有队伍
    	if (Party.PlayerParty.containsKey(event.getPlayer().getName())) {
    		Party.OnlineStat.put(event.getPlayer().getName(), false);
    		Integer OfflineID = Party.OfflineID;
    		Party.PlayerOffline.put(event.getPlayer().getName(), OfflineID);
    		// 记录最后上线时间
    		Party.PlayerLastTime.put(event.getPlayer().getName(), System.currentTimeMillis());
    		// 通知所有人
    		Integer PartyID = Party.PlayerParty.get(event.getPlayer().getName());
    		if (Party.PlayerRole.get(event.getPlayer().getName()).equals("leader")) {
    			PartyCommand.SendAllMember(PartyID, "Line");
        		PartyCommand.SendAllMember(PartyID, "PartyLeaderServerQuit", SupiryRank.RankFormat(event.getPlayer()) + event.getPlayer().getName(), Party.LeaderOfflineKick.toString());
        		PartyCommand.SendAllMember(PartyID, "Line");
        		PartyTimer.OfflinePlayer(event.getPlayer().getName(), OfflineID, Party.LeaderOfflineKick);
    		} else {
    			PartyCommand.SendAllMember(PartyID, "Line");
        		PartyCommand.SendAllMember(PartyID, "PartyServerQuit", SupiryRank.RankFormat(event.getPlayer()) + event.getPlayer().getName(), Party.OfflineKick.toString());
        		PartyCommand.SendAllMember(PartyID, "Line");
        		PartyTimer.OfflinePlayer(event.getPlayer().getName(), OfflineID, Party.OfflineKick);
    		}
    		Party.OfflineID = OfflineID + 1;
    		// 移除所在服务器
        	Party.PlayerServer.remove(event.getPlayer().getName());
    		PartyEvent.SendAllServerUpdate();
    	}
	}
	
	@EventHandler
	public void onPlayerServerSwitch(ServerSwitchEvent event) {
		// 更新语言文件
		ChannelSender.SendUpdateMessages(event.getPlayer());
		ChannelSender.SendUpdateGui(event.getPlayer());
		
		String playername = event.getPlayer().getName();
		
		if (PartyEvent.CheckGameServer(event.getPlayer(), event.getPlayer().getServer().getInfo().getName())) {
			event.getPlayer().connect(event.getFrom(), ServerConnectEvent.Reason.PLUGIN);
		}
		
		if (Party.PlayerParty.containsKey(playername)) {
			Integer PartyID = Party.PlayerParty.get(playername);
			if (Party.PlayerRole.get(playername).equals("leader")) {
				ChannelSender.SendAddParty(PartyID, event.getPlayer());
				for (String member : PartyEvent.GetAllMember(PartyID, false)) {
					ChannelSender.SendAddMember(PartyID, event.getPlayer(), member);
				}
			} else {
				ChannelSender.SendAddMember(PartyID, event.getPlayer(), playername);
			}
			PartyEvent.UpdatePlayerServer(event.getPlayer());
	    	PartyEvent.SendAllServerUpdate();
		}
	}
    
	@EventHandler
	public void onPlayerChat(ChatEvent event) {
		if (event.isProxyCommand()) {
			String text = event.getMessage();
			if (text.startsWith("/server ")) {
				String servername = text.replace("/server ", "");
				ProxiedPlayer player = (ProxiedPlayer) event.getSender();
				if (PartyEvent.CheckGameServer(player, servername)) {
					event.setCancelled(true);
				}
			}
		}
	}
}
