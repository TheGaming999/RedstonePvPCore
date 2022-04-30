package me.redstonepvpcore.gadgets;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class ExpSign extends Gadget {

	public ExpSign(Location location) {
		super(GadgetType.EXP_SIGN, location);
		getMessagesHolder().setMessage(0, getParent().getMessages().getSetExpSign());
		getMessagesHolder().setMessage(1, getParent().getMessages().getRemoveExpSign());
	}

	@Override
	public boolean perform(Player player) {
		return false;
	}


}
