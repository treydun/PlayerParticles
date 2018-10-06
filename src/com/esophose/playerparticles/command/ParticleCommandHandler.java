package com.esophose.playerparticles.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import com.esophose.playerparticles.manager.DataManager;
import com.esophose.playerparticles.manager.LangManager;
import com.esophose.playerparticles.manager.LangManager.Lang;
import com.esophose.playerparticles.manager.PermissionManager;

public class ParticleCommandHandler implements CommandExecutor, TabCompleter {
    
    /**
     * A list of all commands
     */
    private static List<CommandModule> commands;
    
    static {
        commands = new ArrayList<CommandModule>();
        
        commands.add(new AddCommandModule());
        commands.add(new DataCommandModule());
        commands.add(new DefaultCommandModule());
        commands.add(new EditCommandModule());
        commands.add(new EffectCommandModule());
        commands.add(new EffectsCommandModule());
        commands.add(new FixedCommandModule());
        commands.add(new GroupCommandModule());
        commands.add(new GUICommandModule());
        commands.add(new HelpCommandModule());
        commands.add(new InfoCommandModule());
        commands.add(new ListCommandModule());
        commands.add(new RemoveCommandModule());
        commands.add(new ResetCommandModule());
        commands.add(new StyleCommandModule());
        commands.add(new StylesCommandModule());
        commands.add(new VersionCommandModule());
        commands.add(new WorldsCommandModule());
    }
    
    /**
     * Finds a matching CommandModule by its name
     * 
     * @param commandName The command name
     * @return The found CommandModule, otherwise null
     */
    public static CommandModule findMatchingCommand(String commandName) {
    	for (CommandModule commandModule : commands)
    		if (commandModule.getName().equalsIgnoreCase(commandName))
    			return commandModule;
    	return null;
    }
    
    /**
     * Get a list of all available commands
     * 
     * @return A List of all CommandModules registered
     */
    public static List<CommandModule> getCommands() {
        return commands;
    }
    
    /**
     * Get all available command names
     * 
     * @return All available command names
     */
    public static List<String> getCommandNames() {
        List<String> commandNames = new ArrayList<String>();
        for (CommandModule cmd : commands) 
            commandNames.add(cmd.getName());
        return commandNames;
    }

    /**
     * Called when a player executes a /pp command
     * Checks what /pp command it is and calls the corresponding module
     * 
     * @param sender Who executed the command
     * @param cmd The command
     * @param label The command label
     * @param args The arguments following the command
     * @return true
     */
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player p = (Player) sender;
        
        DataManager.getPPlayer(p.getUniqueId(), (pplayer) -> {
        	String commandName = args.length > 0 ? args[0] : "";
        	CommandModule commandModule = findMatchingCommand(commandName);
        	
        	if (commandModule != null) {
        		if (commandModule.requiresEffects() && PermissionManager.getEffectsUserHasPermissionFor(p).size() == 1) {
        			LangManager.sendMessage(p, Lang.COMMAND_ERROR_NO_EFFECTS);
        		} else {
        			String[] cmdArgs = new String[0];
                    if (args.length > 1) cmdArgs = Arrays.copyOfRange(args, 1, args.length);
            		commandModule.onCommandExecute(pplayer, cmdArgs);
        		}
        	} else {
        		LangManager.sendMessage(p, Lang.COMMAND_ERROR_UNKNOWN);
        	}
        });
        
        return true;
    }
    
