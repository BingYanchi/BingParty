package cn.yistars.party.bukkit.addon;

import cn.yistars.party.bukkit.BungeeChannelManager;
import cn.yistars.party.bukkit.Party;
import cn.yistars.party.bukkit.PartyEvent;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.wazup.towerdefense.events.TDPlayerJoinArenaEvent;
import me.wazup.towerdefense.events.TDArenaStopEvent;
import me.wazup.towerdefense.events.TDPlayerLeaveArenaEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;

public class TowerDefense implements Listener {
    private final HashMap<String, String> MemberReadyGame = new HashMap<>();
    private final HashMap<String, String> PlayerNowGame = new HashMap<>();

    // 此插件缺少最多人数的获取
    @EventHandler
    private void onGameJoin(TDPlayerJoinArenaEvent event) {
        System.out.println("触发 TDPlayerJoinArenaEvent");
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
                            if (!event.getArenaName().equals(PlayerNowGame.get(membername)))
                                member.chat("/td leave");
                        }
                        member.chat("/td join " + event.getArenaName());
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
                    event.getPlayer().chat("/td leave");
                    BungeeChannelManager.sendPluginMessage(event.getPlayer(), "OnlyLeaderJoin");
                }
            }
        } else {
            PlayerNowGame.put(playername, event.getArenaName());
        }
    }
    @EventHandler
    private void onGameEnd(TDArenaStopEvent event) {
        System.out.println("触发 TDArenaStopEvent");
        for (String playername : PlayerNowGame.keySet()) {
            if (PlayerNowGame.get(playername).equals(event.getName())) {
                MemberReadyGame.remove(playername);
                PlayerNowGame.remove(playername);
            }
        }
    }

    @EventHandler
    private void onGameExit(TDPlayerLeaveArenaEvent event) {
        System.out.println("触发 TDPlayerLeaveArenaEvent");
        String playername = event.getPlayer().getName();
        PlayerNowGame.remove(playername);
        MemberReadyGame.remove(playername);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        if (MemberReadyGame.containsKey(event.getPlayer().getName())) {
            System.out.println("已提交加入");
            event.getPlayer().chat("/td join " + MemberReadyGame.get(event.getPlayer().getName()));
        }
    }

}
