package cn.yistars.party.bukkit;

import java.util.ArrayList;

public class PartyEvent {
	// 获取所有成员
	public static ArrayList<String> GetAllMember(Integer PartyID, Boolean leader) {
		ArrayList<String> members = new ArrayList<>();
		// 如果包括队长
	   	if (leader) {
	   		members.add(Party.PartyLeader.get(PartyID));
	   	}
	 // 组队管理添加
	   	if (Party.PartyMod.get(PartyID).size() != 0) {
			members.addAll(Party.PartyMod.get(PartyID));
	   	}
		// 组队成员添加
	   	if (Party.PartyMember.get(PartyID).size() != 0) {
			members.addAll(Party.PartyMember.get(PartyID));
	   	}
	   	return members;
	}
}
