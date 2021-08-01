package cn.yistars.party.bukkit.gui;

import java.util.UUID;

import com.alonsoaliaga.alonsolevels.api.AlonsoLevelsAPI;

public class AlonsoLevels {
	
	public static int getPlayerLevel (String uuid) {
		return AlonsoLevelsAPI.getLevel(UUID.fromString(uuid));
	}
}
