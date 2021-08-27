package cn.yistars.party.bukkit.addon;

import cn.yistars.party.bukkit.BungeeChannelManager;
import cn.yistars.party.bukkit.Party;
import cn.yistars.party.bukkit.PartyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import ro.fr33styler.arcade.api.ArcadeAPI;
import ro.fr33styler.arcade.api.event.ArcadeJoinEvent;
import ro.fr33styler.arcade.api.event.ArcadeLeaveEvent;
import ro.fr33styler.arcade.api.event.ArcadeStopEvent;

import java.util.HashMap;

public class Arcade implements Listener {
    private final HashMap<String, String> MemberReadyGame = new HashMap<>();
    private final HashMap<String, String> MemberReadyId = new HashMap<>();
    private final HashMap<String, String> PlayerNowGame = new HashMap<>();
    private final HashMap<String, String> PlayerNowId = new HashMap<>();

    @EventHandler
    private void onGameJoin(ArcadeJoinEvent event) {
        System.out.println("触发 ArcadeJoinEvent");
        String playername = event.getPlayer().getName();
        if (Party.PlayerParty.containsKey(playername)) {
            int PartyID = Party.PlayerParty.get(playername);
            if (Party.PartyLeader.get(PartyID).equals(playername)) {
                // 判断是否够人数
                if (PartyEvent.GetAllMember(PartyID, true).size() > event.getStatus().getMax() - event.getStatus().getPlayerSize()) {
                    System.out.println("因为人数不足以进入而被禁止");
                    System.out.println(PartyEvent.GetAllMember(PartyID, true).size());
                    System.out.println(event.getStatus().getMax());
                    System.out.println(event.getStatus().getPlayerSize());
                    System.out.println(event.getStatus().getMax() - event.getStatus().getPlayerSize());
                    event.setCancelled(true);
                    BungeeChannelManager.sendPluginMessage(event.getPlayer(), "PartyTooLarge");
                    return;
                }
                PlayerNowGame.put(playername, event.getStatus().getMinigame());
                PlayerNowId.put(playername, event.getStatus().getID());
                for (String membername : PartyEvent.GetAllMember(PartyID, false)) {
                    Player member = Bukkit.getPlayer(membername);
                    if (member != null) {
                        if (PlayerNowGame.containsKey(membername)) {
                            // 如果不同, 离开游戏并继续
                            if (!event.getStatus().getMinigame().equals(PlayerNowGame.get(membername)))
                                if (ArcadeAPI.isPlaying(member)) ArcadeAPI.leaveGame(member);
                        }
                        // 如果游戏相同，继续判断
                        if (PlayerNowId.containsKey(membername)) {
                            if (ArcadeAPI.isPlaying(member)) {
                                // 如果 ID 相同, 取消加入
                                if (event.getStatus().getID().equals(PlayerNowId.get(membername))) return;
                            }

                            // 否则退出游戏
                            ArcadeAPI.leaveGame(member);
                        }
                        ArcadeAPI.joinGame(member, event.getStatus().getMinigame(), event.getStatus().getID());
                    } else {
                        BungeeChannelManager.sendPluginMessage(event.getPlayer(), "SendServer", membername);
                    }
                    MemberReadyGame.put(membername, event.getStatus().getMinigame());
                    MemberReadyId.put(membername, event.getStatus().getID());
                }
            } else {
                // 检测是否有权
                if (MemberReadyGame.containsKey(playername) && MemberReadyId.containsKey(playername)) {
                    if (MemberReadyGame.get(playername).equals(event.getStatus().getMinigame()) || MemberReadyId.get(playername).equals(event.getStatus().getID())) {
                        PlayerNowGame.put(playername, event.getStatus().getMinigame());
                        PlayerNowId.put(playername, event.getStatus().getID());
                        return;
                    }
                    event.setCancelled(true);
                    BungeeChannelManager.sendPluginMessage(event.getPlayer(), "OnlyLeaderJoin");
                }
            }
        } else {
            PlayerNowGame.put(playername, event.getStatus().getMinigame());
            PlayerNowId.put(playername, event.getStatus().getID());
        }
    }

    @EventHandler
    private void onGameEnd(ArcadeStopEvent event) {
        System.out.println("触发 ArcadeStopEvent");
        for (String playername : PlayerNowGame.keySet()) {
            if (PlayerNowGame.get(playername).equals(event.getStatus().getMinigame())) {
                if (PlayerNowId.get(playername).equals(event.getStatus().getID())) {
                    MemberReadyGame.remove(playername);
                    MemberReadyId.remove(playername);
                    PlayerNowGame.remove(playername);
                    PlayerNowId.remove(playername);
                }
            }
        }
    }

    @EventHandler
    private void onGameExit(ArcadeLeaveEvent event) {
        System.out.println("触发 ArcadeLeaveEvent");
        String playername = event.getPlayer().getName();
        PlayerNowGame.remove(playername);
        PlayerNowId.remove(playername);
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        if (MemberReadyGame.containsKey(event.getPlayer().getName()) && MemberReadyId.containsKey(event.getPlayer().getName())) {
            System.out.println("已提交加入");
            ArcadeAPI.joinGame(event.getPlayer(), MemberReadyGame.get(event.getPlayer().getName()), MemberReadyId.get(event.getPlayer().getName()));
        }
    }
    /*
    public static String getGame(String Minigame) {
        switch (Minigame) {
            case "TNT Run":
                return "tntrun";
            case "Block Party":
                return "blockparty";
            case "TNT Tag":
                return "tnttag";
            case "Prop Hunt":
                return "prophunt";
            case "Temple Run":
                return "templerun";
            case "Party Games":
                return "partygames";
            case "Bomb Lobbers":
                return "bomblobbers";
            case "Splegg":
                return "splegg";
            case "Mini Walls":
                return "miniwalls";
            default:
                return null;
        }
    }
    */
}
