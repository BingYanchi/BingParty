package cn.yistars.party.bukkit;

import org.bukkit.entity.Player;

import cn.yistars.party.bukkit.gui.AlonsoLevels;
import cn.yistars.party.bukkit.gui.MemberSort;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PAPIHook extends PlaceholderExpansion {

    private Party plugin = Party.instance;

    public void Party(Party plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean persist(){
        return true;
    }

    @Override
    public boolean canRegister(){
        return true;
    }

    @Override
    public String getAuthor(){
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest 
     * method to obtain a value if a placeholder starts with our 
     * identifier.
     * <br>The identifier has to be lowercase and can't contain _ or %
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier(){
        return "partysystem";
    }

    @Override
    public String getVersion(){
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier){

        if(player == null){
            return null;
        }
        
        String[] type = identifier.split(",");
        switch (type[0]) {
        	// 获取语言文件
        	case "lang":
        		if (type.length < 2) return null;
        		switch (type[1]) {
        			case "SortCurrent":
        				switch (MemberSort.PlayerSort.get(player.getName())) {
        					case 0:
        						return Party.Gui.get("SortCurrent").replace("%sort%", Party.Gui.get("SortDefault"));
        					case 1:
        						return Party.Gui.get("SortCurrent").replace("%sort%", Party.Gui.get("SortAz"));
        					case 2:
        						return Party.Gui.get("SortCurrent").replace("%sort%", Party.Gui.get("SortLastTime"));
        					default:
        						return "Error";	
        				}
        			case "SortOrder":
        				return Party.Gui.get("SortOrder").replace("%order%", MemberSort.PlayerOrder.get(player.getName()) ? Party.Gui.get("OrderDefault") : Party.Gui.get("OrderReverse"));
					case "SerrchNotFoundInfo":
						if (MemberSort.PlayerSort.containsKey(player.getName())) {
							return Party.Gui.get("SerrchNotFoundInfo").replace("%word%", MemberSort.PlayerSearch.get(player.getName()));
						} else {
							return "Error";
						}
					case "KickPlayerSubTitle":
						if (type.length != 3) return null;
						return Party.Gui.get("KickPlayerSubTitle").replace("%player%", type[2]);
					case "PromotePlayerSubTitle":
						if (type.length != 3) return null;
						return Party.Gui.get("PromotePlayerSubTitle").replace("%player%", type[2]);
					default:
        				return Party.Gui.get(type[1]);
        		}
        	// 判断组队人数
        	case "size":
        		if (type.length == 1) {
        			if (Party.PlayerParty.containsKey(player.getName())) {
            			int PartySize = MemberSort.MemberSort.get(player.getName()).size();
            			if (MemberSort.PlayerSearch.containsKey(player.getName()) && PartySize == 0) {
							PartySize = -1;
						}
            			return String.valueOf(PartySize);
            		}
            		return "0";
        		}
        		return null;
        	// 获取指定位置的成员信息
        	case "member":
        		if (!(type.length == 3)) return null;
        		if (!Party.PlayerParty.containsKey(player.getName())) return null;
        		String MemberName;
        		try {
					int Num = Integer.parseInt(type[1]);
					if (!MemberSort.MemberSort.containsKey(player.getName())) {
						return null;
					}
					MemberName = MemberSort.MemberSort.get(player.getName()).get(Num);
				} catch (NumberFormatException e) {
					if (!MemberSort.MemberSort.get(player.getName()).contains(type[1])) {
						return null;
					}
					MemberName = type[1];
				}
        		if (MemberName == null) return null;

        		// 判别类型
        		switch (type[2]) {
        			case "MemberLevel":
        				if (Party.AlonsoLevelsHook) {
        					return Party.Gui.get("MemberLevel") + AlonsoLevels.getPlayerLevel(Party.PlayerUUID.get(MemberName));
        				} else {
        					return Party.Gui.get("MemberLevel") + "0";
        				}
        			case "MemberPoint":
        				return Party.Gui.get("MemberPoint") + "0";
        			case "MemberGuild":
        				return Party.Gui.get("MemberGuild") + Party.Gui.get("MemberNull");
        			case "Rank":
        				return Party.Rank.get(MemberName);
        			case "Name":
        				return MemberName;
        			case "MemberStats":
        				return Party.Gui.get("MemberStat").replace("%stat%", Party.OnlineStat.get(MemberName) ? Party.Gui.get("MemberOnline") : Party.Gui.get("MemberOffline"));
        			case "MemberSever":
        				if (!Party.OnlineStat.get(MemberName)) {
        					return "";
        				} else {
        					return Party.Gui.get("MemberSever").replace("%server%", Party.PlayerServer.get(MemberName));
        				}
        		}
        		return null;
        	// 获取身份
        	case "role":
        		if (Party.PlayerRole.containsKey(player.getName())) {
        			return Party.PlayerRole.get(player.getName());
        		}
        		return null;
        	// 是否处于搜索模式
			case "search":
				if (MemberSort.PlayerSearch.containsKey(player.getName())) {
					return "true";
				} else {
					return "false";
				}
			// 获取是否在组队
			case "party":
				if (Party.PlayerParty.containsKey(player.getName())) {
					return "true";
				} else {
					return "false";
				}
        }

        // We return null if an invalid placeholder (f.e. %someplugin_placeholder3%) 
        // was provided
        return null;
    }
}
