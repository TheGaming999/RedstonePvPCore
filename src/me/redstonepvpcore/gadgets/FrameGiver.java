package me.redstonepvpcore.gadgets;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class FrameGiver extends Gadget {

	public FrameGiver(Location location) {
		super(GadgetType.FRAME_GIVER, location);
		getMessagesHolder().setMessage(0, getParent().getMessages().getSetFrameGiver());
		getMessagesHolder().setMessage(1, getParent().getMessages().getRemoveFrameGiver());
	}

	@Override
	public boolean perform(Player player) {
		return false;
	}


}
