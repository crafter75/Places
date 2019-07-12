package pro.kuqs.places.menu;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import pro.kuqs.places.util.CustomInventory;
import pro.kuqs.places.util.ItemBuilder;

import java.util.*;
import java.util.stream.IntStream;

public class DeathPointMenu extends CustomInventory {

    private static final ItemStack PREVIOUS_PAGE = new ItemBuilder( Material.PLAYER_HEAD ).setSkullValue( UUID.fromString( "a68f0b64-8d14-4000-a95f-4b9ba14f8df9" ),
            "eyJ0aW1lc3RhbXAiOjE1NjA3OTg4NTg1MTEsInByb2ZpbGVJZCI6ImE2OGYwYjY0OGQxNDQwMDBhOTVmNGI5YmExNGY4ZGY5IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dMZWZ0Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9mN2FhY2FkMTkzZTIyMjY5NzFlZDk1MzAyZGJhNDMzNDM4YmU0NjQ0ZmJhYjVlYmY4MTgwNTQwNjE2NjdmYmUyIn19fQ==" )
            .name( "§bVorherige Seite" ).build();

    private static final ItemStack NEXT_PAGE = new ItemBuilder( Material.PLAYER_HEAD ).setSkullValue( UUID.fromString( "50c8510b-5ea0-4d60-be9a-7d542d6cd156" ),
            "eyJ0aW1lc3RhbXAiOjE1NjI2NzQ3NDM2MjcsInByb2ZpbGVJZCI6IjUwYzg1MTBiNWVhMDRkNjBiZTlhN2Q1NDJkNmNkMTU2IiwicHJvZmlsZU5hbWUiOiJNSEZfQXJyb3dSaWdodCIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDM0ZWYwNjM4NTM3MjIyYjIwZjQ4MDY5NGRhZGMwZjg1ZmJlMDc1OWQ1ODFhYTdmY2RmMmU0MzEzOTM3NzE1OCJ9fX0=" )
            .name( "§bNächste Seite" ).build();

    private static final int INV_SIZE = 45;

    private int page, maxPages;

    private Map<UUID, Location> currentDeathPoints;

    public DeathPointMenu( Player player, Map<UUID, Location> deathPoints ) {
        super( 6 * 9, "§cWähle einen Death point", player );
        this.currentDeathPoints = deathPoints;
        this.page = 0;
        this.maxPages = deathPoints.size() / INV_SIZE;
        if ( deathPoints.size() % INV_SIZE != 0 || deathPoints.isEmpty() ) {
            this.maxPages++;
        }
        this.setItemsForCurrentPage();

        if ( deathPoints.isEmpty() ) {
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
                    String name = ChatColor.stripColor( event.getCurrentItem().getItemMeta().getDisplayName() );
                    this.getPlayer().performCommand( "places deathpoint " + name );
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

        List<UUID> uuids = new ArrayList<>( this.currentDeathPoints.keySet() );
        int offset = page * INV_SIZE;
        for ( int i = 0; i + offset < uuids.size() && i < INV_SIZE; i++ ) {
            UUID uuid = uuids.get( ( i + offset ) );

            OfflinePlayer player = Bukkit.getOfflinePlayer( uuid );
            Location location = this.currentDeathPoints.get( uuid );
            this.getInventory().setItem( i, new ItemBuilder( Material.PLAYER_HEAD ).name( "§e" + player.getName() )
                    .lore( Arrays.asList(
                            "§7§oX: " + location.getBlockX(),
                            "§7§oY: " + location.getBlockY(),
                            "§7§oZ: " + location.getBlockZ() )
                    ).setSkullOwner( player ).build() );
        }

        this.getInventory().setItem( INV_SIZE, PREVIOUS_PAGE );
        this.getInventory().setItem( ( INV_SIZE + 8 ), NEXT_PAGE );
        this.getInventory().setItem( ( INV_SIZE + 4 ), new ItemBuilder( Material.PAPER ).name( "§bSeite " + ( this.page + 1 ) + "/" + this.maxPages ).build() );
    }
}
