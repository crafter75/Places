package pro.kuqs.places.menu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import pro.kuqs.places.Place;
import pro.kuqs.places.util.CustomInventory;
import pro.kuqs.places.util.ItemBuilder;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.IntStream;

public class PlaceMenu extends CustomInventory {

    private static final ItemStack PREVIOUS_PAGE = new ItemBuilder( Material.PLAYER_HEAD ).setSkullValue( UUID.fromString( "a68f0b64-8d14-4000-a95f-4b9ba14f8df9" ),
            "eyJ0aW1lc3RhbXAiOjE1NjA3OTg4NTg1MTEsInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mN2FhY2FkMTkzZTIyMjY5NzFlZDk1MzAyZGJhNDMzNDM4YmU0NjQ0ZmJhYjVlYmY4MTgwNTQwNjE2NjdmYmUyIn19fQ==" )
            .name( "§bVorherige Seite" ).build();

    private static final ItemStack NEXT_PAGE = new ItemBuilder( Material.PLAYER_HEAD ).setSkullValue( UUID.fromString( "50c8510b-5ea0-4d60-be9a-7d542d6cd156" ),
            "eyJ0aW1lc3RhbXAiOjE1NjI2NzQ3NDM2MjcsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM0ZWYwNjM4NTM3MjIyYjIwZjQ4MDY5NGRhZGMwZjg1ZmJlMDc1OWQ1ODFhYTdmY2RmMmU0MzEzOTM3NzE1OCJ9fX0=" )
            .name( "§bNächste Seite" ).build();

    private static final int INV_SIZE = 45;

    private List<Place> currentPlaces;

    private int page, maxPages;

    private Mode mode;

    public enum Mode {
        NORMAL,
        DELETE,
        TELEPORT;
    }

    public PlaceMenu( Player player, String title, List<Place> places ) {
        super( 6 * 9, title.length() > 32 ? title.substring( 0, 32 ) : title, player );
        this.page = 0;
        this.maxPages = places.size() / INV_SIZE;
        if ( places.size() % INV_SIZE != 0 || places.isEmpty() ) {
            this.maxPages++;
        }
        this.currentPlaces = places;
        this.mode = Mode.NORMAL;
        if ( title.contains( "löschen" ) ) {
            this.mode = Mode.DELETE;
        } else if ( title.contains( "TPn" ) ) {
            this.mode = Mode.TELEPORT;
        }

        this.setItemsForCurrentPage();

        if ( this.currentPlaces.isEmpty() ) {
            this.getInventory().setItem( 22, new ItemBuilder( Material.BARRIER ).name( "§cKeine Places gefunden!" ).build() );
        }
    }

    @Override
    public void onClick( InventoryClickEvent event ) {
        event.setCancelled( true );

        if ( Objects.requireNonNull( event.getCurrentItem() ).getType().equals( Material.PLAYER_HEAD ) ) {
            switch ( ChatColor.stripColor( Objects.requireNonNull( event.getCurrentItem().getItemMeta() ).getDisplayName().toLowerCase() ) ) {
                case "vorherige seite":
                    if ( this.page > 0 ) {
                        this.page--;
                        this.setItemsForCurrentPage();
                    }
                    return;
                case "nächste seite":
                    if ( this.page < ( this.maxPages - 1 ) ) {
                        this.page++;
                        this.setItemsForCurrentPage();
                    }
                    return;
                default:
                    String desc = ChatColor.stripColor( event.getCurrentItem().getItemMeta().getDisplayName() );
                    this.currentPlaces.stream().filter( place -> place.getDescription().equalsIgnoreCase( desc ) ).findFirst().ifPresent( place -> {
                        event.getView().close();

                        switch ( this.mode ) {
                            case DELETE:
                                this.getPlayer().performCommand( "place del " + place.getDescription() );
                                return;
                            case TELEPORT:
                                this.getPlayer().performCommand( "place teleport " + place.getDescription() );
                                return;
                            default:
                                Toolkit.getDefaultToolkit().getSystemClipboard().setContents( new StringSelection(
                                        "X: " + place.getLocation().getBlockX() + " Y: " + place.getLocation().getBlockY() + " Z: " + place.getLocation().getBlockZ()
                                ), null );
                                this.getPlayer().sendMessage( "§aKoordinaten in die Zwischenablage gespeichert!" );
                        }
                    } );
                    return;
            }
        }
    }

    @Override
    public void onOpen( InventoryOpenEvent event ) {

    }

    @Override
    public void onClose( InventoryCloseEvent event ) {

    }

    private void setItemsForCurrentPage() {
        IntStream.range( 0, INV_SIZE ).forEach( i -> this.getInventory().setItem( i, null ) );

        int offset = page * INV_SIZE;
        for ( int i = 0; i + offset < this.currentPlaces.size() && i < INV_SIZE; i++ ) {
            Place place = this.currentPlaces.get( ( i + offset ) );

            this.getInventory().setItem( i, new ItemBuilder( Material.PLAYER_HEAD ).name( "§e" + place.getDescription() )
                    .lore( Arrays.asList(
                            "§7§oX: " + place.getLocation().getBlockX(),
                            "§7§oY: " + place.getLocation().getBlockY(),
                            "§7§oZ: " + place.getLocation().getBlockZ(),
                            "§7§oVon: " + place.getCreatorName() )
                    ).setSkullOwner( Bukkit.getOfflinePlayer( place.getCreatorUUID() ) ).build() );
        }

        this.getInventory().setItem( INV_SIZE, PREVIOUS_PAGE );
        this.getInventory().setItem( ( INV_SIZE + 8 ), NEXT_PAGE );
        this.getInventory().setItem( ( INV_SIZE + 4 ), new ItemBuilder( Material.PAPER ).name( "§bSeite " + ( this.page + 1 ) + "/" + this.maxPages ).build() );
    }
}
