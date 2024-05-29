package de.legoshi.td2core.util;

public enum Message {

    PREFIX("§7§l[§3§lTD2§7§l]§f"),
    SUCCESS_TAG("§a"),
    WARNING_TAG("§c"),
    
    PLAYER_JOIN("§7§a {1} §7joined the game."),
    PLAYER_LEAVE("§7§c {1} §7left the game."),
    
    PLAYER_MAP_JOIN("§7 You joined the map §6{1}§7."),
    PLAYER_MAP_JOIN_STAFF("§7 You joined the map §6{1}§7 as Staff."),
    PLAYER_MAP_LEAVE("§7 You left the map §6{1}§7."),
    PLAYER_MAP_LEAVE_STAFF("§7 You left the map §6{1}§7 as Staff."),
    PLAYER_NOT_IN_MAP("You are not in a map."),
    
    CHECKPOINT_REACHED("§7 Checkpoint activated!"),
    GOAL_REACHED("§7 §6{1} §7has beaten §6{2}§7!"),
    SET_CP("§7 Set the checkpoint."),
    NIGHT_VISION_ON("§7 Successfully turned on night vision."),
    NIGHT_VISION_OFF("§7 Successfully turned off night vision."),
    NOT_WORTHY("§4§lYOU §7§lare §4§lNOT WORTHY§7§l..."),
    VERIFY_START("§7You have 5 minutes to verify in discord. Type §6/verify {1} §7in §6#verify"),
    ALREADY_VERIFIED("You have already been verified."),
    VERIFY_ERROR("Your code is incorrect or your time ran out..."),
    VERIFY_SUCCESS("You have been verified!"),
    ALREADY_CREATED("You have already created a key. You have 5 minutes to verify in discord. Type §6/verify {1} §cin §6#verify"),
    
    ERROR_NO_PERMISSION("You do not have permission to use this command."),
    
    ERROR_NOT_A_PLAYER("You must be a player to use this command."),
    ERROR_IN_LOBBY("You must be in a course to do that!"),
    ERROR_PLAYER_ONLINE("The player must be offline to use this command."),
    ERROR_PLAYER_DELETE("Could not delete the player {1}."),
    SUCCESS_ADD_BLOCK_DATA(" Successfully added the blockdata to block."),
    BLOCK_DATA_ERROR_SYNTAX("Syntax error!"),
    PLAYER_VERSION_NOT_SUPPORTED("§cWARNING §7recommended versions: §6§l1.12-1.12.2§7.\nSome jumps might not be possible in other versions."),
    PLAYER_NOT_ON_GROUND("You must be on the ground to do that."),
    TELEPORT("Teleported!"),
    NOT_A_NUMBER("Please only enter numbers."),
    PLAYER_NOT_ONLINE("The player is not online."),
    TP_USAGE("§7Usage: §6/tp <player> <toPlayer> §7|| §6/tp <x> <y> <z> [yaw] [pitch]"),
    ERROR_ALREADY_VERIFIED("You have already been verified."),
    KIT_RESET("Successfully reset all kits."),
    KIT_RESET_HELP("Want to get the initial inventory back? Use §6/kit reset§a."),
    LB_HELP("Use §6/lb §cto see the leaderboard of your current map or type §6/lb global §cto the see entire leaderboard."),
    
    PLAYER_SWITCH_TO_PRACTICE("§7 Switched to practice mode."),
    PLAYER_SWITCH_TO_PARKOUR("§7 Switched to parkour mode."),
    PLAYER_SET_FLY("§7 Toggle fly: {1}"),
    NEXT_CP("§4Forced SP:§7 ({1}, {2}, {3})"),
    CHECKPOINT_ACTIVATED("**{1}** has reached SP **{2}** in **{3}**."),
    COMPLETION_ACTIVATED("**{1}** has BEATEN **{2}** in **{3}** with **{4}** fails."),
    LOG_ACTIVATED("[{1}] **{2}** has activated SP({3}, {4}, {5}) in **{6}** with **{7}** fails."),
    LEADERBOARD_ENTRY("{1} {2}   - {3} completion.\n"),
    NOT_STAFF_MODE("§7You can't join a course while in staff mode. Use /staff to deactivate."),
    SPC_USAGE(" §7Use: §6/spc <x> <y> <z> [yaw] [pitch] §7or §6/spc §7to set precise coordinates."),
    PRECISE_COORDS_USAGE(" §7Hit a §6checkpoint §7to connect this one with."),
    TIME_USAGE(" §7Type §6/duration <ticks> §7to set a maximum time for this checkpoint."),
    PLAYER_TOO_SLOW(" §7You were too slow :(!"),
    ERROR_NO_SPC("Select the setting inside a pressure plate first."),
    BLOCK_DATA_NEXT_CHECKPOINT("§4Next checkpoint:§7 ({1}, {2}, {3})"),
    PLAYER_STEP_PROGRESS(" §7I'm not a checkpoint. I'm here to track your progress!"),
    ACTIVATED_CP("Activated checkpoint."),
    DEACTIVATED_CP("Deactivated checkpoint."),
    ACTIVATE_INDEX_CP("§7Set the checkpoint number by typing §6/cp <number> §7or §6/cp -1 §7to deactivate."),
    DEACTIVATED_NO_SPRINT("No Sprint deactivated."),
    ACTIVATED_NO_SPRINT("No Sprint activated."),
    