//    private static final String[] COMMANDS = { "help", "gui", "effect", "effects", "style", "styles", "data", "fixed", "reset", "worlds", "version" };
//    private static final String[] FIXED_COMMANDS = { "create", "remove", "list", "info" };
//    private static final List<String> BLOCK_MATERIALS = ParticleUtils.getAllBlockMaterials();
//    private static final List<String> ITEM_MATERIALS = ParticleUtils.getAllItemMaterials();

    /**
     * Activated when a user pushes tab in chat prefixed with /pp
     * 
     * @param sender The sender that hit tab, should always be a player
     * @param cmd The command the player is executing
     * @param alias The possible alias for the command
     * @param args All arguments following the command
     * @return A list of commands available to the sender
     */
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if (!(sender instanceof Player)) return new ArrayList<String>();

        if (cmd.getName().equalsIgnoreCase("pp")) {
            System.out.println("Args in onTabComplete: " + Arrays.toString(args) + " " + args.length);
            if (args.length <= 1) {
                CommandModule commandModule = findMatchingCommand(""); // Get the default command module
                return commandModule.onTabComplete(DataManager.getPPlayer(((Player) sender).getUniqueId()), args);
            } else if (args.length >= 2) {
            	CommandModule commandModule = findMatchingCommand(args[0]);
            	String[] cmdArgs = Arrays.copyOfRange(args, 1, args.length);
            	if (commandModule != null) {
            		return commandModule.onTabComplete(DataManager.getPPlayer(((Player) sender).getUniqueId()), cmdArgs);
            	}
            	
            	// TODO: Move to correct CommandModules
//                if (args[0].equalsIgnoreCase("effect") && args.length == 2) {
//                    List<String> commands = PermissionManager.getEffectsUserHasPermissionFor((Player) sender);
//                    StringUtil.copyPartialMatches(args[1], commands, completions);
//                } else if (args[0].equalsIgnoreCase("style") && args.length == 2) {
//                    List<String> commands = PermissionManager.getStylesUserHasPermissionFor((Player) sender);
//                    StringUtil.copyPartialMatches(args[1], commands, completions);
//                } else if (args[0].equalsIgnoreCase("fixed") && args.length > 1) {
//                    if (args.length == 2) {
//                        List<String> commands = Arrays.asList(FIXED_COMMANDS);
//                        StringUtil.copyPartialMatches(args[1], commands, completions);
//                    } else if (args[1].equalsIgnoreCase("create")) {
//                        completions.add("<x> <y> <z> <effect> <style> [data]");
//                    } else if ((args[1].equalsIgnoreCase("remove") || args[1].equalsIgnoreCase("info")) && args.length == 3) {
//                        completions.add("<id>");
//                    } 
//                } else if (args[0].equalsIgnoreCase("data")) {
//                    PPlayer pplayer = DataManager.getPPlayer(((Player) sender).getUniqueId());
//                    if (pplayer == null) {
//                        completions.add(ChatColor.stripColor(Lang.NO_DATA_USAGE.get()));
//                    } else if (pplayer.getParticleEffect().hasProperty(ParticleProperty.REQUIRES_MATERIAL_DATA) && args.length == 2) {
//                        if (pplayer.getParticleEffect() == ParticleEffect.ITEM) {
//                            StringUtil.copyPartialMatches(args[1], ITEM_MATERIALS, completions);
//                        } else {
//                            StringUtil.copyPartialMatches(args[1], BLOCK_MATERIALS, completions);
//                        }
//                    } else if (pplayer.getParticleEffect().hasProperty(ParticleProperty.COLORABLE)) {
//                        if (pplayer.getParticleEffect() == ParticleEffect.NOTE && args.length == 2) {
//                            completions.add("<0-23>");
//                            StringUtil.copyPartialMatches(args[args.length - 1], Arrays.asList(new String[] { "rainbow" }), completions);
//                        } else if (pplayer.getParticleEffect() != ParticleEffect.NOTE && args.length > 1 && args.length < 5) {
//                            completions.add("<0-255>");
//                            if (args.length == 2) {
//                                StringUtil.copyPartialMatches(args[args.length - 1], Arrays.asList(new String[] { "rainbow" }), completions);
//                            }
//                        }
//                    } else if (args.length == 2) {
//                        completions.add(ChatColor.stripColor(Lang.NO_DATA_USAGE.get()));
//                    }
//                }
//            } else {
//                List<String> commands = new ArrayList<String>(Arrays.asList(COMMANDS));
//                StringUtil.copyPartialMatches(args[0], commands, completions);
              }
        }
        return null;
    }

//    public static String[] getCommandsList() {
//        return COMMANDS;
//    }

}
