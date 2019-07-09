package pro.kuqs.places.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import pro.kuqs.places.Places;

import java.util.Optional;

@Getter
public abstract class CustomInventory implements Listener {

    private Inventory inventory;

    private Player player;

    @Setter
    private boolean ignoreClose = false;

    public CustomInventory( int size, String title ) {
        this.inventory = Bukkit.createInventory( null, size, title );

        Bukkit.getServer().getPluginManager().registerEvents( this, Places.getInstance() );
    }

    public CustomInventory( int size, String title, Player player ) {
        this( size, title );
        this.player = player;
    }

    public void setItem( int slot, ItemStack item ) {
        this.inventory.setItem( slot, item );
    }

    public void addItem( ItemStack... item ) {
        this.inventory.addItem( item );
    }

    public Optional<ItemStack> getItem( int slot ) {
        ItemStack item = this.inventory.getItem( slot );
        if ( item == null || item.getType().equals( Material.AIR ) ) {
            return Optional.empty();
        }
        return Optional.of( item );
    }

    public abstract void onClick( InventoryClickEvent event );

    public abstract void onOpen( InventoryOpenEvent event );

    public abstract void onClose( InventoryCloseEvent event );

    @EventHandler
    public void onClickEvent( InventoryClickEvent event ) {
        if ( event.getInventory() == null ) return;
        if ( event.getClickedInventory() == null ) return;
        if ( event.getCurrentItem() == null || event.getCurrentItem().getType().equals( Material.AIR ) ) return;
        if ( !event.getInventory().equals( this.inventory ) ) return;
        if ( this.player != null && !this.player.getUniqueId().equals( event.getWhoClicked().getUniqueId() ) ) return;
        this.onClick( event );
    }

    @EventHandler
    public void onOpenEvent( InventoryOpenEvent event ) {
        if ( event.getInventory() == null ) return;
        if ( !event.getInventory().equals( this.inventory ) ) return;
        if ( this.player != null && !this.player.getUniqueId().equals( event.getPlayer().getUniqueId() ) ) return;
        this.onOpen( event );
    }

    @EventHandler
    public void onCloseEvent( InventoryCloseEvent event ) {
        if ( event.getInventory() == null ) return;
        if ( !event.getInventory().equals( this.inventory ) ) return;
        if ( this.player != null && !this.player.getUniqueId().equals( event.getPlayer().getUniqueId() ) ) return;
        this.onClose( event );
        if ( this.player != null ) {
            if ( this.ignoreClose ) {
                this.ignoreClose = false;
                return;
            }
            HandlerList.unregisterAll( this );
        }
    }
}