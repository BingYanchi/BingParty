package cn.yistars.party.bukkit;

import cn.yistars.party.bukkit.addon.Arcade;
import cn.yistars.party.bukkit.addon.TowerDefense;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import cn.yistars.party.bukkit.event.RemovePartyEvent;
import cn.yistars.party.bukkit.gui.MemberSort;

public class PlayerEvent implements Listener {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		MemberSort.PlayerSort.put(event.getPlayer().getName(), 0);
		MemberSort.PlayerOrder.put(event.getPlayer().getName(), true);
	}
	
	@EventHandler
	public void onPlayerQuit (PlayerQuitEvent event) {
		String playername = event.getPlayer().getName();
		MemberSort.PlayerSort.remove(playername);
		MemberSort.PlayerOrder.remove(event.getPlayer().getName());
		MemberSort.MemberSort.remove(event.getPlayer().getName());
		if (Party.Leaders.containsKey(playername)) {
			RemovePartyEvent removepartyEvent = new RemovePartyEvent(Party.Leaders.get(playername), event.getPlayer());
			Bukkit.getPluginManager().callEvent(removepartyEvent);
		}
	}
}
