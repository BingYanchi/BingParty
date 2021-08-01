package cn.yistars.party.bukkit.gui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import cn.yistars.party.bukkit.Party;
import cn.yistars.party.bukkit.PartyEvent;

public class MemberSort {
	public static String[] SortType = {"Default", "Az", "LastTime"};
	public static HashMap<String, Integer> PlayerSort = new HashMap<>();
	// true 为正常模式, false 为倒序模式
	public static HashMap<String, Boolean> PlayerOrder = new HashMap<>();
	public static HashMap<String, ArrayList<String>> MemberSort = new HashMap<>();
	public static HashMap<String, String> PlayerSearch = new HashMap<>();
	
	public static void ChangeSort (Player player) {
		if (PlayerSearch.containsKey(player.getName())) return;

		int Sort = PlayerSort.get(player.getName()) + 1;
		if (Sort >= SortType.length) {
			Sort = 0;
		}
		PlayerSort.put(player.getName(), Sort);
		UpdateMember(player);
	}
	
	public static void ChangeOrder (Player player) {
		if (PlayerSearch.containsKey(player.getName())) return;

		Boolean Order = PlayerOrder.get(player.getName());
		if (Order) {
			PlayerOrder.put(player.getName(), false);
		} else {
			PlayerOrder.put(player.getName(), true);
		}
		UpdateMember(player);
	}

	public static void ChangeSearch (Player player, String... word) {
		Boolean Search = PlayerSearch.containsKey(player.getName());
		if (Search) {
			PlayerSearch.remove(player.getName());
			UpdateMember(player);
		} else {
			PlayerSearch.put(player.getName(), word[0]);
			UpdateMember(player, word[0]);
		}
	}
	
	public static void UpdateAllMember() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			UpdateMember(player);
		}
	}
	
	public static void UpdateMember (Player player, String... word) {
		if (!Party.PlayerParty.containsKey(player.getName())) return;
		Integer PartyID = Party.PlayerParty.get(player.getName());
		ArrayList<String> Member = PartyEvent.GetAllMember(PartyID, true);

		if (PlayerSearch.containsKey(player.getName())) {
			ArrayList<String> NewMember = (ArrayList<String>) Member.clone();
			for (String Membername : Member) {
				if (!Membername.startsWith(word[0])){
					NewMember.remove(Membername);
				}
			}
			MemberSort.put(player.getName(), NewMember);
			return;
		}

		switch (SortType[PlayerSort.get(player.getName())]) {
			case "Default":
				break;
			case "Az":
				Member.sort(Comparator.naturalOrder());
				break;
			case "LastTime":
				HashMap<Long, String> Offline = new HashMap<>();
				for (String member : Member) {
					if (Party.PlayerLastTime.containsKey(member)) {
						Offline.put(Party.PlayerLastTime.get(member), member);
					}
				}
				ArrayList<Long> Time = new ArrayList<>();
				for (Long time : Offline.keySet()) {
					Member.remove(Offline.get(time));
					Time.add(time);
				}
				Time.sort(Comparator.naturalOrder());
				Time.sort(Comparator.reverseOrder());
				for (Long atime : Time) {
					Member.add(Offline.get(atime));
				}
				break;
		}
		
		if (!PlayerOrder.get(player.getName())) {
			Member.sort(Comparator.reverseOrder());
		}
		MemberSort.put(player.getName(), Member);
	}
}
