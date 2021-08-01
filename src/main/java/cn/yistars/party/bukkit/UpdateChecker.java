package cn.yistars.party.bukkit;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.inventivetalent.update.spiget.bukkit.SpigetUpdate;
import org.inventivetalent.update.spiget.bukkit.UpdateCallback;
import org.inventivetalent.update.spiget.bukkit.comparator.VersionComparator;

public class UpdateChecker implements Listener {
	
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
    	if (Party.CheckUpdate && event.getPlayer().hasPermission("partysystem.admin")) {
    		System.out.println(Party.Messages);
    		Checker(event.getPlayer(), false);
    	}
    }
	
	public static void Checker(Player player, boolean hasCommand) {
		SpigetUpdate updater = new SpigetUpdate(Party.instance, 94456);

		updater.setVersionComparator(VersionComparator.EQUAL);

		updater.checkForUpdate(new UpdateCallback() {
			@Override
			public void updateAvailable(String newVersion, String downloadUrl, boolean hasDirectDownload) {
				String msg = Party.Messages.get("Update");
				msg = msg.replace("%old_version%", Party.instance.getDescription().getVersion());
				msg = msg.replace("%new_version%", newVersion);
				msg = ChatColor.translateAlternateColorCodes('&', msg);
				player.spigot().sendMessage(new TextComponent(msg));
				
				msg = Party.Messages.get("UpdatePage");
				msg = msg.replace("%url%", "https://www.spigotmc.org/resources/partysystem.94456/");
				msg = ChatColor.translateAlternateColorCodes('&', msg);
				TextComponent message = new TextComponent(msg);
				message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/partysystem.94456/"));
				player.spigot().sendMessage(new TextComponent(message));
				
				if (hasDirectDownload) {
					if (updater.downloadUpdate()) {
						msg = Party.Messages.get("AutoDownload");
						msg = ChatColor.translateAlternateColorCodes('&', msg);
						message = new TextComponent(msg);
					} else {
						msg = Party.Messages.get("AutoDownloadFail");
						msg = ChatColor.translateAlternateColorCodes('&', msg);
						message = new TextComponent(msg);
						player.spigot().sendMessage(new TextComponent(message));
						
						msg = Party.Messages.get("UpdateDownload");
						msg = msg.replace("%url%", downloadUrl);
						msg = ChatColor.translateAlternateColorCodes('&', msg);
						message = new TextComponent(msg);
						message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, downloadUrl));
					}
					player.spigot().sendMessage(new TextComponent(message));
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
					player.spigot().sendMessage(new TextComponent(message));
				}
			}
		});
	}
}
