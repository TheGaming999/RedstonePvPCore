package me.redstonepvpcore.mothers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import me.redstonepvpcore.utils.Colorizer;

public class Actions {

	@Nullable List<String> broadcastMessages;
	@Nullable List<String> commands;
	@Nullable List<String> messages;
	final CommandSender console = Bukkit.getConsoleSender();
	private Set<ActionExecutor> actionExecutors;
	private boolean executors;
	
	public Actions() {}

	public Actions(@Nullable List<String> broadcastMessages, @Nullable List<String> commands,
			@Nullable List<String> messages, boolean build) {
		this.broadcastMessages = broadcastMessages;
		this.commands = commands;
		this.messages = messages;
		if(!build) return;
		actionExecutors = new HashSet<>();
		executors = false;
		if(this.commands != null) {
			this.commands = Colorizer.colorize(this.commands);
			actionExecutors.add(new CommandExecutor());
			executors = true;
		}
		if(this.broadcastMessages != null) {
			this.broadcastMessages = Colorizer.colorize(this.broadcastMessages);
			actionExecutors.add(new BroadcastExecutor());
			executors = true;
		}
		if(this.messages != null) {
			this.messages = Colorizer.colorize(this.messages);
			actionExecutors.add(new MessageExecutor());
			executors = true;
		}
	}
	
	public boolean hasExecutors() {
		return executors;
	}
	
	@Nullable
	public List<String> getBroadcastMessages() {
		return broadcastMessages;
	}

	public void setBroadcastMessages(List<String> broadcastMessages) {
		this.broadcastMessages = broadcastMessages;
	}

	@Nullable
	public List<String> getCommands() {
		return commands;
	}

	public void setCommands(List<String> commands) {
		this.commands = commands;
	}

	@Nullable
	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}
	
	public void buildExecutors() {
		executors = false;
		actionExecutors = new HashSet<>();
		if(commands != null) {
			actionExecutors.add(new CommandExecutor());
			executors = true;
		}
		if(broadcastMessages != null) {
			actionExecutors.add(new BroadcastExecutor());
			executors = true;
		}
		if(messages != null) {
			actionExecutors.add(new MessageExecutor());
			executors = true;
		}
	}
	
	public void execute(CommandSender target) {
		if(executors) actionExecutors.forEach(executor -> executor.execute(target));
	}
	
	private interface ActionExecutor {
		
		void execute(CommandSender target);
		
	}
	
	private class CommandExecutor implements ActionExecutor {

		@Override
		public void execute(CommandSender target) {
			String targetName = target.getName();
			commands.forEach(command -> Bukkit.dispatchCommand(console, command.replace("%player%", targetName)));
		}
		
	}
	
	private class BroadcastExecutor implements ActionExecutor {

		@Override
		public void execute(CommandSender target) {
			String targetName = target.getName();
			broadcastMessages.forEach(message -> Bukkit.broadcastMessage(message.replace("%player%", targetName)));
		}
		
	}
	
	private class MessageExecutor implements ActionExecutor {

		@Override
		public void execute(CommandSender target) {
			String targetName = target.getName();
			messages.forEach(message -> target.sendMessage(message.replace("%player%", targetName)));
		}
		
	}
	
}
