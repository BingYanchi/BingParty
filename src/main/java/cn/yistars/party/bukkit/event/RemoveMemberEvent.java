package cn.yistars.party.bukkit.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RemoveMemberEvent extends Event {
	private static final HandlerList HANDLERS = new HandlerList();
	
	private final Integer PartyID;
	private final String PlayerName;
	private final Player Player;
	
	public RemoveMemberEvent(Integer PartyID, Player Player) {
		this.PartyID = PartyID;
		this.PlayerName = Player.getName();
		this.Player = Player;
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
    
    public Player getPlayer() {
    	return Player;
    }
    
}
