package pro.kuqs.places.command;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import pro.kuqs.places.Place;
import pro.kuqs.places.Places;
import pro.kuqs.places.menu.PlaceMenu;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

public class PlaceCommand implements CommandExecutor, TabCompleter {

    private Places places;

    public PlaceCommand( Places places ) {
        this.places = places;
    }

    @Override
    public boolean onCommand( CommandSender commandSender, Command command, String label, String[] args ) {
        if ( !( commandSender instanceof Player ) ) {
            commandSender.sendMessage( "Nicht fuer dich!" );
            return true;
        }
        Player player = (Player) commandSender;

        if ( args.length >= 2 ) {
            if ( args[0].equalsIgnoreCase( "set" ) ) {
                String desc;
                Location location;
                if ( args.length >= 5 ) {
                    try {
                        location = new Location( player.getWorld(), Integer.parseInt( args[1] ), Integer.parseInt( args[2] ), Integer.parseInt( args[3] ) );
                        desc = this.joinStrings( args, 4 );
                    } catch ( NumberFormatException e ) {
                        location = player.getLocation();
                        desc = this.joinStrings( args, 1 );
                    }
                } else {
                    desc = this.joinStrings( args, 1 );
                    location = player.getLocation();
                }

                if ( this.getPlace( desc ).isPresent() ) {
                    player.sendMessage( "§cEin Place mit dieser Beschreibung existiert bereits!" );
                    return true;
                }

                Place place = new Place( desc, location, player.getUniqueId() );
                try {
                    this.places.getPlaceConfig().savePlace( place );
                    this.places.getPlaces().add( place );
                    player.sendMessage( "§aPlace erfolgreich erstellt!" );
                } catch ( IOException e ) {
                    e.printStackTrace();
                    player.sendMessage( "§cFehler beim erstellen des Places!" );
                }
                return true;
            } else if ( args[0].equalsIgnoreCase( "del" ) ) {
                String desc = this.joinStrings( args, 1 );

                Optional<Place> optionalPlace = this.getPlace( desc );
                if ( !optionalPlace.isPresent() ) {
                    player.sendMessage( "§cEs wurde kein Place mit dieser Beschreibung gefunden!" );
                    return true;
                }
                try {
                    this.places.getPlaceConfig().deletePlace( optionalPlace.get() );
                    this.places.getPlaces().remove( optionalPlace.get() );
                    player.sendMessage( "§aPlace erfolgreich gelöscht!" );
                } catch ( IOException e ) {
                    e.printStackTrace();
                    player.sendMessage( "§cFehler beim löschen des Places!" );
                }
                return true;
            } else if ( args[0].equalsIgnoreCase( "teleport" ) ) {
                if ( !player.isOp() ) {
                    player.sendMessage( "§cKeine Rechte!" );
                    return true;
                }
                String desc = this.joinStrings( args, 1 );

                Optional<Place> optionalPlace = this.getPlace( desc );
                if ( !optionalPlace.isPresent() ) {
                    player.sendMessage( "§cEs wurde kein Place mit dieser Beschreibung gefunden!" );
                    return true;
                }
                player.teleport( optionalPlace.get().getLocation() );
                player.sendMessage( "§aDu wurdest zum Place '§e" + optionalPlace.get().getDescription() + "§a' teleportiert." );
                return true;
            }
        } else {
            if ( args[0].equalsIgnoreCase( "del" ) || args[0].equalsIgnoreCase( "delete" ) ) {
                player.openInventory( new PlaceMenu( player, "§cWähle ein Place zum löschen", this.places.getPlaces() ).getInventory() );
                return true;
            } else if ( args[0].equalsIgnoreCase( "teleport" ) || args[0].equalsIgnoreCase( "tp" ) ) {
                if ( !player.isOp() ) {
                    player.sendMessage( "§cKeine Rechte!" );
                    return true;
                }
                player.openInventory( new PlaceMenu( player, "§cWähle ein Place zum TPn", this.places.getPlaces() ).getInventory() );
                return true;
            }
        }
        player.sendMessage( "§aPlace Commands" );
        player.sendMessage( "§e/place set <X> <Y> <Z> <Description>" );
        player.sendMessage( "§e/place set <Description>" );
        player.sendMessage( "§e/place delete [Description]" );
        player.sendMessage( "§e/place teleport [Description]" );
        player.sendMessage( "§e/place" );
        player.sendMessage( "§e/place <Name>" );
        return false;
    }

    private Optional<Place> getPlace( String desc ) {
        return this.places.getPlaces().stream().filter( place -> place.getDescription().equalsIgnoreCase( desc ) ).findFirst();
    }

    private String joinStrings( String[] args, int start ) {
        StringBuilder stringBuilder = new StringBuilder();
        IntStream.range( start, args.length ).forEach( i -> stringBuilder.append( args[i] ).append( " " ) );
        return stringBuilder.toString().trim();
    }

    @Override
    public List<String> onTabComplete( CommandSender commandSender, Command command, String s, String[] strings ) {
        return null;
    }
}
