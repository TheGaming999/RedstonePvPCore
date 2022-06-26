package me.redstonepvpcore.messages;

public class MessagesHolder {

	private String[] messages;

	public MessagesHolder() {
		this.messages = new String[] { "none", "none" };
	}

	/**
	 * <b>DEFAULT:</b> 0 = setGadgetMessage, 1 = removeGadgetMessage
	 * 
	 * @param id id of message to get
	 * @return message stored on that id
	 */
	public String getMessage(int id) {
		return messages[id];
	}

	public void setMessage(int id, String message) {
		messages[id] = message;
	}

}
