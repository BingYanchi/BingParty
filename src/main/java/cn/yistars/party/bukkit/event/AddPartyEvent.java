package cn.yistars.party.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AddPartyEvent extends Event {
	private static final HandlerList HANDLERS = new HandlerList();
	
	private final Integer PartyID;
	private final String LeaderName;
	private final Player Leader;
	//private final String[] Members;
	
	public AddPartyEvent(Integer PartyID, Player Leader) {
		this.PartyID = PartyID;
		this.LeaderName = Leader.getName();
		this.Leader = Leader;
		//this.Members = Members;
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
    
    public String getLeaderName() {
    	return this.LeaderName;
    }

    public Player getLeader() {
    	return this.Leader;
    }
    
}
