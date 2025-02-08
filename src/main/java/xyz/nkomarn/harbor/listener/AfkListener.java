package xyz.nkomarn.harbor.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.provider.DefaultAFKProvider;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class AfkListener implements Listener {
    private final DefaultAFKProvider afkProvider;
    private Queue<AfkPlayer> players;
    private final Harbor harbor;
    private boolean status;

    public AfkListener(@NotNull DefaultAFKProvider afkProvider) {
        this.afkProvider = afkProvider;
        this.harbor = afkProvider.getHarbor();
        harbor.getLogger().info("Initializing fallback AFK detection system. Fallback AFK system is not enabled at this time");
        status = false;
    }

    /**
     * Provides a way to start the listener
     */
    public void start() {
        if(!status) {
            status = true;
            players = new ArrayDeque<>();
            // Populate the queue with any existing players
            players.addAll(Bukkit.getOnlinePlayers().stream().map((Function<Player, AfkPlayer>) AfkPlayer::new).collect(Collectors.toSet()));

            // Register listeners after populating the queue
            Bukkit.getServer().getPluginManager().registerEvents(this, harbor);

            // We want every player to get a check every 20 ticks. The runnable smooths out checking a certain
            // percentage of players over all 20 ticks. Thusly, the runnable must run on every tick

            harbor.getLogger().info("Fallback AFK detection system is enabled");
        } else {
            harbor.getLogger().info("Fallback AFK detection system was already enabled");
        }
    }

    /**
     * Provides a way to halt the listener
     */
    public void stop() {
        if(status) {
            status = false;
            HandlerList.unregisterAll(this);
            players = null;
            harbor.getLogger().info("Fallback AFK detection system is disabled");
        } else {
            harbor.getLogger().info("Fallback AFK detection system was already disabled");
        }

    }

    private static final class AfkPlayer {
        private final Player player;
        private int locationHash;

        public AfkPlayer(Player player) {
            this.player = player;
            locationHash = player.getEyeLocation().hashCode();
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AfkPlayer afkPlayer = (AfkPlayer) o;
            return player.getUniqueId().equals(afkPlayer.player.getUniqueId());
        }

        @Override
        public int hashCode() {
            return player.getUniqueId().hashCode();
        }
    }
}
