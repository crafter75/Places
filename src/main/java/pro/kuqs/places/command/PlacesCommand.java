package pro.kuqs.places.command;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import pro.kuqs.places.Place;
import pro.kuqs.places.Places;
import pro.kuqs.places.menu.PlaceMenu;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PlacesCommand implements CommandExecutor, TabCompleter {

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
                if ( !this.places.hasPermissionsForPlace( player, optionalPlace.get() ) ) {
                    player.sendMessage( "§cKeine Rechte!" );
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
                String desc = this.joinStrings( args, 1 );

                Optional<Place> optionalPlace = this.getPlace( desc );
                if ( !optionalPlace.isPresent() ) {
                    player.sendMessage( "§cEs wurde kein Place mit dieser Beschreibung gefunden!" );
                    return true;
                }
                if ( !this.places.hasPermissionsForPlace( player, optionalPlace.get() ) ) {
                    player.sendMessage( "§cKeine Rechte!" );
                    return true;
                }
                player.teleport( optionalPlace.get().getLocation() );
                player.sendMessage( "§aDu wurdest zum Place '§e" + optionalPlace.get().getDescription() + "§a' teleportiert." );
                return true;
            } else if ( args[0].equalsIgnoreCase( "rename" ) ) {
                String desc = this.joinStrings( args, 1 );

                Optional<Place> optionalPlace = this.getPlace( desc );
                if ( !optionalPlace.isPresent() ) {
                    player.sendMessage( "§cEs wurde kein Place mit dieser Beschreibung gefunden!" );
                    return true;
                }
                if ( !this.places.hasPermissionsForPlace( player, optionalPlace.get() ) ) {
                    player.sendMessage( "§cKeine Rechte!" );
                    return true;
                }

                new AnvilGUI( this.places, player, "Name", ( p, reply ) -> {
                    Optional<Place> otherPlace = this.getPlace( reply );
                    if ( otherPlace.isPresent() ) {
                        return "§cBeschreibung existiert bereits!";
                    }
                    Place place = optionalPlace.get();
                    try {
                        this.places.getPlaceConfig().deletePlace( place );
                        place.setDescription( reply );
                        this.places.getPlaceConfig().savePlace( place );
                        player.sendMessage( "§aDer Place wurde zu '§e" + optionalPlace.get().getDescription() + "§a' umbennant." );
                        if ( player.getLevel() > 0 ) {
                            player.setLevel( ( player.getLevel() + 1 ) );
                        }
                        return null;
                    } catch ( IOException e ) {
                        e.printStackTrace();
                        player.sendMessage( "§cFehler beim umbenennen des Places!" );
                        return null;
                    }
                } );
                return true;
            } else if ( args[0].equalsIgnoreCase( "navigate" ) ) {
                String desc = this.joinStrings( args, 1 );

                Optional<Place> optionalPlace = this.getPlace( desc );
                if ( !optionalPlace.isPresent() ) {
                    player.sendMessage( "§cEs wurde kein Place mit dieser Beschreibung gefunden!" );
                    return true;
                }
                this.places.removeNavigation( player );
                Place place = optionalPlace.get();

                if ( !player.getWorld().getName().equals( Objects.requireNonNull( place.getLocation().getWorld() ).getName() ) ) {
                    player.sendMessage( "§cDu bist nicht in der gleichen Welt wie der Place!" );
                    return true;
                }
                Scoreboard scoreboard = Objects.requireNonNull( Bukkit.getScoreboardManager() ).getNewScoreboard();
                Objective objective = scoreboard.registerNewObjective( "navigate_place", "dummy", "§e" + place.getDescription() );
                objective.setDisplaySlot( DisplaySlot.SIDEBAR );
                objective.getScore( "§aX§7: §b" + place.getLocation().getBlockX() ).setScore( 5 );
                objective.getScore( "§aY§7: §b" + place.getLocation().getBlockY() ).setScore( 4 );
                objective.getScore( "§aZ§7: §b" + place.getLocation().getBlockZ() ).setScore( 3 );
                objective.getScore( "§aEntfernung§7:" ).setScore( 2 );
                Team team = scoreboard.registerNewTeam( "distance" );
                team.addEntry( "§b" );
                team.setSuffix( "" );
                objective.getScore( "§b" ).setScore( 1 );
                player.sendMessage( "§aDas Scoreboard zeigt dir den Weg zum Place!" );
                this.places.getNavigate().put( player.getUniqueId(), place.getLocation() );
                player.setScoreboard( scoreboard );
                return true;
            }
        } else if ( args.length == 1 ) {
            if ( args[0].equalsIgnoreCase( "del" ) || args[0].equalsIgnoreCase( "delete" ) ) {
                player.openInventory( new PlaceMenu( player, "§cWähle ein Place zum löschen", this.places.getPlaces() ).getInventory() );
                return true;
            } else if ( args[0].equalsIgnoreCase( "teleport" ) || args[0].equalsIgnoreCase( "tp" ) ) {
                player.openInventory( new PlaceMenu( player, "§cWähle ein Place zum TPn", this.places.getPlaces() ).getInventory() );
                return true;
            } else if ( args[0].equalsIgnoreCase( "rename" ) ) {
                player.openInventory( new PlaceMenu( player, "§cWähle ein Place zum umbenennen", this.places.getPlaces() ).getInventory() );
                return true;
            } else if ( args[0].equalsIgnoreCase( "help" ) ) {
                this.sendHelp( player );
                return true;
            } else if ( args[0].equalsIgnoreCase( "cancel" ) ) {
                this.places.removeNavigation( player );
                player.sendMessage( "§aNavigation wurde beendet." );
                return true;
            } else {
                player.openInventory( new PlaceMenu( player, "§aPlaces von " + args[0], this.places.getPlaces().stream().filter( place ->
                        place.getCreatorName().equalsIgnoreCase( args[0] ) ).collect( Collectors.toList() ) ).getInventory() );
                return true;
            }
        }
        player.openInventory( new PlaceMenu( player, "§aPlaces", this.places.getPlaces() ).getInventory() );
        return true;
    }

    private void sendHelp( Player player ) {
        player.sendMessage( "§aPlace Commands" );
        player.sendMessage( "§e/place set <X> <Y> <Z> <Description>" );
        player.sendMessage( "§e/place set <Description>" );
        player.sendMessage( "§e/place delete [Description]" );
        player.sendMessage( "§e/place teleport [Description]" );
        player.sendMessage( "§e/place rename [Description]" );
        player.sendMessage( "§e/place navigate [Description]" );
        player.sendMessage( "§e/place" );
        player.sendMessage( "§e/place <Name>" );
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
        // TODO
        return null;
    }
}
