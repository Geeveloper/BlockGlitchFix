package me.geekles.blockglitchfix.runnables;

import java.util.HashSet;
import java.util.UUID;

import me.geekles.blockglitchfix.api.listeners.WorldWatcher;
import me.geekles.blockglitchfix.config.ConfigData;
import me.geekles.blockglitchfix.mechanism.GlitchMechanic;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.geekles.blockglitchfix.BlockGlitchFix;
import me.geekles.blockglitchfix.api.listeners.BlockUpdateEvent;
import me.geekles.blockglitchfix.api.listeners.BlockUpdateEvent.BlockUpdateReason;

public class BlockUpdateChecker extends BukkitRunnable {

    private static BlockUpdateChecker instance;
    private BlockGlitchFix plugin;

    public BlockUpdateChecker getInstance() {
        return instance;
    }

    public BlockUpdateChecker(BlockGlitchFix plugin) {
        this.plugin = plugin;
        instance = this;
    }

    /**
     * Stop checking players.
     */
    public void stopCheck() {
        plugin.getData().blockCheck.clear();
        cancel();
    }

    @SuppressWarnings("unchecked")
    public void run() {
        for (UUID id : (HashSet<UUID>) plugin.getData().blockCheck.clone()) {
            Player player = Bukkit.getPlayer(id);
            World world = player.getLocation().getWorld();
            if (WorldWatcher.getBlacklistedWorlds().stream().anyMatch(w -> w.getUID().equals(world.getUID())))
                continue;

            BlockUpdateEvent event = new BlockUpdateEvent(Bukkit.getPlayer(id), BlockUpdateReason.FAST_BREAKING);
            /* Creates an instance of a custom listener class and is ready to call. */
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) { // makes sure the called event hasn't been cancelled
                GlitchMechanic.updateBlocks(player, ConfigData.RADIUS.getInt()); // updates blocks
            }
        }
    }

}
