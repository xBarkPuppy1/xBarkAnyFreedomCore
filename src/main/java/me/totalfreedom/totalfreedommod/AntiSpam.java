package me.totalfreedom.totalfreedommod;

import me.totalfreedom.totalfreedommod.player.FPlayer;
import me.totalfreedom.totalfreedommod.util.FSync;
import me.totalfreedom.totalfreedommod.util.FUtil;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class AntiSpam extends FreedomService
{
    /*
     
     https://www.youtube.com/watch?v=ZADJ8S1qH3U
     
     
     [Intro]
     Lets get it
     Steve Drive
     R.I.P L'A Capone
     OTF
     These nigga's steady woofing like they want beef, want beef?
     You want smoke? You want smoke? Just tell me
     
     [Hook]
     These niggas steady woofing like they want beef, want beef?[You want smoke?]
     I can make that happen if you want beef, you want beef?[You want smoke?]
     Catch you while you're capping with this semi, semi
     Put the semi-automatic to your kidney, kidney
     
     [Verse 1]
     Put the semi-automatic to your kidney, kidney
     I'm off the dope I got the pole you talking tough you getting smoked
     These nigga's steady woofing like they want beef, but I really know
     Glock or nickel yeah that bitch go, I'm going like I'm at a fucking show
     I'm off Tu pack saying fuck Jojo[Fuck Jojo]
     Talking shit get your life took no joke
     I'm with my nigga's and my niggas ain't no joke
     And if you got that fucking bag then you getting poked
     And if you acting tough, I'ma fucking blow, and that's on Pluto[On Pluto]
     Me and Durk finna spaz, and I put that on the guys, it's homicides
     Cause we dropping Y's[Die Y, Die Y], head shot got him traumatized
     And you want beef?[You want beef fu nigga?] but when I see you, you don't speak?[You don't even talk]
     I got my 9 on me[Rondo] and I'ma blow and that's on me[I'm Rondo]
     I'm getting tree tree[Getting dope], getting top from a bitch named Kiki
     
     [Chorus]
     
     [Verse 2]
     And if you really want smoke[You want smoke little nigga?]
     I will give your ass smoke[Give your ass smoke little nigga?]
     This Glock 9 bitch I tote, and I will put it to your throat
     I'm off this Tooka pack and no L'A, I'ma go crazy
     You supposed to be my nigga but actin' like a fan that’s crazy
     What the fuck wrong with these nigga's, they fugazi
     I made this song for the niggas, cause they crazy
     Separate me from them niggas[Separate Rondo]
     Pull up on your block, with the mops
     Then I hit the dip and put him up in case of attempts[Incase a nigga survive]
     But we don't make throws, we shoot like Pimp[We shoot to kill]
     I got 23[Two three] So i don't fucking speak[I don't speak]
     Riding fast, I hit the dash, ain't gon last[You ain't gonna last nigga], I'ma blast[Cause ima blast on a nigga]
     Numba Nine, bitch [I'm #9 lil nigga], and I'm a sav, bitch
     
     [Chorus]
     */

    public static final int MSG_PER_CYCLE = 8;
    public static final int TICKS_PER_CYCLE = 2 * 10;
    //
    public BukkitTask cycleTask = null;

    public AntiSpam(TotalFreedomMod plugin)
    {
        super(plugin);
    }

    @Override
    protected void onStart()
    {
        new BukkitRunnable()
        {

            @Override
            public void run()
            {
                cycle();
            }
        }.runTaskTimer(plugin, TICKS_PER_CYCLE, TICKS_PER_CYCLE);
    }

    @Override
    protected void onStop()
    {
        FUtil.cancel(cycleTask);
    }

    private void cycle()
    {
        for (Player player : server.getOnlinePlayers())
        {
            final FPlayer playerdata = plugin.pl.getPlayer(player);

            // TODO: Move each to their own section
            playerdata.resetMsgCount();
            playerdata.resetBlockDestroyCount();
            playerdata.resetBlockPlaceCount();
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event)
    {
        final Player player = event.getPlayer();
        String message = event.getMessage().trim();

        final FPlayer playerdata = plugin.pl.getPlayerSync(player);

        // Check for spam
        if (playerdata.incrementAndGetMsgCount() > MSG_PER_CYCLE)
        {
            FSync.bcastMsg(player.getName() + " was automatically kicked for spamming chat.", ChatColor.RED);
            FSync.autoEject(player, "Kicked for spamming chat.");

            playerdata.resetMsgCount();

            event.setCancelled(true);
            return;
        }

        // Check for message repeat
        if (playerdata.getLastMessage().equalsIgnoreCase(message))
        {
            FSync.playerMsg(player, "Please do not repeat messages.");
            event.setCancelled(true);
            return;
        }

        playerdata.setLastMessage(message);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
    {
        String command = event.getMessage();
        final Player player = event.getPlayer();
        final FPlayer fPlayer = plugin.pl.getPlayer(player);
        fPlayer.setLastCommand(command);

        if (fPlayer.allCommandsBlocked())
        {
            FUtil.playerMsg(player, "Your commands have been blocked by an admin.", ChatColor.RED);
            event.setCancelled(true);
            return;
        }

        if (fPlayer.incrementAndGetMsgCount() > MSG_PER_CYCLE)
        {
            FUtil.bcastMsg(player.getName() + " was kicked in the nuts for spamming cmds.", ChatColor.RED);
            plugin.ae.autoEject(player, "Kicked for spamming commands.");

            fPlayer.resetMsgCount();
            event.setCancelled(true);
        }
    }

}
