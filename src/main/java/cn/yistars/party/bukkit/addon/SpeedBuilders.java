package cn.yistars.party.bukkit.addon;

import cn.yistars.party.bukkit.Party;
import cn.yistars.party.bukkit.PartyEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;

public class SpeedBuilders implements Listener {
    private final ArrayList<String> ReadyGame = new ArrayList<>();
    @EventHandler
    private void onGameJoin(AsyncPlayerChatEvent event) {
        String playername = event.getPlayer().getName();
        if (event.getMessage().contains("/sb ")) {
            if (event.getMessage().split(" ").length >= 2) {
                if (Party.PlayerParty.containsKey(playername)) {
                    int PartyID = Party.PlayerParty.get(playername);
                    if (Party.PartyLeader.get(PartyID).equals(playername)) {
                        switch (event.getMessage().split(" ")[1]) {
                            case "join": case "autojoin":
                                // 触发
                                for (String membername : PartyEvent.GetAllMember(PartyID, false)) {
                                    Player member = Bukkit.getPlayer(membername);
                                    if (member != null) {
                                        member.chat("/sb autojoin");
                                    } else {

                                    }
                                }
                                break;
                        }
                    } else {

                    }

                }

            }
        }
    }
}
