package cn.yistars.party.bungee;

import org.inventivetalent.update.spiget.bungee.SpigetUpdate;
import org.inventivetalent.update.spiget.bungee.UpdateCallback;
import org.inventivetalent.update.spiget.bungee.comparator.VersionComparator;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class UpdateChecker implements Listener {
	
    @EventHandler
    public void onPlayerJoin(PostLoginEvent event) {
    	if (Party.CheckUpdate && event.getPlayer().hasPermission("partysystem.admin")) {
    		Checker(event.getPlayer(), false);
    	}
    }
	
	public static void Checker(ProxiedPlayer player, boolean hasCommand) {
		SpigetUpdate updater = new SpigetUpdate(Party.instance, 94456);

		updater.setVersionComparator(VersionComparator.EQUAL);

		updater.checkForUpdate(new UpdateCallback() {
			@Override
			public void updateAvailable(String newVersion, String downloadUrl, boolean hasDirectDownload) {
				String msg = Party.Messages.get("Update");
				msg = msg.replace("%old_version%", Party.instance.getDescription().getVersion());
				msg = msg.replace("%new_version%", newVersion);
				msg = ChatColor.translateAlternateColorCodes('&', msg);
				player.sendMessage(new TextComponent(msg));
				
				msg = Party.Messages.get("UpdatePage");
				msg = msg.replace("%url%", "https://www.spigotmc.org/resources/partysystem.94456/");
				msg = ChatColor.translateAlternateColorCodes('&', msg);
				TextComponent message = new TextComponent(msg);
				message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/partysystem.94456/"));
				player.sendMessage(new TextComponent(message));
				
				if (hasDirectDownload) {
					msg = Party.Messages.get("UpdateDownload");
					msg = msg.replace("%url%", downloadUrl);
					msg = ChatColor.translateAlternateColorCodes('&', msg);
					message = new TextComponent(msg);
					message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, downloadUrl));
					player.sendMessage(new TextComponent(message));
				}
				//// A new version is available
				// newVersion - the latest version
				// downloadUrl - URL to the download
				// hasDirectDownload - whether the update is available for a direct download on spiget.org
			}

			@Override
			public void upToDate() {
				if (hasCommand) {
					String msg = Party.Messages.get("NoUpdate");
					msg = ChatColor.translateAlternateColorCodes('&', msg);
					TextComponent message = new TextComponent(msg);
					player.sendMessage(new TextComponent(message));
				}
			}
		});
	}
}
