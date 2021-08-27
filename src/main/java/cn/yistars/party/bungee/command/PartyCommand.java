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
    	super("party", null, "p", "组队", "队伍");
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
            						// 如果有这个玩家的请求
            						// 检测玩家是否已经有组队了
                    				if (Party.PlayerParty.containsKey(playername)) {
                    					SendMessage(player, "Line");
                            			SendMessage(player, "YouAlreadyInParty");
                            			SendMessage(player, "Line");
                    					return;
                    				}
                    				if (!Party.PlayerParty.containsKey(inviter)) {
                    					continue;
                    				}
            						// 获取邀请人的组队 ID
            						Integer PartyID = Party.PlayerParty.get(inviter);
            						// 通知所有人, 这个玩家进入了组队
            						SendAllMember(PartyID, "Line");
            						SendAllMember(PartyID, "PartyJoin", SupiryRank.RankFormat(player) + playername);
            						SendAllMember(PartyID, "Line");
            						// 通知本人, 已进入组队
            						SendMessage(player, "Line");
            						SendMessage(player, "SuccessJoin", Party.Rank.get(Party.PartyLeader.get(PartyID)) + Party.PartyLeader.get(PartyID));
            						ArrayList<String> oldmembers = PartyEvent.GetAllMember(PartyID, false);
            						// 如果的队伍里还有其他成员
            						if (oldmembers.size() != 0) {
            							StringBuilder oldmember = new StringBuilder();
            							for (String member : oldmembers) {
            								oldmember.append(" ").append(Party.Rank.get(member)).append(member);
            							}
            							SendMessage(player, "None");
            							SendMessage(player, "SuccessJoinMember", oldmember.toString());
            						}
            						SendMessage(player, "Line");
            						// 处理加入组队
            						ArrayList<String> members = Party.PartyMember.get(PartyID);
            						members.add(playername);
            						Party.PartyMember.put(PartyID, members);
            						Party.PlayerParty.put(playername, PartyID);
            						Party.PlayerRole.put(playername, "member");
            						//  减少邀请数字
            						Integer InviteNumber = Party.PartyInviteNumber.get(PartyID) - 1;
            						Party.PartyInviteNumber.put(PartyID, InviteNumber);
            						// 传递加入成员
            						ChannelSender.SendAddMember(PartyID, player, Party.PartyLeader.get(PartyID));
            						// 添加 UUID
            						Party.PlayerUUID.put(playername, player.getUniqueId().toString());
            						// 添加在线状态
            						Party.OnlineStat.put(playername, true);
            						// 更新玩家所在服务器
            						PartyEvent.UpdatePlayerServer(player);
            						// 通知所有服务器更新
            						PartyEvent.SendAllServerUpdate();
                    				// 清除掉这个请求
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
        				// 计算人数
        				int membernum = PartyEvent.GetAllMember(PartyID, true).size();
            			SendMessage(player, "PartyListTitle", Integer.toString(membernum));
            			SendMessage(player, "None");
            			// 队长
            			ArrayList<String> members = new ArrayList<>();
            			members.add(Party.PartyLeader.get(PartyID));
            			SendPartyList(player, "PartyLeader", members);
            			// 如果有队员或副队长, 留个空格
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
        				// 告诉本人成功退出
        				SendMessage(player, "Line");
        				SendMessage(player, "YouLeaveParty");
        				SendMessage(player, "Line");
        				// 获取原先身份
        				String role = Party.PlayerRole.get(playername);
        				// 执行退出
        				PartyEvent.KickMember(PartyID, playername);
        				// 如果队伍不存着则结束
        				if (!Party.PartyLeader.containsKey(PartyID)) {
        					return;
        				}
        				// 通知所有人
        				SendAllMember(PartyID, "Line");
						SendAllMember(PartyID, "PartyLeave", SupiryRank.RankFormat(player) + playername);
						SendAllMember(PartyID, "Line");
        				// 如果是队长则额外消息
						if (role.equals("leader") && PartyEvent.GetAllMember(PartyID, false).size() != 0) {
							SendAllMember(PartyID, "Line");
							SendAllMember(PartyID, "PartyLeaderLeave", SupiryRank.RankFormat(player) + playername, Party.Rank.get(Party.PartyLeader.get(PartyID)) + Party.PartyLeader.get(PartyID));
							SendAllMember(PartyID, "Line");
						}
						// 检测组队是否仅剩一人
		    			PartyEvent.CheckNoMemberParty(PartyID);
        			} else {
        				SendMessage(player, "Line");
        				SendMessage(player, "NoInParty");
        				SendMessage(player, "Line");
        			}
        			break;
        		case "warp":
        			if (Party.PlayerRole.containsKey(playername)) {
        				// 有他的组队信息
        				if (Party.PlayerRole.get(playername).equals("leader")) {
        					Integer PartyID = Party.PlayerParty.get(playername);
        					
        					String warpServer = player.getServer().getInfo().getName();
        					String warpmembers = PartyEvent.WarpParty(PartyID, warpServer, true);
        					
        			    	// 通知队长
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
        				// 没有队伍
        				SendMessage(player, "Line");
        				SendMessage(player, "NoInParty");
        				SendMessage(player, "Line");
        			}
        			break;
        		case "disband":
        			if (Party.PlayerParty.containsKey(playername)) {
        				if (Party.PlayerRole.get(playername).equals("leader")) {
            				Integer PartyID = Party.PlayerParty.get(playername);
            				// 通知所有人
            				SendAllMember(PartyID, "Line");
            				SendAllMember(PartyID, "PartyDisband", SupiryRank.RankFormat(player) + playername);
            				SendAllMember(PartyID, "Line");
            				// 执行解散
            				PartyEvent.DisbandParty(PartyID);
            			} else {
            				SendMessage(player, "Line");
            				SendMessage(player, "NoDisband");
            				SendMessage(player, "Line");
            			}
        			} else {
        				// 没有队伍
        				SendMessage(player, "Line");
        				SendMessage(player, "NoInParty");
        				SendMessage(player, "Line");
        			}
        			break;
        		case "promote":
        			if (args.length == 2) {
        				// 证实队长权限
        				if (Party.PlayerRole.get(playername).equals("leader")) {
        					// 获取队伍 
        					Integer PartyID = Party.PlayerParty.get(playername);
        					// 确认不是本人
        					if (args[1].equals(playername)) {
        						SendMessage(player, "Line");
        						SendMessage(player, "NoPromoteSelf");
        						SendMessage(player, "Line");
        						return;
        					}
        					// 确认是否在队伍中
        					if (PartyEvent.CheckInParty(PartyID, args[1], false)) {
        						switch (Party.PlayerRole.get(args[1])) {
        							case "member":
        								PartyEvent.ChangeRole(PartyID, args[1], "mod");
        								// 通知所有人
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
            					// 给所有人发消息
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
									// 通知所有人
									SendAllMember(PartyID, "Line");
									SendAllMember(PartyID, "PartyKick", Party.Rank.get(args[1]) + args[1]);
									SendAllMember(PartyID, "Line");
									// 通知被踢的
									ProxiedPlayer kickplayer = ProxyServer.getInstance().getPlayer(args[1]);
									if (kickplayer != null) {
										SendMessage(kickplayer, "Line");
										SendMessage(kickplayer, "PlayerKicked", SupiryRank.RankFormat(player) + playername);
										SendMessage(kickplayer, "Line");
									}
									// 检测组队是否仅剩一人
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
        				// 判断是否为空
        				if (offlinemembers.size() == 0) {
        					SendMessage(player, "Line");
        					SendMessage(player, "NoKickOffline");
        					SendMessage(player, "Line");
        				} else {
        					// 通知所有人
        					StringBuilder displayname = new StringBuilder();
        					for (String member : offlinemembers) {
        						displayname.append(" ").append(Party.Rank.get(member)).append(member);
        						// 移除玩家
        						PartyEvent.KickMember(PartyID, member);
        					}
        					SendAllMember(PartyID, "Line");
        					SendAllMember(PartyID, "SuccessKick", displayname.toString());
        					SendAllMember(PartyID, "Line");
        				}
        				// 检测组队是否仅剩一人
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
        							// 切换到允许所有人邀请
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
            				// 处理
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
							// 切换到允许所有人邀请
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
    	
    	// 发送函数
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
    	
    	// 替换变量
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
