package pro.kuqs.places;

import lombok.Getter;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Team;
import pro.kuqs.places.command.PlacesCommand;
import pro.kuqs.places.listener.PlayerDeathListener;
import pro.kuqs.places.listener.PlayerJoinListener;
import pro.kuqs.places.listener.PlayerQuitListener;

import java.io.IOException;
import java.util.*;

@Getter
public class Places extends JavaPlugin {

    @Getter
    private static Places instance;

    private List<Place> places;

    private Map<UUID, Location> deathPoints = new HashMap<>();

    private PlaceConfig placeConfig;

    private Map<UUID, Location> navigate = new HashMap<>();

    @Override
    public void onEnable() {
        instance = this;

        // Init config
        try {
            this.placeConfig = new PlaceConfig( this.getDataFolder() );
        } catch ( IOException e ) {
            e.printStackTrace();
            System.out.println( "[Places] Can't create config!" );
            return;
        }

        // Load places
        this.places = this.placeConfig.getPlacesFromFile();

        // Load death points
        this.deathPoints = this.placeConfig.getDeathPoints();

        // Register command
        Objects.requireNonNull( this.getCommand( "places" ) ).setExecutor( new PlacesCommand( this ) );

        // Register listener
        this.getServer().getPluginManager().registerEvents( new PlayerQuitListener( this ), this );
        this.getServer().getPluginManager().registerEvents( new PlayerJoinListener( this ), this );
        this.getServer().getPluginManager().registerEvents( new PlayerDeathListener( this ), this );

        // Start task
        Bukkit.getScheduler().runTaskTimer( this, () ->
                Bukkit.getOnlinePlayers().stream().filter( p -> this.navigate.containsKey( p.getUniqueId() ) ).forEach( player -> {
                    Location placeLoc = this.navigate.get( player.getUniqueId() );
                    if ( !player.getWorld().getName().equals( Objects.requireNonNull( placeLoc.getWorld() ).getName() ) ) {
                        player.sendMessage( "§cDu bist nicht in der gleichen Welt wie der Place!" );
                        this.removeNavigation( player );
                        return;
                    }
                    int distance = (int) player.getLocation().distance( placeLoc );

                    Team team = player.getScoreboard().getTeam( "distance" );
                    if ( team != null ) {
                        team.setSuffix( distance + " Blöcke" );
                    }

                    Block block = player.getTargetBlock( null, 5 );
                    int blockDistance = (int) block.getLocation().distance( placeLoc );

                    player.spigot().sendMessage( ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(
                            blockDistance < distance ? "§aDu läufst in die richtige Richtung!" : "§cDu läufst in die falsche Richtung!" ) );

                    if ( distance <= 3 ) {
                        player.sendMessage( "§aDu hast dein Ziel erreicht!" );
                        this.removeNavigation( player );
                    }
                } ), 20L, 20L );
    }

    public void removeNavigation( Player player ) {
        if ( this.navigate.containsKey( player.getUniqueId() ) ) {
            Objects.requireNonNull( player.getScoreboard().getTeam( "distance" ) ).unregister();
            Objects.requireNonNull( player.getScoreboard().getObjective( "navigate_place" ) ).unregister();
            player.setScoreboard( Objects.requireNonNull( Bukkit.getScoreboardManager() ).getMainScoreboard() );
            this.navigate.remove( player.getUniqueId() );
            player.spigot().sendMessage( ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText( "§e" ) );
        }
    }

    public boolean hasPermissionsForPlace( Player player, Place place ) {
        if ( !this.placeConfig.usePermissions() ) return true;
        if ( player.isOp() ) return true;
        return place.getCreatorUUID().equals( player.getUniqueId() );
    }
}
