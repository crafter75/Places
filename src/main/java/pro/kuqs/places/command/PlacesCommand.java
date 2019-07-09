package pro.kuqs.places.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pro.kuqs.places.Places;
import pro.kuqs.places.menu.PlaceMenu;

import java.util.stream.Collectors;

public class PlacesCommand implements CommandExecutor {

    private Places places;

    public PlacesCommand( Places places ) {
        this.places = places;
    }

    @Override
    public boolean onCommand( CommandSender commandSender, Command command, String label, String[] args ) {
        if ( !( commandSender instanceof Player ) ) {
            commandSender.sendMessage( "Nicht fuer dich!" );
            return true;
        }
        Player player = (Player) commandSender;

        if ( args.length == 1 ) {
            if ( args[0].equalsIgnoreCase( "help" ) ) {
                player.sendMessage( "§aPlace Commands" );
                player.sendMessage( "§e/place set <X> <Y> <Z> <Description>" );
                player.sendMessage( "§e/place set <Description>" );
                player.sendMessage( "§e/place delete [Description]" );
                player.sendMessage( "§e/place teleport [Description]" );
                player.sendMessage( "§e/place" );
                player.sendMessage( "§e/place <Name>" );
                return true;
            }
            player.openInventory( new PlaceMenu( player, "§aPlaces von " + args[0], this.places.getPlaces().stream().filter( place ->
                    place.getCreatorName().equalsIgnoreCase( args[0] ) ).collect( Collectors.toList() ) ).getInventory() );
            return true;
        }
        player.openInventory( new PlaceMenu( player, "§aPlaces", this.places.getPlaces() ).getInventory() );
        return true;
    }
}
