package dev.qruet.solidfix.timers;

import dev.qruet.solidfix.CoreManager;
import dev.qruet.solidfix.SolidManager;
import dev.qruet.solidfix.SolidServer;
import dev.qruet.solidfix.config.ConfigData;
import dev.qruet.solidfix.events.BlockUpdateEvent;
import dev.qruet.solidfix.player.SolidMiner;
import dev.qruet.solidfix.utils.BlockUpdateUtil;
import dev.qruet.solidfix.utils.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * @author qruet
 * @version 1.9_01
 */
public class BlockUpdateManager extends SolidManager {

    private Task task;

    public BlockUpdateManager(CoreManager.Registrar registrar) {
        super(registrar);
    }

    public boolean init() {
        if (task != null)
            throw new UnsupportedOperationException("Can not yet disable a non-initialized class.");
        task = new Task();
        task.start();
        return true;
    }

    public boolean disable() {
        if (task == null)
            return false;
        task.cancel();
        return true;
    }

    private class Task extends Thread {

        private Iterator<SolidMiner> mIterator;
        private boolean cancelled;

        public boolean isCancelled() {
            return cancelled;
        }

        public void cancel() {
            cancelled = true;
        }

        public void run() {
            while (!cancelled) {
                mIterator = SolidServer.getOnlinePlayers()
                        .stream()
                        .filter(SolidMiner::isFastMining)
                        .collect(Collectors.toList())
                        .iterator();

                while (mIterator.hasNext()) {
                    SolidMiner miner = mIterator.next();
                    Player player = miner.getPlayer();
                    if (player == null)
                        continue;

                    if (SolidServer.getWorlds().stream().noneMatch(w -> w.getUID().equals(player.getWorld().getUID())))
                        continue;

                    BlockUpdateEvent event = new BlockUpdateEvent(player, BlockUpdateEvent.BlockUpdateReason.FAST_BREAKING);
                    // Creates an instance of a custom listener class and is ready to call.
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) { // makes sure the called event hasn't been cancelled
                        BlockUpdateUtil.updateBlocks(player, miner.getRecentBrokenBlock().getLocation(), ConfigData.RADIUS.getInt()); // updates blocks
                    }

                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    long delay = (long) (50 * Math.pow((20.0 / ServerUtil.getTPS()), 4));
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}