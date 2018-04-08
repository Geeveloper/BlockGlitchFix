package me.geekles.blockglitchfix;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

/** You should not have to extend or instantiate this class for any reason. This
 * class is used to store the data. */
public class BlockGlitchFixData {

	public long COOLDOWN_CHECKER = 50;
	public long COOLDOWN_CHECKER_REMOVAL = 1250;
	public long UPDATE_INTERVAL = 5l;
	public int RADIUS = 4;

	public Map<UUID, Long> lastBreakTimeSlow = new HashMap<UUID, Long>();
	public Map<UUID, Long> lastBreakTime = new HashMap<UUID, Long>();
	public HashSet<UUID> blockCheck = new HashSet<UUID>();

	public void removeData(Player player) {
		removeData(player.getUniqueId());
	}

	public void removeData(UUID id) {
		blockCheck.remove(id);
		lastBreakTime.remove(id);
		lastBreakTimeSlow.remove(id);
	}

}
