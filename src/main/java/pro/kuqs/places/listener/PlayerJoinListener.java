package pro.kuqs.places.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import pro.kuqs.places.Places;

import java.util.Objects;

public class PlayerJoinListener implements Listener {

    private Places places;

    public PlayerJoinListener( Places places ) {
        this.places = places;
    }

    @EventHandler
    public void onJoin( PlayerJoinEvent event ) {
        Player player = event.getPlayer();

        if( !player.getScoreboard().equals( Objects.requireNonNull( Bukkit.getScoreboardManager() ).getMainScoreboard() ) ) {
            player.setScoreboard( Bukkit.getScoreboardManager().getMainScoreboard() );
        }
    }
}
