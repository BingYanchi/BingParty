package cn.yistars.party.bukkit.addon;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import cn.yistars.party.bukkit.event.AddMemberEvent;
import cn.yistars.party.bukkit.event.AddPartyEvent;
import cn.yistars.party.bukkit.event.RemoveMemberEvent;
import cn.yistars.party.bukkit.event.RemovePartyEvent;

import com.andrei1058.bedwars.api.BedWars;

import java.util.Objects;

public class BedWars1058 implements Listener {
	BedWars bedwarsAPI = Objects.requireNonNull(Bukkit.getServicesManager().getRegistration(BedWars.class)).getProvider();
	
	@EventHandler
	private void onAddParty(AddPartyEvent event) {
		//System.out.println("检测到 AddPartyEvent");
		Player Leader = event.getLeader();
		if (Leader != null) {
			bedwarsAPI.getPartyUtil().createParty(Leader);
		}
	}
	
	@EventHandler
	private void onRemoveParty(RemovePartyEvent event) {
		Player Leader = Bukkit.getPlayer(event.getLeaderName());
		if (Leader != null) {
			bedwarsAPI.getPartyUtil().disband(Leader);
		}
	}
	
	@EventHandler
	private void onAddMember(AddMemberEvent event) {
		//System.out.println("检测到 AddMemberEvent");
		Player Member = Bukkit.getPlayer(event.getPlayerName());
		Player Leader = Bukkit.getPlayer(event.getLeaderName());
		if (Member != null) {
			bedwarsAPI.getPartyUtil().addMember(Leader, Member);
		}
	}
	
	@EventHandler
	private void onRemoveMember(RemoveMemberEvent event) {
		Player Member = Bukkit.getPlayer(event.getPlayerName());
		if (Member != null) {
			bedwarsAPI.getPartyUtil().removeFromParty(Member);
		}
	}
}
