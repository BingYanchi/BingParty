package cn.yistars.party.bungee.command;

import cn.yistars.party.bungee.Party;
import cn.yistars.party.bungee.PartyEvent;
import cn.yistars.party.bungee.addon.SupiryRank;
import cn.yistars.party.bungee.channel.ChannelSender;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PartyCommand extends Command implements TabExecutor {
	
    public PartyCommand() {
    	super("party", null, "p", "η»ι", "ιδΌ");
    }
    
    @Override
	public void execute(CommandSender sender, String[] args) {
    	ProxiedPlayer player = (ProxiedPlayer) sender;
        if (!(sender instanceof ProxiedPlayer)) {
        	SendMessage(player, "Console");
            return;
        }
        
        if (args.length == 0) {
        	SendMessage(player, "Line");
        	SendMessage(player, "HelpTitle");
        	SendMessage(player, "HelpAccept");
        	SendMessage(player, "HelpInvite");
        	SendMessage(player, "HelpList");
        	SendMessage(player, "HelpLeave");
        	SendMessage(player, "HelpWarp");
        	SendMessage(player, "HelpDisband");
        	SendMessage(player, "HelpPromote");
        	SendMessage(player, "HelpDemote");
        	SendMessage(player, "HelpTransfer");
        	SendMessage(player, "HelpKick");
        	SendMessage(player, "HelpKickOffline");
        	SendMessage(player, "HelpSettings");
        	SendMessage(player, "HelpChat");
        	SendMessage(player, "HelpMute");
        	SendMessage(player, "Line");
        } else {
        	String subcommand = args[0].toLowerCase();
        	String playername = player.getName();
        	switch(subcommand) {
        		case "accept":
        			if (args.length == 2) {
        				if (Party.PartyInvite.containsKey(playername)) {
							ArrayList<String> inviters = Party.PartyInvite.get(playername);
            				for (String inviter : inviters) {
            					if (inviter.equals(args[1])) {
            						// ε¦ζζθΏδΈͺη©ε?Άηθ―·ζ±
            						// ζ£ζ΅η©ε?Άζ―ε¦ε·²η»ζη»ιδΊ
                    				if (Party.PlayerParty.containsKey(playername)) {
                    					SendMessage(player, "Line");
                            			SendMessage(player, "YouAlreadyInParty");
                            			SendMessage(player, "Line");
                    					return;
                    				}
                    				if (!Party.PlayerParty.containsKey(inviter)) {
                    					continue;
                    				}
            						// θ·ειθ―·δΊΊηη»ι ID
            						Integer PartyID = Party.PlayerParty.get(inviter);
            						// ιη₯ζζδΊΊ, θΏδΈͺη©ε?ΆθΏε₯δΊη»ι
            						SendAllMember(PartyID, "Line");
            						SendAllMember(PartyID, "PartyJoin", SupiryRank.RankFormat(player) + playername);
            						SendAllMember(PartyID, "Line");
            						// ιη₯ζ¬δΊΊ, ε·²θΏε₯η»ι
            						SendMessage(player, "Line");
            						SendMessage(player, "SuccessJoin", Party.Rank.get(Party.PartyLeader.get(PartyID)) + Party.PartyLeader.get(PartyID));
            						ArrayList<String> oldmembers = PartyEvent.GetAllMember(PartyID, false);
            						// ε¦ζηιδΌιθΏζεΆδ»ζε
            						if (oldmembers.size() != 0) {
            							StringBuilder oldmember = new StringBuilder();
            							for (String member : oldmembers) {
            								oldmember.append(" ").append(Party.Rank.get(member)).append(member);
            							}
            							SendMessage(player, "None");
            							SendMessage(player, "SuccessJoinMember", oldmember.toString());
            						}
            						SendMessage(player, "Line");
            						// ε€ηε ε₯η»ι
            						ArrayList<String> members = Party.PartyMember.get(PartyID);
            						members.add(playername);
            						Party.PartyMember.put(PartyID, members);
            						Party.PlayerParty.put(playername, PartyID);
            						Party.PlayerRole.put(playername, "member");
            						//  εε°ιθ―·ζ°ε­
            						Integer InviteNumber = Party.PartyInviteNumber.get(PartyID) - 1;
            						Party.PartyInviteNumber.put(PartyID, InviteNumber);
            						// δΌ ιε ε₯ζε
            						ChannelSender.SendAddMember(PartyID, player, Party.PartyLeader.get(PartyID));
            						// ζ·»ε  UUID
            						Party.PlayerUUID.put(playername, player.getUniqueId().toString());
            						// ζ·»ε ε¨ηΊΏηΆζ
            						Party.OnlineStat.put(playername, true);
            						// ζ΄ζ°η©ε?Άζε¨ζε‘ε¨
            						PartyEvent.UpdatePlayerServer(player);
            						// ιη₯ζζζε‘ε¨ζ΄ζ°
            						PartyEvent.SendAllServerUpdate();
                    				// ζΈι€ζθΏδΈͺθ―·ζ±
            						if (inviters.size() == 1) {
            							Party.PartyInvite.remove(playername);
									} else {
            							inviters.remove(inviter);
            							Party.PartyInvite.put(playername, inviters);
									}
									return;
								}
            				}
        				}
        				SendMessage(player, "Line");
        				SendMessage(player, "NoPlayerInviter");
					} else {
        				SendMessage(player, "Line");
        				SendMessage(player, "UseAccept");
					}
					SendMessage(player, "Line");
					break;
        		case "invite":
        			if (args.length >= 2) {
        				for (int i =1; i<=(args.length-1); i++) {
        					PartyEvent.SendPartyInvite(player, args[i]);
        				}
        			} else {
        				SendMessage(player, "Line");
        				SendMessage(player, "UseInvite");
        				SendMessage(player, "Line");
        			}
        			break;
        		case "list":
        			if (Party.PlayerParty.get(playername) != null) {
        				Integer PartyID = Party.PlayerParty.get(playername);
        				SendMessage(player, "Line");
        				// θ?‘η?δΊΊζ°
        				int membernum = PartyEvent.GetAllMember(PartyID, true).size();
            			SendMessage(player, "PartyListTitle", Integer.toString(membernum));
            			SendMessage(player, "None");
            			// ιιΏ
            			ArrayList<String> members = new ArrayList<>();
            			members.add(Party.PartyLeader.get(PartyID));
            			SendPartyList(player, "PartyLeader", members);
            			// ε¦ζζιεζε―ιιΏ, ηδΈͺη©Ίζ Ό
            			if (Party.PartyMod.get(PartyID).size() != 0 || Party.PartyMember.get(PartyID).size() != 0) {
            				SendMessage(player, "None");
            			}
            			
            			if (Party.PartyMod.get(PartyID).size() != 0) {
							members = new ArrayList<>(Party.PartyMod.get(PartyID));
                			SendPartyList(player, "PartyMod", members);
            			}
            			
            			if (Party.PartyMember.get(PartyID).size() != 0) {
							members = new ArrayList<>(Party.PartyMember.get(PartyID));
                			SendPartyList(player, "PartyMember", members);
            			}
					} else {
        				SendMessage(player, "Line");
        				SendMessage(player, "NoInParty");
					}
					SendMessage(player, "Line");
					break;
        		case "leave":
        			if (Party.PlayerParty.get(playername) != null) {
        				Integer PartyID = Party.PlayerParty.get(playername);
        				// εθ―ζ¬δΊΊζειεΊ
        				SendMessage(player, "Line");
        				SendMessage(player, "YouLeaveParty");
        				SendMessage(player, "Line");
        				// θ·εεεθΊ«δ»½
        				String role = Party.PlayerRole.get(playername);
        				// ζ§θ‘ιεΊ
        				PartyEvent.KickMember(PartyID, playername);
        				// ε¦ζιδΌδΈε­ηεη»ζ
        				if (!Party.PartyLeader.containsKey(PartyID)) {
        					return;
        				}
        				// ιη₯ζζδΊΊ
        				SendAllMember(PartyID, "Line");
						SendAllMember(PartyID, "PartyLeave", SupiryRank.RankFormat(player) + playername);
						SendAllMember(PartyID, "Line");
        				// ε¦ζζ―ιιΏει’ε€ζΆζ―
						if (role.equals("leader") && PartyEvent.GetAllMember(PartyID, false).size() != 0) {
							SendAllMember(PartyID, "Line");
							SendAllMember(PartyID, "PartyLeaderLeave", SupiryRank.RankFormat(player) + playername, Party.Rank.get(Party.PartyLeader.get(PartyID)) + Party.PartyLeader.get(PartyID));
							SendAllMember(PartyID, "Line");
						}
						// ζ£ζ΅η»ιζ―ε¦δ»ε©δΈδΊΊ
		    			PartyEvent.CheckNoMemberParty(PartyID);
        			} else {
        				SendMessage(player, "Line");
        				SendMessage(player, "NoInParty");
        				SendMessage(player, "Line");
        			}
        			break;
        		case "warp":
        			if (Party.PlayerRole.containsKey(playername)) {
        				// ζδ»ηη»ιδΏ‘ζ―
        				if (Party.PlayerRole.get(playername).equals("leader")) {
        					Integer PartyID = Party.PlayerParty.get(playername);
        					
        					String warpServer = player.getServer().getInfo().getName();
        					String warpmembers = PartyEvent.WarpParty(PartyID, warpServer, true);
        					
        			    	// ιη₯ιιΏ
        					if (warpmembers != null) {
        						SendMessage(player, "Line");
            			    	SendMessage(player, "SuccessWarp", warpmembers);
            			    	SendMessage(player, "Line");
        					}
        				} else {
        					SendMessage(player, "Line");
        					SendMessage(player, "NoLeader");
        					SendMessage(player, "Line");
        				}
        			} else {
        				// ζ²‘ζιδΌ
        				SendMessage(player, "Line");
        				SendMessage(player, "NoInParty");
        				SendMessage(player, "Line");
        			}
        			break;
        		case "disband":
        			if (Party.PlayerParty.containsKey(playername)) {
        				if (Party.PlayerRole.get(playername).equals("leader")) {
            				Integer PartyID = Party.PlayerParty.get(playername);
            				// ιη₯ζζδΊΊ
            				SendAllMember(PartyID, "Line");
            				SendAllMember(PartyID, "PartyDisband", SupiryRank.RankFormat(player) + playername);
            				SendAllMember(PartyID, "Line");
            				// ζ§θ‘θ§£ζ£
            				PartyEvent.DisbandParty(PartyID);
            			} else {
            				SendMessage(player, "Line");
            				SendMessage(player, "NoDisband");
            				SendMessage(player, "Line");
            			}
        			} else {
        				// ζ²‘ζιδΌ
        				SendMessage(player, "Line");
        				SendMessage(player, "NoInParty");
        				SendMessage(player, "Line");
        			}
        			break;
        		case "promote":
        			if (args.length == 2) {
        				// θ―ε?ιιΏζι
        				if (Party.PlayerRole.get(playername).equals("leader")) {
        					// θ·ειδΌ 
        					Integer PartyID = Party.PlayerParty.get(playername);
        					// η‘?θ?€δΈζ―ζ¬δΊΊ
        					if (args[1].equals(playername)) {
        						SendMessage(player, "Line");
        						SendMessage(player, "NoPromoteSelf");
        						SendMessage(player, "Line");
        						return;
        					}
        					// η‘?θ?€ζ―ε¦ε¨ιδΌδΈ­
        					if (PartyEvent.CheckInParty(PartyID, args[1], false)) {
        						switch (Party.PlayerRole.get(args[1])) {
        							case "member":
        								PartyEvent.ChangeRole(PartyID, args[1], "mod");
        								// ιη₯ζζδΊΊ
        								SendAllMember(PartyID, "Line");
        								SendAllMember(PartyID, "RolePromote", SupiryRank.RankFormat(player) + playername, Party.Rank.get(args[1]) + args[1], "Mod");
        								SendAllMember(PartyID, "Line");
        								break;
        							case "mod":
        								PartyEvent.ChangeRole(PartyID, args[1], "leader");
        								SendAllMember(PartyID, "Line");
        								SendAllMember(PartyID, "RolePromote", SupiryRank.RankFormat(player) + playername, Party.Rank.get(args[1]) + args[1], "Leader");
        								SendAllMember(PartyID, "RoleMod", SupiryRank.RankFormat(player) + playername);
        								SendAllMember(PartyID, "Line");
        								break;
        						}
        					} else {
        						SendMessage(player, "Line");
                				SendMessage(player, "NotInYourParty");
                				SendMessage(player, "Line");
        					}
        				} else {
            				SendMessage(player, "Line");
            				SendMessage(player, "NoLeader");
            				SendMessage(player, "Line");
        				}
        			} else {
        				SendMessage(player, "Line");
        				SendMessage(player, "UsePromote");
        				SendMessage(player, "Line");
        			}
        			break;
        		case "demote":
        			if (args.length == 2) {
        				if (Party.PlayerRole.get(playername).equals("leader")) {
        					Integer PartyID = Party.PlayerParty.get(playername);
        					if (args[1].equals(playername)) {
        						SendMessage(player, "Line");
        						SendMessage(player, "NoPromoteSelf");
        						SendMessage(player, "Line");
        						return;
        					}
        					if (PartyEvent.CheckInParty(PartyID, args[1], false)) {
        						if (Party.PlayerRole.get(args[1]).equals("mod")) {
        							PartyEvent.ChangeRole(PartyID, args[1], "member");
        							SendAllMember(PartyID, "Line");
    								SendAllMember(PartyID, "RoleDemote", SupiryRank.RankFormat(player) + playername, Party.Rank.get(args[1]) + args[1], "Member");
    								SendAllMember(PartyID, "Line");
        						} else {
        							SendMessage(player, "Line");
            						SendMessage(player, "NoDemote");
            						SendMessage(player, "Line");
        						}
        					}
        				} else {
            				SendMessage(player, "Line");
            				SendMessage(player, "NoLeader");
            				SendMessage(player, "Line");
        				}
        			} else {
        				SendMessage(player, "Line");
        				SendMessage(player, "UseDemote");
        				SendMessage(player, "Line");
        			}
        			break;
        		case "transfer":
        			if (args.length == 2) {
            			if (Party.PlayerRole.get(playername).equals("leader")) {
            				Integer PartyID = Party.PlayerParty.get(playername);
            				if (PartyEvent.CheckInParty(PartyID, args[1], false)) {
            					PartyEvent.ChangeRole(PartyID, args[1], "leader");
            					// η»ζζδΊΊεζΆζ―
            					SendAllMember(PartyID, "Line");
            					SendAllMember(PartyID, "SuccessTransfer", SupiryRank.RankFormat(player) + playername, Party.Rank.get(args[1]) + args[1]);
            					SendAllMember(PartyID, "Line");
            				} else {
            					SendMessage(player, "Line");
            					SendMessage(player, "NotInYourParty");
            					SendMessage(player, "Line");
            				}
            			} else {
            				SendMessage(player, "Line");
            				SendMessage(player, "NoLeader");
            				SendMessage(player, "Line");
            			}
        			} else {
        				SendMessage(player, "Line");
        				SendMessage(player, "UseTransfer");
        				SendMessage(player, "Line");
        			}
        			break;
        		case "kick":
        			if (args.length == 2) {
        				if (Party.PlayerParty.containsKey(playername)) {
							if (Party.PlayerRole.get(playername).equals("leader")) {
								Integer PartyID = Party.PlayerParty.get(playername);
								if (PartyEvent.CheckInParty(PartyID, args[1], false)) {
									PartyEvent.KickMember(PartyID, args[1]);
									// ιη₯ζζδΊΊ
									SendAllMember(PartyID, "Line");
									SendAllMember(PartyID, "PartyKick", Party.Rank.get(args[1]) + args[1]);
									SendAllMember(PartyID, "Line");
									// ιη₯θ’«θΈ’η
									ProxiedPlayer kickplayer = ProxyServer.getInstance().getPlayer(args[1]);
									if (kickplayer != null) {
										SendMessage(kickplayer, "Line");
										SendMessage(kickplayer, "PlayerKicked", SupiryRank.RankFormat(player) + playername);
										SendMessage(kickplayer, "Line");
									}
									// ζ£ζ΅η»ιζ―ε¦δ»ε©δΈδΊΊ
									PartyEvent.CheckNoMemberParty(PartyID);
								} else {
									SendMessage(player, "Line");
									SendMessage(player, "NotInYourParty");
									SendMessage(player, "Line");
								}
							} else {
								SendMessage(player, "Line");
								SendMessage(player, "NoLeader");
								SendMessage(player, "Line");
							}
						} else {
							SendMessage(player, "Line");
							SendMessage(player, "NoInParty");
							SendMessage(player, "Line");
						}
        			} else {
        				SendMessage(player, "Line");
        				SendMessage(player, "UseKick");
        				SendMessage(player, "Line");
        			}
        			break;
        		case "kickoffline":
        			if (Party.PlayerRole.get(playername).equals("leader")) {
        				Integer PartyID = Party.PlayerParty.get(playername);
        				ArrayList<String> members = PartyEvent.GetAllMember(PartyID, false);
        				ArrayList<String> offlinemembers = new ArrayList<>();
        				for (String member : members) {
        					if (!(Party.OnlineStat.get(member))) {
        						offlinemembers.add(member);
        					}
        				}
        				// ε€ζ­ζ―ε¦δΈΊη©Ί
        				if (offlinemembers.size() == 0) {
        					SendMessage(player, "Line");
        					SendMessage(player, "NoKickOffline");
        					SendMessage(player, "Line");
        				} else {
        					// ιη₯ζζδΊΊ
        					StringBuilder displayname = new StringBuilder();
        					for (String member : offlinemembers) {
        						displayname.append(" ").append(Party.Rank.get(member)).append(member);
        						// η§»ι€η©ε?Ά
        						PartyEvent.KickMember(PartyID, member);
        					}
        					SendAllMember(PartyID, "Line");
        					SendAllMember(PartyID, "SuccessKick", displayname.toString());
        					SendAllMember(PartyID, "Line");
        				}
        				// ζ£ζ΅η»ιζ―ε¦δ»ε©δΈδΊΊ
		    			PartyEvent.CheckNoMemberParty(PartyID);
        			} else {
        				SendMessage(player, "Line");
        				SendMessage(player, "NoLeader");
        				SendMessage(player, "Line");
        			}
        			break;
        		case "setting":
        		case "settings":
        			if (args.length == 2) {
        				if (args[1].equalsIgnoreCase("allinvite")) {
        					if (Party.PlayerRole.containsKey(playername)) {
        						if (Party.PlayerRole.get(playername).equals("leader")) {
        							Integer PartyID = Party.PlayerParty.get(playername);
        							// εζ’ε°εθ?ΈζζδΊΊιθ―·
        							if (!Party.PartyAllInvite.get(PartyID)) {
        								Party.PartyAllInvite.put(PartyID, true);
        								SendAllMember(PartyID, "Line");
        	        					SendAllMember(PartyID, "EnableSetting", SupiryRank.RankFormat(player) + playername, Party.Messages.get("AllInvite"));
									} else {
        								Party.PartyAllInvite.put(PartyID, false);
        								SendAllMember(PartyID, "Line");
        	        					SendAllMember(PartyID, "DisableSetting", SupiryRank.RankFormat(player) + playername, Party.Messages.get("AllInvite"));
									}
									SendAllMember(PartyID, "Line");
								} else {
        							SendMessage(player, "Line");
                    				SendMessage(player, "NoLeader");
                    				SendMessage(player, "Line");
								}
							} else {
        						SendMessage(player, "Line");
                				SendMessage(player, "NoInParty");
                				SendMessage(player, "Line");
							}
							return;
						}
        			}
        			SendMessage(player, "Line");
        			SendMessage(player, "SettingTitle");
        			SendMessage(player, "SettingInvite");
        			SendMessage(player, "SettingPrivate");
        			SendMessage(player, "Line");
        			break;
        		case "chat":
        			if (args.length >= 2) {
        				if (Party.PlayerParty.containsKey(playername)) {
            				Integer PartyID = Party.PlayerParty.get(playername);
            				if (Party.PartyMute.get(PartyID)) {
            					if (Party.PlayerRole.get(playername).equals("member") && !Party.Staff.contains(playername)) {
            						SendMessage(player, "Line");
            						SendMessage(player, "PartyChatMute");
            						SendMessage(player, "Line");
            						return;
            					}
            				}
            				// ε€η
            				StringBuilder message = new StringBuilder();
            	    		for (int i =1; i<=(args.length-1); i++) {
            	    			message.append(args[i]).append(" ");
            	    		}
            	    		
            	    		message = new StringBuilder(message.substring(0, message.length() - 1));
            	    		PartyEvent.PartyChat(player, message.toString());
            				
            			} else {
            				SendMessage(player, "Line");
            				SendMessage(player, "NoInParty");
            				SendMessage(player, "Line");
            			}
        			} else {
        				SendMessage(player, "Line");
        				SendMessage(player, "UseChat");
        				SendMessage(player, "Line");
        			}
        			break;
        		case "mute":
					if (Party.PlayerRole.containsKey(playername)) {
						if (Party.PlayerRole.get(playername).equals("leader")) {
							Integer PartyID = Party.PlayerParty.get(playername);
							// εζ’ε°εθ?ΈζζδΊΊιθ―·
							if (!Party.PartyMute.get(PartyID)) {
								Party.PartyMute.put(PartyID, true);
								SendAllMember(PartyID, "Line");
	        					SendAllMember(PartyID, "EnableMuteTitle");
	        					SendAllMember(PartyID, "EnableMute");
							} else {
								Party.PartyMute.put(PartyID, false);
								SendAllMember(PartyID, "Line");
	        					SendAllMember(PartyID, "DisableMute");
							}
							SendAllMember(PartyID, "Line");
						} else {
							SendMessage(player, "Line");
            				SendMessage(player, "NoLeader");
            				SendMessage(player, "Line");
						}
						return;
					} else {
						SendMessage(player, "Line");
        				SendMessage(player, "NoInParty");
        				SendMessage(player, "Line");
					}
					break;
				default:
					for (int i =0; i<=(args.length-1); i++) {
    					PartyEvent.SendPartyInvite(player, args[i]);
    				}
        	}
        }
    }

    public static void SendHoverMessage(ProxiedPlayer player, String name, String honver, String... args) {
    	String msg = Party.Messages.get(name);
    	honver = Party.Messages.get(honver);
    	String command = "";

		if ("GetInvite".equals(name)) {
			//msg = msg.replace("%player%", args[0]);
			msg = msg.replace("%time%", args[1]);
			honver = honver.replace("%player%", args[0]);
			command = "/party accept " + args[0];
		}
    	msg = ChatColor.translateAlternateColorCodes('&', msg);
    	honver = ChatColor.translateAlternateColorCodes('&', honver);
    	
    	TextComponent component = new TextComponent(msg);
        component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(honver).create()));
        component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        player.sendMessage(component);
    }
    
    public static void SendAllMember(Integer PartyID, String message, String... args) {
    	ArrayList<String> members = PartyEvent.GetAllMember(PartyID, true);
    	
    	// ειε½ζ°
    	for (String playername : members) {
    		ProxiedPlayer memberplayer = ProxyServer.getInstance().getPlayer(playername);
    		if (memberplayer != null) {
    			SendMessage(memberplayer, message, args);
    		}
    	}
    }
    
    public static void SendPartyList(ProxiedPlayer player, String name, ArrayList<String> players) {
    	StringBuilder msg = new StringBuilder(Party.Messages.get(name));
    	
    	for (String playername : players) {
    		String displayname = " " + Party.Rank.get(playername) + playername;
    		if (Party.OnlineStat.get(playername)) {
    			displayname = displayname + " " + Party.Messages.get("PartyOnline");
    		} else {
    			displayname = displayname + " " + Party.Messages.get("PartyOffline");
    		}
    		msg.append(displayname);
    	}
    	
    	msg = new StringBuilder(ChatColor.translateAlternateColorCodes('&', msg.toString()));
        player.sendMessage(new TextComponent(msg.toString()));
    }
    
    public static void SendMessage(ProxiedPlayer player, String name, String... args) {
    	String msg = Party.Messages.get(name);
    	
    	// ζΏζ’ει
    	switch (name) {
    		case "SuccessInvite":
    			msg = msg.replace("%player%", args[0]);
    			msg = msg.replace("%inviteplayer%", args[1]);
    			msg = msg.replace("%time%", args[2]);
    			break;
    		case "AlreadyInParty": case "PlayerKicked": case "PartyKick": case "SuccessKick": case "InviteExpired": case "RoleMod": case "PartyJoin": case "PartyLeave": case "PartyDisband": case "SuccessJoin":
    		case "SuccessJoinMember": case "SuccessWarp": case "PartyWarp": case "PartyExpired": case "AlreadyInviter": case "GetInviteInfo": case "PartyServerKick":
    			msg = msg.replace("%player%", args[0]);
    			break;
    		case "SuccessTransfer":
    			msg = msg.replace("%leader%", args[0]);
    			msg = msg.replace("%newleader%", args[1]);
    			break;
    		case "RolePromote": case "RoleDemote":
    			msg = msg.replace("%player%", args[0]);
    			msg = msg.replace("%roleplayer%", args[1]);
    			msg = msg.replace("%role%", Party.Messages.get(args[2]));
    			break;
    		case "PartyListTitle":
    			msg = msg.replace("%number%", args[0]);
    			break;
    		case "PartyLeaderLeave":
    			msg = msg.replace("%player%", args[0]);
    			msg = msg.replace("%leader%", args[1]);
    			break;
    		case "PartyServerQuit": case "PartyLeaderServerQuit":
    			msg = msg.replace("%player%", args[0]);
    			msg = msg.replace("%time%", args[1]);
    			break;
    		case "EnableSetting": case "DisableSetting":
    			msg = msg.replace("%player%", args[0]);
    			msg = msg.replace("%setting%", args[1]);
    			break;
    		case "PartyChat":
    			msg = msg.replace("%player%", args[0]);
    			msg = msg.replace("%message%", args[1]);
    			break;
    		case "PartyTooLarge":
    			msg = msg.replace("%size%", args[0]);
    	}
		
		if (!(msg == null || msg.length() <= 0)) {
    		msg = ChatColor.translateAlternateColorCodes('&', msg);
            player.sendMessage(new TextComponent(msg));
    	}
    }
    
    public Iterable<String> onTabComplete(final CommandSender sender, final String[] args) {
    	switch (args.length) {
    		case 1:
    			return Arrays.asList("accept", "invite", "list", "leave", "warp", "disband", "promote", "demote", "transfer", "kick", "kickoffline", "settings","chat", "mute");
    		case 2:
    			if (args[0].equalsIgnoreCase("setting") || args[0].equalsIgnoreCase("settings")) {
    				return Collections.singletonList("allinvite");
    			} else if (args[0].equalsIgnoreCase("invite") || !(Party.PlayerParty.containsKey(sender.getName()))) {
    				final List<String> list = new ArrayList<>();
    				for(final ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
    					list.add(player.getName());
    				}
    				list.remove(sender.getName());
    				return list;
    			} else {
    				return PartyEvent.GetAllMember(Party.PlayerParty.get(sender.getName()), false);
    			}
    		default:
    			if (args[0].equalsIgnoreCase("invite") || !(Party.PlayerParty.containsKey(sender.getName()))) {
    				final List<String> list = new ArrayList<>();
    				for(final ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
    					list.add(player.getName());
    				}
    				list.remove(sender.getName());
    				return list;
    			}
    			return Collections.emptyList();
    	}
	}
}
