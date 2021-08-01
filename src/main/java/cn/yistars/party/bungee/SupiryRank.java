package cn.yistars.party.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SupiryRank {
	// 只获取带颜色的名字
	public static String RankName(ProxiedPlayer player) {
		if (player.hasPermission("rank.admin")) {
			return "§c管理员";
		} else if (player.hasPermission("rank.helper")) {
			return "§b玩家支持";
		} else if (player.hasPermission("rank.mod")) {
			return "§a志愿者";
		} else if (player.hasPermission("rank.mvp++")) {
			return "§6MVP++";
		} else if (player.hasPermission("rank.mvp+")) {
			return "§bMVP§c+";
		} else if (player.hasPermission("rank.mvp")) {
			return "§bMVP";
		} else if (player.hasPermission("rank.vip+")) {
			return "§aVIP§6+";
		} else if (player.hasPermission("rank.vip")) {
			return "§aVIP";
		} else if (player.hasPermission("rank.mojang")) {
			return "§a正版玩家";
		} else {
			return "§7普通玩家";
		}
	}
	
	// 用于聊天格式的带 []
	public static String RankFormat(ProxiedPlayer player) {
		if (player.hasPermission("rank.admin")) {
			return "§c[Admin] ";
		} else if (player.hasPermission("rank.helper")) {
			return "§b[Helper] ";
		} else if (player.hasPermission("rank.mod")) {
			return "§a[Mod] ";
		} else if (player.hasPermission("rank.mvp++")) {
			return "§6[MVP++] ";
		} else if (player.hasPermission("rank.mvp+")) {
			return "§b[MVP§c+§b] ";
		} else if (player.hasPermission("rank.mvp")) {
			return "§b[MVP] ";
		} else if (player.hasPermission("rank.vip+")) {
			return "§a[VIP§6+§a] ";
		} else if (player.hasPermission("rank.vip")) {
			return "§a[VIP] ";
		} else if (player.hasPermission("rank.mojang")) {
			return "§a[Mojang] ";
		} else {
			return "§7";
		}
	}
	
	public static String RankChatColor(ProxiedPlayer player) {
		if (player.hasPermission("rank.admin")) {
			return "§r";
		} else if (player.hasPermission("rank.helper")) {
			return "§r";
		} else if (player.hasPermission("rank.mod")) {
			return "§r";
		} else if (player.hasPermission("rank.mvp++")) {
			return "§r";
		} else if (player.hasPermission("rank.mvp+")) {
			return "§r";
		} else if (player.hasPermission("rank.mvp")) {
			return "§r";
		} else if (player.hasPermission("rank.vip+")) {
			return "§r";
		} else if (player.hasPermission("rank.vip")) {
			return "§r";
		} else if (player.hasPermission("rank.mojang")) {
			return "§r";
		} else {
			return "§7";
		}
	}
}
