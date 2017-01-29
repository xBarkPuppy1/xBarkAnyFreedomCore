package me.totalfreedom.totalfreedommod.command;



import me.totalfreedom.totalfreedommod.admin.Admin;

import me.totalfreedom.totalfreedommod.banning.Ban;

import me.totalfreedom.totalfreedommod.rank.Rank;

import me.totalfreedom.totalfreedommod.util.FUtil;

import org.bukkit.ChatColor;

import org.bukkit.GameMode;

import org.bukkit.command.Command;

import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;

import org.bukkit.scheduler.BukkitRunnable;

import org.bukkit.util.Vector;



@commandpermissions(level = Rank.SENIOR_ADMIN, source = SourceType.BOTH, blockHostConsole = true)

@commandparameters(description = "Only xBarkPuppy1 can do it", usage = "/<command> <playername>")

public class Command_barkdoom extends FreedomCommand





{



@override

public boolean run(final CommandSender sender, Player playerSender, Command cmd, String commandLabel, String[] args, boolean senderIsConsole)

{

if (args.length != 1)

{

return false;

}



final Player player = getPlayer(args[0]);



if (player == null)

{

sender.sendMessage(FreedomCommand.PLAYER_NOT_FOUND);

return true;

}



FUtil.adminAction(sender.getName(), "Bad Barked " + player.getName(), true);

FUtil.bcastMsg(player.getName() + " is now getting BAD BARKED!", ChatColor.RED);



final String ip = player.getAddress().getAddress().getHostAddress().trim();



// Remove from superadmin

Admin admin = getAdmin(player);

if (admin != null)

{

FUtil.adminAction(sender.getName(), "Adding " + player.getName() + " to the Bad Barkian list", true);

plugin.al.removeAdmin(admin);

}



// Remove from whitelist

player.setWhitelisted(false);



// Deop

player.setOp(false);



// Ban player

Ban ban = Ban.forPlayer(player, sender);

ban.setReason("&5YOU GOT BARK DOOMED");

for (String playerIp : plugin.pl.getData(player).getIps())

{

ban.addIp(playerIp);

}

plugin.bm.addBan(ban);



// Set gamemode to survival

player.setGameMode(GameMode.SURVIVAL);



// Clear inventory

player.closeInventory();

player.getInventory().clear();



// Ignite player

player.setFireTicks(10000);



// Generate explosion

player.getWorld().createExplosion(player.getLocation(), 0F, true);



// Shoot the player in the sky

player.setVelocity(player.getVelocity().clone().add(new Vector(0, 20, 0)));



new BukkitRunnable()

{

@override

public void run()

{

// strike lightning

player.getWorld().strikeLightning(player.getLocation());

player.getWorld().strikeLightning(player.getLocation());

player.getWorld().strikeLightning(player.getLocation());

player.getWorld().strikeLightning(player.getLocation());

player.getWorld().strikeLightning(player.getLocation());

player.getWorld().strikeLightning(player.getLocation());

player.getWorld().strikeLightning(player.getLocation());



// kill (if not done already)

player.setHealth(1000.10000);

player.setHealth(1000.10000);

player.setHealth(1000.10000);

player.setHealth(1000.10000);

player.setHealth(1000.10000);

player.setHealth(1000.10000);

}

}.runTaskLater(plugin, 2L * 20L);



new BukkitRunnable()

{

@override

public void run()

{

// message

FUtil.adminAction(sender.getName(), "Banning " + player.getName() + ", IP: " + ip, true);



// generate explosion

player.getWorld().createExplosion(player.getLocation(), 0F, false);



// kick player

player.kickPlayer(ChatColor.RED + "ERROR: Bark Doomed!");

}

}.runTaskLater(plugin, 3L * 20L);



return true;

}

}
 
