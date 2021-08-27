package cn.yistars.party.bungee.channel;

import cn.yistars.party.bungee.command.PartyCommand;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cn.yistars.party.bungee.Party;
import cn.yistars.party.bungee.PartyEvent;

public class ChannelListener implements Listener {

    @EventHandler
    public void onPluginMessage(PluginMessageEvent event) {
    	
        if (event.getTag().equalsIgnoreCase("BungeeCord")) {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(event.getData()));

            try {
                String subchannel = dis.readUTF();
                
                ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
        		String playername = player.getName();
        		
                if (!subchannel.equalsIgnoreCase("PlayerCount")) {
                	 //System.out.println(subchannel);
                }
                
                if (subchannel.equalsIgnoreCase("Connect")) {
                	String servername = dis.readUTF();
                	if (PartyEvent.CheckGameServer(player, servername)) {
                		event.setCancelled(true);
                	}
                }
                
                if (subchannel.equalsIgnoreCase("BingParty")) {

                    String type = dis.readUTF();
                    //System.out.println(type);
                    
            		ByteArrayDataOutput out = ByteStreams.newDataOutput();
            		out.writeUTF("BingParty");
                    
                    switch (type) {
                    	case "PlayerCommand":
                        	String command = dis.readUTF();
                        	ProxyServer.getInstance().getPluginManager().dispatchCommand(player, command);
                    		return;
						case "SendMessage":
							String msg = dis.readUTF();
							PartyCommand.SendMessage(player, msg);
							return;
						case "SendServer":
							String sendname = dis.readUTF();
							ServerInfo server = player.getServer().getInfo();
							ProxiedPlayer send = ProxyServer.getInstance().getPlayer(sendname);
							if (send != null) {
								send.connect(server, ServerConnectEvent.Reason.PLUGIN);
							}
							return;
                    	case "CheckInParty":
                    	    out.writeUTF("CheckInParty");
                    	    out.writeBoolean(Party.PlayerParty.containsKey(playername));
                    		break;
                    	case "GetPartyRole":
                    	    out.writeUTF("GetPartyRole");

							out.writeUTF(Party.PlayerRole.getOrDefault(playername, null));
                    		break;
                    	case "GetPartyLeader":
                    		out.writeUTF("GetPartyLeader");
                    		
                    		if (Party.PlayerParty.containsKey(playername)) {
                    			Integer PartyID = Party.PlayerParty.get(playername);
                    			String Leader = Party.PartyLeader.get(PartyID);
                    			
                    			out.writeUTF(Leader);
                    		} else {
                    			out.writeUTF(null);
                    		}
                    		break;
                    	case "GetAllMember":
                    	    out.writeUTF("GetAllMember");
                    		if (Party.PlayerParty.containsKey(playername)) {
                    			Integer PartyID = Party.PlayerParty.get(playername);
                    			Boolean Leader = dis.readBoolean();
                    			StringBuilder members = new StringBuilder();
                    			for (String member : PartyEvent.GetAllMember(PartyID, Leader)) {
                    				members.append(member).append(", ");
                    			}
                    			members = new StringBuilder(members.substring(0, members.length() - 2));
                    			// 返回
                    			out.writeUTF(members.toString());
                    		} else {
                    			out.writeUTF(null);
                    		}
                    		break;
                    	case "GetPartyID":
                    	    out.writeUTF("GetPartyID");
							out.writeInt(Party.PlayerParty.getOrDefault(playername, -1));
                    		break;
                    }
                    player.getServer().getInfo().sendData("BungeeCord", out.toByteArray() );
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
