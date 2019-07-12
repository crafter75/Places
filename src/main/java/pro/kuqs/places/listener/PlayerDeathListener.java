package pro.kuqs.places.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import pro.kuqs.places.Places;

import java.io.IOException;

public class PlayerDeathListener implements Listener {

    private Places places;

    public PlayerDeathListener( Places places ) {
        this.places = places;
    }

    @EventHandler
    public void onDeath( PlayerDeathEvent event ) {
        Player player = event.getEntity();

        this.places.getDeathPoints().put( player.getUniqueId(), player.getLocation() );
        try {
            this.places.getPlaceConfig().saveDeathPoint( player.getUniqueId(), player.getLocation() );
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
}