    STAFF_MODE_OFF(" §7§lSTAFF MODE §c§lOFF."),
    STAFF_MODE_ON(" §7§lSTAFF MODE §a§lON."),
    SUCCESS_PLAYER_DELETE("Successfully deleted the player {1}."),
    SUCCESS_PARKOUR_KIT_GIVEN("Successfully given the parkour kit."),
    SUCCESS_PRAC_KIT_GIVEN("Successfully given the practice kit."),
    SUCCESS_RELOADED("Successfully reloaded the config."),
    SUCCESS_SPAWN("Returned to spawn..."),
    SUCCESS_RESET("Successfully reset."),
    SUCCESS_HIDE_ALL("Successfully hidden all players."),
    SUCCESS_SHOW_ALL("Successfully shown all players."),
    SUCCESS_HIDE_PLAYER("Successfully hidden §6{1}§a."),
    SUCCESS_SHOW_PLAYER("Successfully shown §6{1}§a."),
    ERROR_SHOW_PLAYER("Could not show {1}. Is the player online or /hideall active?"),
    ERROR_HIDE_PLAYER("Could not hide {1}. Is the player online?"),
    BLOCK_DATA_SUCCESS("Successfully added blockdata."),
    CANT_DESTROY_BLOCK_DATA("You can't destroy the blockdata."),
    SUCCESS_REMOVE_BLOCK_DATA("Successfully removed the blockdata from block."),
    DISCORD_MESSAGE(" §7Join the official §6TD2-Discord: §ahttps://discord.gg/WjqfN2qzzT §7to view and compare your progress on this server!"),
    ERROR_IN_TUTORIAL("Please finish the tutorial before doing this."),
    TUTORIAL_COMPLETED("You have finished the tutorial!"),
    
    HELP_MESSAGE("§7----- §6HELP §7------"),
    HELP_MESSAGE_1("§7/help - §eShow this message"),
    HELP_MESSAGE_2("§7/prac - §eEnable/Disable practice mode"),
    HELP_MESSAGE_3("§7/kit [reset] - §eGive current item configuration (or reset to standard configuration)"),
    HELP_MESSAGE_4("§7/leave - §eLeave a map"),
    HELP_MESSAGE_5("§7/delete - §eDelete player stats"),
    HELP_MESSAGE_6("§7/spawn - §eReturn to spawn"),
    HELP_MESSAGE_7("§7/td2reload - §eReload the config"),
    HELP_MESSAGE_8("§7/nv - §eToggle night vision"),
    HELP_MESSAGE_9("§7/reset - §eReset progress in the map you are in"),
    HELP_MESSAGE_10("§7/hideall | /showall - §eHides/shows all players"),
    HELP_MESSAGE_11("§7/hide <player> | /show <player> - §eHides/shows a specific player"),
    HELP_MESSAGE_12("§7/lb [global] - §eShow the leaderboard of your current map (or the global lb)"),
    HELP_MESSAGE_13("§7/staff - §eSwitch to/from staff mode"),
    HELP_MESSAGE_14("§7/verify - §eVerify yourself in the discord (/discord)"),
    HELP_MESSAGE_15("§7/discord - §eGet the discord link"),
    ;
    
    private final String message;
    
    Message(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String getMessage(String ... strings) {
        String finalMessage = message;
        int length = strings.length;
        for (int i = 0; i < length; i++) {
            finalMessage = finalMessage.replace("{" + (i+1) + "}", strings[i]);
        }
        return finalMessage;
    }
    
    public String getInfoMessage(String ... strings) {
        return PREFIX.message + getMessage(strings);
    }
    
    public String getSuccessMessage(String ... strings) {
        return SUCCESS_TAG.message + getMessage(strings);
    }
    
    public String getWarningMessage(String ... strings) {
        return WARNING_TAG.message + getMessage(strings);
    }

}
