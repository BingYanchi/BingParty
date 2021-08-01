package cn.yistars.party.bungee.channel;

import java.util.ArrayList;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cn.yistars.party.bungee.Party;
import cn.yistars.party.bungee.PartyEvent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ChannelSender {
	// PartyEvent
	// 新增组队
	public static void SendAddParty(Integer PartyID, ProxiedPlayer player) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PartySystem");
		
		out.writeUTF("AddParty");
		out.writeInt(PartyID);
		out.writeUTF(player.getName());
		player.getServer().getInfo().sendData("BungeeCord", out.toByteArray() );
	}
	
	// 移除组队
	public static void SendRemoveParty(Integer PartyID, ProxiedPlayer player, String leadername) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PartySystem");
		
		out.writeUTF("RemoveParty");
		out.writeInt(PartyID);
		out.writeUTF(leadername);
		player.getServer().getInfo().sendData("BungeeCord", out.toByteArray() );
	}
	
	// 组队增加成员
		public static void SendAddMember(Integer PartyID, ProxiedPlayer player, String playername) {
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("PartySystem");
			
			out.writeUTF("AddMember");
			out.writeInt(PartyID);
			out.writeUTF(playername);
			// LeaderName
			out.writeUTF(Party.PartyLeader.getOrDefault(PartyID, null));
			
			player.getServer().getInfo().sendData("BungeeCord", out.toByteArray() );
		}
	
	// 组队移除成员
	public static void SendRemoveMember(Integer PartyID, ProxiedPlayer player, String playername) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PartySystem");
		
		out.writeUTF("RemoveMember");
		out.writeInt(PartyID);
		out.writeUTF(playername);
		
		player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
	}
	
	// 组队更改队长
	public static void SendNewLeader(Integer PartyID, ProxiedPlayer player, String oldleader) {
		SendRemoveParty(PartyID, player, oldleader);
		SendAddParty(PartyID, player);
		for (String membername : PartyEvent.GetAllMember(PartyID, false)) {
			SendAddMember(PartyID, player, membername);
		}
	}
	
	public static void SendUpdateRank(ProxiedPlayer player) {
		StringBuilder Str = new StringBuilder();
		for (String key : Party.Rank.keySet()) {
			Str.append(key).append("=").append(Party.Rank.get(key)).append("&");
		}
		if (Str.length() != 0) {
			Str = new StringBuilder(Str.substring(0, Str.length() - 1));
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PartySystem");
		
		out.writeUTF("UpdateRank");
		out.writeUTF(Str.toString());
		
		player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
	}
	
	public static void SendUpdateOnlineStat (ProxiedPlayer player) {
		StringBuilder Str = new StringBuilder();
		for (String key : Party.OnlineStat.keySet()) {
			Str.append(key).append("=").append(Party.OnlineStat.get(key)).append("&");
		}
		if (Str.length() != 0) {
			Str = new StringBuilder(Str.substring(0, Str.length() - 1));
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PartySystem");
		
		out.writeUTF("UpdateOnlineStat");
		out.writeUTF(Str.toString());
		
		player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
	}
	
	public static void SendUpdatePlayerUUID (ProxiedPlayer player) {
		StringBuilder Str = new StringBuilder();
		for (String key : Party.PlayerUUID.keySet()) {
			Str.append(key).append("=").append(Party.PlayerUUID.get(key)).append("&");
		}
		if (Str.length() != 0) {
			Str = new StringBuilder(Str.substring(0, Str.length() - 1));
		}

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PartySystem");
		
		out.writeUTF("UpdatePlayerUUID");
		out.writeUTF(Str.toString());
		
		player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
	}
	
	public static void SendUpdatePlayerLastTime (ProxiedPlayer player) {
		StringBuilder Str = new StringBuilder();
		for (String key : Party.PlayerLastTime.keySet()) {
			Str.append(key).append("=").append(Party.PlayerLastTime.get(key)).append("&");
		}
		if (Str.length() != 0) {
			Str = new StringBuilder(Str.substring(0, Str.length() - 1));
		}

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PartySystem");
		
		out.writeUTF("UpdatePlayerLastTime");
		out.writeUTF(Str.toString());
		
		player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
	}
	
	public static void SendUpdatePlayerServer (ProxiedPlayer player) {
		StringBuilder Str = new StringBuilder();
		for (String key : Party.PlayerServer.keySet()) {
			Str.append(key).append("=").append(Party.PlayerServer.get(key)).append("&");
		}
		if (Str.length() != 0) {
			Str = new StringBuilder(Str.substring(0, Str.length() - 1));
		}

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PartySystem");
		
		out.writeUTF("UpdatePlayerServer");
		out.writeUTF(Str.toString());
		
		player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
	}
	
	public static void SendUpdatePlayerRole (ProxiedPlayer player) {
		StringBuilder Str = new StringBuilder();
		for (String key : Party.PlayerRole.keySet()) {
			Str.append(key).append("=").append(Party.PlayerRole.get(key)).append("&");
		}
		if (Str.length() != 0) {
			Str = new StringBuilder(Str.substring(0, Str.length() - 1));
		}

		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PartySystem");
		
		out.writeUTF("UpdatePlayerRole");
		out.writeUTF(Str.toString());
		
		player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
	}
	
	public static void SendUpdatePartyLeader (ProxiedPlayer player) {
		StringBuilder Str = new StringBuilder();
		for (Integer key : Party.PartyLeader.keySet()) {
			Str.append(key).append("=").append(Party.PartyLeader.get(key)).append("&");
		}
		if (Str.length() != 0) {
			Str = new StringBuilder(Str.substring(0, Str.length() - 1));
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PartySystem");
		
		out.writeUTF("UpdatePartyLeader");
		out.writeUTF(Str.toString());
		
		player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
	}
	
	public static void SendUpdatePlayerParty (ProxiedPlayer player) {
		StringBuilder Str = new StringBuilder();
		for (String key : Party.PlayerParty.keySet()) {
			Str.append(key).append("=").append(Party.PlayerParty.get(key)).append("&");
		}
		if (Str.length() != 0) {
			Str = new StringBuilder(Str.substring(0, Str.length() - 1));
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PartySystem");
		
		out.writeUTF("UpdatePlayerParty");
		out.writeUTF(Str.toString());
		
		player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
	}
	
	public static void SendUpdatePartyMod (ProxiedPlayer player) {
		StringBuilder Str = new StringBuilder();
		for (Integer key : Party.PartyMod.keySet()) {
			Str.append(key).append("=");
			for (String name : Party.PartyMod.get(key)) {
				Str.append(name).append(";");
			}
			if (Party.PartyMod.get(key).size() != 0) {
				Str = new StringBuilder(Str.substring(0, Str.length() - 1));
			}
			Str.append("&");
		}
		if (Str.length() != 0) {
			Str = new StringBuilder(Str.substring(0, Str.length() - 1));
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PartySystem");
		
		out.writeUTF("UpdatePartyMod");
		out.writeUTF(Str.toString());
		
		player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
	}
	
	public static void SendUpdatePartyMember (ProxiedPlayer player) {
		StringBuilder Str = new StringBuilder();
		for (Integer key : Party.PartyMember.keySet()) {
			Str.append(key).append("=");
			for (String name : Party.PartyMember.get(key)) {
				Str.append(name).append(";");
			}
			if (Party.PartyMember.get(key).size() != 0) {
				Str = new StringBuilder(Str.substring(0, Str.length() - 1));
			}
			Str.append("&");
		}
		if (Str.length() != 0) {
			Str = new StringBuilder(Str.substring(0, Str.length() - 1));
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PartySystem");
		
		out.writeUTF("UpdatePartyMember");
		out.writeUTF(Str.toString());
		
		player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
	}
	
	public static void SendUpdateMessages (ProxiedPlayer player) {
		StringBuilder Str = new StringBuilder();
		ArrayList<String> UpdateMessage = new ArrayList<>();
		UpdateMessage.add("Checking");
		UpdateMessage.add("NoUpdate");
		UpdateMessage.add("Update");
		UpdateMessage.add("UpdatePage");
		UpdateMessage.add("UpdateDownload");
		UpdateMessage.add("AutoDownload");
		UpdateMessage.add("AutoDownloadFail");
		
		for (String key : UpdateMessage) {
			Str.append(key).append("=").append(Party.Messages.get(key)).append("@");
		}
		if (Str.length() != 0) {
			Str = new StringBuilder(Str.substring(0, Str.length() - 1));
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PartySystem");
		
		out.writeUTF("UpdateMessages");
		out.writeUTF(Str.toString());
		
		player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
	}
	
	public static void SendUpdateGui (ProxiedPlayer player) {
		StringBuilder Str = new StringBuilder();
		
		for (String key : Party.Gui.keySet()) {
			Str.append(key).append("=").append(Party.Gui.get(key)).append("@");
		}
		if (Str.length() != 0) {
			Str = new StringBuilder(Str.substring(0, Str.length() - 1));
		}
		
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("PartySystem");
		
		out.writeUTF("UpdateGui");
		out.writeUTF(Str.toString());
		
		player.getServer().getInfo().sendData("BungeeCord", out.toByteArray());
	}
}
