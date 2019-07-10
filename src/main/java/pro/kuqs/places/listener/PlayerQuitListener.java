package pro.kuqs.places.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import pro.kuqs.places.Places;

import java.util.Objects;

public class PlayerQuitListener implements Listener {

    private Places places;

    public PlayerQuitListener( Places places ) {
        this.places = places;
    }

    @EventHandler
    public void onQuit( PlayerQuitEvent event ) {
        Player player = event.getPlayer();

        this.places.removeNavigation( player );
    }
}
