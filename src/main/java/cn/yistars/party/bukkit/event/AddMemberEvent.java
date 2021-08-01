package cn.yistars.party.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AddMemberEvent extends Event {
	private static final HandlerList HANDLERS = new HandlerList();
	
	private final Integer PartyID;
	private final String PlayerName;
	private final String LeaderName;
	private final Player Player;
	private final Player Leader;
	
	public AddMemberEvent(Integer PartyID, Player Player, Player Leader) {
		this.PartyID = PartyID;
		this.PlayerName = Player.getName();
		this.LeaderName = Leader.getName();
		this.Player = Player;
		this.Leader = Leader;
	}

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
    
    public Integer getPartyID() {
    	return this.PartyID;
    }
    
    public String getPlayerName() {
    	return this.PlayerName;
    }
    
    public String getLeaderName() {
    	return this.LeaderName;
    }
    
    public Player getPlayer() {
    	return Player;
    }
    
    public Player getLeader() {
    	return Leader;
    }
    
}
