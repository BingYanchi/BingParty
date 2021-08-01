package cn.yistars.party.bukkit;

import java.util.ArrayList;
import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cn.yistars.party.bukkit.event.AddMemberEvent;
import cn.yistars.party.bukkit.event.AddPartyEvent;
import cn.yistars.party.bukkit.event.RemoveMemberEvent;
import cn.yistars.party.bukkit.event.RemovePartyEvent;
import cn.yistars.party.bukkit.gui.MemberSort;

public class BungeeChannelManager implements PluginMessageListener {
	//public final static String PLUGIN_CHANNEL = "PartySystem";
	
	@Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		try {
        	if (channel.equals("BungeeCord")) {
        		ByteArrayDataInput in = ByteStreams.newDataInput(message);
        		String subchannel = in.readUTF();
        		
        		if (subchannel.equals("PartySystem")) {
        			String type = in.readUTF();
        			// TODO
        			System.out.println(type);
        			switch (type) {
        				// 更新语言文件
        				case "UpdateMessages":
        					String Str = in.readUTF();
        					Party.Messages.clear();
        					for (String group : Str.split("@")) {
        						Party.Messages.put(group.split("=")[0], group.split("=")[1]);
        					}
        					break;
        				case "UpdateGui":
        					Str = in.readUTF();
        					Party.Gui.clear();
        					for (String group : Str.split("@")) {
        						Party.Gui.put(group.split("=")[0], group.split("=")[1]);
        					}
        					break;
        				// 事件
        				case "AddParty":
        					int PartyID = in.readInt();
        					String LeaderName = in.readUTF();
        					
        					Player Leader = Bukkit.getPlayer(LeaderName);
        					if (Leader == null) return;
        					Party.Leaders.put(LeaderName, PartyID);
        					AddPartyEvent addpartyEvent = new AddPartyEvent(PartyID, Leader);
        					Bukkit.getPluginManager().callEvent(addpartyEvent);
        					break;
        				case "RemoveParty":
        					PartyID = in.readInt();
        					LeaderName = in.readUTF();
        					
        					Leader = Bukkit.getPlayer(LeaderName);
        					if (Leader == null) return;
        					RemovePartyEvent removepartyEvent = new RemovePartyEvent(PartyID, Leader);
        					Bukkit.getPluginManager().callEvent(removepartyEvent);
        					break;
        				case "AddMember":
        					PartyID = in.readInt();
        					String PlayerName = in.readUTF();
        					LeaderName = in.readUTF();
        					
        					Player Player = Bukkit.getPlayer(PlayerName);
        					Leader = Bukkit.getPlayer(LeaderName);
        					if (Player == null || Leader == null) return;
        					
        					AddMemberEvent addmemberEvent = new AddMemberEvent(PartyID, Player, Leader);
        					Bukkit.getPluginManager().callEvent(addmemberEvent);
        					break;
        				case "RemoveMember":
        					PartyID = in.readInt();
        					PlayerName = in.readUTF();
        					
        					Player = Bukkit.getPlayer(PlayerName);
        					if (Player == null) return;
        					
        					RemoveMemberEvent removememberEvent = new RemoveMemberEvent(PartyID, Player);
        					Bukkit.getPluginManager().callEvent(removememberEvent);
        					break;
        				// 处理更新
        				case "UpdateRank":
        					Str = in.readUTF();
        					Party.Rank.clear();
        					if (Str.equals("")) return;
        					for (String group : Str.split("&")) {
        						Party.Rank.put(group.split("=")[0], group.split("=")[1]);
        					}
        					break;
        				case "UpdateOnlineStat":
        					Str = in.readUTF();
        					Party.OnlineStat.clear();
        					if (Str.equals("")) return;
        					for (String group : Str.split("&")) {
        						Party.OnlineStat.put(group.split("=")[0], group.split("=")[1].equals("true"));
        					}
        					break;
        				case "UpdatePlayerUUID":
        					Str = in.readUTF();
        					Party.PlayerUUID.clear();
        					if (Str.equals("")) return;
        					for (String group : Str.split("&")) {
        						Party.PlayerUUID.put(group.split("=")[0], group.split("=")[1]);
        					}
        					break;
        				case "UpdatePlayerLastTime":
        					Str = in.readUTF();
        					Party.PlayerLastTime.clear();
        					if (Str.equals("")) return;
        					for (String group : Str.split("&")) {
        						Party.PlayerLastTime.put(group.split("=")[0], Long.parseLong(group.split("=")[1]));
        					}
        					break;
        				case "UpdatePlayerServer":
        					Str = in.readUTF();
        					Party.PlayerServer.clear();
        					if (Str.equals("")) return;
        					for (String group : Str.split("&")) {
        						Party.PlayerServer.put(group.split("=")[0], group.split("=")[1]);
        					}
        					break;
        				case "UpdatePlayerRole":
        					Str = in.readUTF();
        					Party.PlayerRole.clear();
        					if (Str.equals("")) return;
        					for (String group : Str.split("&")) {
        						Party.PlayerRole.put(group.split("=")[0], group.split("=")[1]);
        					}
        					MemberSort.UpdateAllMember();
        					break;
        				case "UpdatePartyLeader":
        					Str = in.readUTF();
        					Party.PartyLeader.clear();
        					if (Str.equals("")) return;
        					for (String group : Str.split("&")) {
        						Party.PartyLeader.put(Integer.parseInt(group.split("=")[0]), group.split("=")[1]);
        					}
        					break;
        				case "UpdatePlayerParty":
        					Str = in.readUTF();
        					Party.PlayerParty.clear();
        					if (Str.equals("")) return;
        					for (String group : Str.split("&")) {
        						Party.PlayerParty.put(group.split("=")[0], Integer.parseInt(group.split("=")[1]));
        					}
        					break;
        				case "UpdatePartyMod":
        					Str = in.readUTF();
        					Party.PartyMod.clear();
        					if (Str.equals("")) return;
        					for (String group : Str.split("&")) {
        						ArrayList<String> Mod = new ArrayList<>();
        						String[] grouplist = group.split("=");
        						if (grouplist.length == 2) {
									Collections.addAll(Mod, grouplist[1].split(";"));
        						}
        						Party.PartyMod.put(Integer.parseInt(grouplist[0]), Mod);
        					}
        					break;
        				case "UpdatePartyMember":
        					Str = in.readUTF();
        					Party.PartyMember.clear();
        					if (Str.equals("")) return;
        					for (String group : Str.split("&")) {
        						ArrayList<String> Member = new ArrayList<>();
        						String[] grouplist = group.split("=");
        						if (grouplist.length == 2) {
									Collections.addAll(Member, grouplist[1].split(";"));
        						}
        						Party.PartyMember.put(Integer.parseInt(grouplist[0]), Member);
        					}
        					break;
        			}
        		}
        	}
        } catch (Exception e) {
            //EOF/IllegalStateException?
        }
	}
	
	public static void sendPluginMessage(Player player, String... args) {
    	
        if (player != null) {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("PartySystem");
            for (String arg : args) {
                if (arg != null) {
                    out.writeUTF(arg);
                }
            }
            player.sendPluginMessage(Party.instance, "BungeeCord", out.toByteArray());
		}
	}
}
