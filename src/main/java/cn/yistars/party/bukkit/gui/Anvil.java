package cn.yistars.party.bukkit.gui;

import cn.yistars.party.bukkit.BungeeChannelManager;
import cn.yistars.party.bukkit.Party;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Anvil {
    public static void CreateAnvil(Player player, String command, String title, String text) {
        new AnvilGUI.Builder()
                .onComplete((p, playerText) -> {
                    switch (command) {
                        case "invite": case "kick":
                            String Command = "party " + command + " " + playerText;
                            BungeeChannelManager.sendPluginMessage(p, "PlayerCommand", Command);
                            return AnvilGUI.Response.close();
                        case "search":
                            p.chat("/party search " + playerText);
                            return AnvilGUI.Response.close();
                        default:
                            return AnvilGUI.Response.close();
                    }
                })
                .text(text)
                .itemLeft(new ItemStack(Material.PAPER))
                .title(title)
                .plugin(Party.instance)
                .open(player);
    }
}
