package cn.yistars.party.bukkit.addon;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import cn.yistars.party.bukkit.BungeeChannelManager;
import cn.yistars.party.bukkit.Party;
import cn.yistars.party.bukkit.PartyEvent;

import com.walrusone.skywarsreloaded.events.SkyWarsJoinEvent;
import com.walrusone.skywarsreloaded.events.SkyWarsLeaveEvent;
import com.walrusone.skywarsreloaded.SkyWarsReloaded;


public class SkyWarsReloadedHook implements Listener {
    private final HashMap<String, String> MemberReadyGame = new HashMap<>();
    private final HashMap<String, String> PlayerNowGame = new HashMap<>();

    @EventHandler
    private void onGameJoin(SkyWarsJoinEvent event) {

        System.out.println("触发 SkyWarsJoinEvent");
        String playername = event.getPlayer().getName();
        if (Party.PlayerParty.containsKey(playername)) {
            int PartyID = Party.PlayerParty.get(playername);
            if (Party.PartyLeader.get(PartyID).equals(playername)) {
                // 判断是否够人数
                if (PartyEvent.GetAllMember(PartyID, false).size() > event.getGame().getMaxPlayers() - event.getGame().getPlayerCount()) {
                    System.out.println("因为人数不足以进入而被禁止");
                    System.out.println(PartyEvent.GetAllMember(PartyID, true).size());
                    System.out.println(event.getGame().getMaxPlayers());
                    System.out.println(event.getGame().getPlayerCount());
                    System.out.println(event.getGame().getMaxPlayers() - event.getGame().getPlayerCount());
                    event.getGame().removePlayer(event.getPlayer().getUniqueId());
                    BungeeChannelManager.sendPluginMessage(event.getPlayer(), "PartyTooLarge");
                    return;
                }
                PlayerNowGame.put(playername, event.getGame().getName());
                for (String membername : PartyEvent.GetAllMember(PartyID, false)) {
                    Player member = Bukkit.getPlayer(membername);
                    if (member != null) {
                        if (PlayerNowGame.containsKey(membername)) {
                            // 如果不同, 离开游戏并继续
                            if (!event.getGame().getName().equals(PlayerNowGame.get(membername))) {
                                if (SkyWarsReloaded.getAPI().getGameAPI().getGame(member) != null) SkyWarsReloaded.getAPI().getGameAPI().getGame(member).removePlayer(member.getUniqueId());
                            } else {
                                return;
                            }
                        }
                        System.out.println(event.getGame().getName());
                        member.chat("/sw join " + event.getGame().getName());
                    } else {
                        BungeeChannelManager.sendPluginMessage(event.getPlayer(), "SendServer", membername);
                    }
                    MemberReadyGame.put(membername, event.getGame().getName());
                }
            } else {
                // 检测是否有权
                if (MemberReadyGame.containsKey(playername)) {
                    if (MemberReadyGame.get(playername).equals(event.getGame().getName())) {
                        PlayerNowGame.put(playername, event.getGame().getName());
                        return;
                    }
                    event.getGame().removePlayer(event.getPlayer().getUniqueId());
                    BungeeChannelManager.sendPluginMessage(event.getPlayer(), "OnlyLeaderJoin");
                }
            }
        } else {
            PlayerNowGame.put(playername, event.getGame().getName());
        }
    }

    @EventHandler
    private void onGameExit(SkyWarsLeaveEvent event) {
        System.out.println("触发 SkyWarsLeaveEvent");
        String playername = event.getPlayer().getName();
        PlayerNowGame.remove(playername);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        if (MemberReadyGame.containsKey(event.getPlayer().getName())) {
            System.out.println("已提交加入");
            new BukkitRunnable() {
                @Override
                public void run() {
                    event.getPlayer().chat("/sw join " + MemberReadyGame.get(event.getPlayer().getName()));
                }
            }.runTaskLater(Party.instance, 10);
        }
    }
}
