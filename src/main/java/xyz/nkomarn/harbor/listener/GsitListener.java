package xyz.nkomarn.harbor.listener;

import dev.geco.gsit.api.event.PlayerPoseChangeEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import xyz.nkomarn.harbor.Harbor;
import xyz.nkomarn.harbor.task.Checker;

public class GsitListener implements Listener {

    private final Harbor harbor;
    private final Checker checker;

    public GsitListener(Harbor harbor, Checker checker) {
        this.harbor = harbor;
        this.checker = checker;
    }

    @EventHandler
    public void onPlayerPoseChange(PlayerPoseChangeEvent event) {
        Player player = event.getPlayer();
        if (event.getPose() == Pose.SLEEPING) {
            // Player is laying down, treat as sleeping
            checker.addSleepingPlayer(player);
        } else if (event.getPreviousPose() == Pose.SLEEPING) {
            // Player stopped laying down, treat as waking up
            checker.removeSleepingPlayer(player);
        }
    }
}
