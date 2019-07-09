package pro.kuqs.places;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;

@Getter
public class Place {

    private String description;

    private Location location;

    private UUID creatorUUID;

    private String creatorName;

    public Place( String description, Location location, UUID creatorUUID ) {
        this.description = description;
        this.location = location;
        this.creatorUUID = creatorUUID;
        this.creatorName = Bukkit.getOfflinePlayer( this.creatorUUID ).getName();
    }
}
