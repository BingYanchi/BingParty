package cn.yistars.party.bukkit.addon;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import cn.yistars.party.bukkit.BungeeChannelManager;
import cn.yistars.party.bukkit.Party;
import cn.yistars.party.bukkit.PartyEvent;

import me.wazup.dragonescape.events.DEPlayerJoinArenaEvent;
import me.wazup.dragonescape.events.DEPlayerLeaveArenaEvent;

public class DragonEscape implements Listener {
    private final HashMap<String, String> MemberReadyGame = new HashMap<>();
    private final HashMap<String, String> PlayerNowGame = new HashMap<>();

    @EventHandler
    private void onGameJoin(DEPlayerJoinArenaEvent event) {
        System.out.println("触发 DEPlayerLeaveArenaEvent");
        String playername = event.getPlayer().getName();
        if (Party.PlayerParty.containsKey(playername)) {
            int PartyID = Party.PlayerParty.get(playername);
            if (Party.PartyLeader.get(PartyID).equals(playername)) {
                PlayerNowGame.put(playername, event.getArenaName());
                for (String membername : PartyEvent.GetAllMember(PartyID, false)) {
                    Player member = Bukkit.getPlayer(membername);
                    if (member != null) {
                        if (PlayerNowGame.containsKey(membername)) {
                            // 如果不同, 离开游戏并继续
                            if (!event.getArenaName().equals(PlayerNowGame.get(membername))) {
                                member.chat("/DragonEscape leave");
                            } else {
                                return;
                            }
                        }
                        member.chat("/DragonEscape join " + event.getArenaName());
                    } else {
                        BungeeChannelManager.sendPluginMessage(event.getPlayer(), "SendServer", membername);
                    }
                    MemberReadyGame.put(membername, event.getArenaName());
                }
            } else {
                // 检测是否有权
                if (MemberReadyGame.containsKey(playername)) {
                    if (MemberReadyGame.get(playername).equals(event.getArenaName())) {
                        PlayerNowGame.put(playername, event.getArenaName());
                        return;
                    }
                    event.getPlayer().chat("/DragonEscape leave");
                    BungeeChannelManager.sendPluginMessage(event.getPlayer(), "OnlyLeaderJoin");
                }
            }
        } else {
            PlayerNowGame.put(playername, event.getArenaName());
        }
    }

    @EventHandler
    private void onGameExit(DEPlayerLeaveArenaEvent event) {
        System.out.println("触发 DEPlayerLeaveArenaEvent");
        String playername = event.getPlayer().getName();
        PlayerNowGame.remove(playername);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        if (MemberReadyGame.containsKey(event.getPlayer().getName())) {
            System.out.println("已提交加入");
            event.getPlayer().chat("/DragonEscape join " + MemberReadyGame.get(event.getPlayer().getName()));
        }
    }
}
