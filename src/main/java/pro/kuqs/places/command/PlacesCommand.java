package pro.kuqs.places.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pro.kuqs.places.menu.PlaceMenu;

public class PlacesCommand implements CommandExecutor {

    @Override
    public boolean onCommand( CommandSender commandSender, Command command, String label, String[] args ) {
        if ( !( commandSender instanceof Player ) ) {
            commandSender.sendMessage( "Nicht fuer dich!" );
            return true;
        }
        Player player = (Player) commandSender;

        if ( args.length == 1 ) {
            player.openInventory( new PlaceMenu( player, args[0] ).getInventory() );
            return true;
        }
        player.openInventory( new PlaceMenu( player, null ).getInventory() );
        return true;
    }
}
