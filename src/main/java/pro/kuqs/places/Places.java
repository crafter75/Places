package pro.kuqs.places;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import pro.kuqs.places.command.PlaceCommand;
import pro.kuqs.places.command.PlacesCommand;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Getter
public class Places extends JavaPlugin {

    @Getter
    private static Places instance;

    private List<Place> places;

    private PlaceConfig placeConfig;

    @Override
    public void onEnable() {
        instance = this;

        // Init config
        try {
            this.placeConfig = new PlaceConfig( this.getDataFolder() );
        } catch ( IOException e ) {
            e.printStackTrace();
            System.out.println("[Places] Can't create config!");
            return;
        }

        // Load places
        this.places = this.placeConfig.getPlacesFromFile();

        // Register command
        Objects.requireNonNull( this.getCommand( "place" ) ).setExecutor( new PlaceCommand( this ) );
        Objects.requireNonNull( this.getCommand( "places" ) ).setExecutor( new PlacesCommand() );
    }

    @Override
    public void onDisable() {

    }
}
