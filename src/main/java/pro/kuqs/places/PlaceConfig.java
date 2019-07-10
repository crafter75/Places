package pro.kuqs.places;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PlaceConfig {

    private File placeFile;

    private FileConfiguration placeCfg;

    public PlaceConfig( File dataFolder ) throws IOException {
        if ( !dataFolder.exists() ) {
            dataFolder.mkdir();
        }
        this.placeFile = new File( dataFolder, "places.yml" );
        this.placeCfg = YamlConfiguration.loadConfiguration( this.placeFile );

        if ( !this.placeFile.exists() ) {
            this.placeFile.createNewFile();
        }

        if(this.placeCfg.get( "use_permissions" ) == null) {
            this.placeCfg.set( "use_permissions", true );
            this.placeCfg.save( this.placeFile );
        }
    }

    public boolean usePermissions() {
        return this.placeCfg.getBoolean( "use_permissions" );
    }

    public List<Place> getPlacesFromFile() {
        List<Place> places = new ArrayList<>();

        if ( this.placeCfg.get( "places" ) != null ) {
            for ( String key : Objects.requireNonNull( this.placeCfg.getConfigurationSection( "places" ) ).getKeys( false ) ) {
                Location location = new Location(
                        Bukkit.getWorld( Objects.requireNonNull( this.placeCfg.getString( "places." + key + ".world" ) ) ),
                        this.placeCfg.getDouble( "places." + key + ".x" ),
                        this.placeCfg.getDouble( "places." + key + ".y" ),
                        this.placeCfg.getDouble( "places." + key + ".z" ),
                        (float) this.placeCfg.getDouble( "places." + key + ".yaw" ),
                        (float) this.placeCfg.getDouble( "places." + key + ".pitch" )
                );

                places.add( new Place( key.replace( "_", " " ), location,
                        UUID.fromString( Objects.requireNonNull( this.placeCfg.getString( "places." + key + ".creator" ) ) ) ) );
            }
        }
        System.out.println( "[Places] Loaded " + places.size() + " places!" );
        return places;
    }

    public void savePlace( Place place ) throws IOException {
        String desc = place.getDescription().replace( " ", "_" );
        this.placeCfg.set( "places." + desc + ".creator", place.getCreatorUUID().toString() );
        this.placeCfg.set( "places." + desc + ".world", Objects.requireNonNull( place.getLocation().getWorld() ).getName() );
        this.placeCfg.set( "places." + desc + ".x", place.getLocation().getX() );
        this.placeCfg.set( "places." + desc + ".y", place.getLocation().getY() );
        this.placeCfg.set( "places." + desc + ".z", place.getLocation().getZ() );
        this.placeCfg.set( "places." + desc + ".yaw", place.getLocation().getYaw() );
        this.placeCfg.set( "places." + desc + ".pitch", place.getLocation().getPitch() );

        this.placeCfg.save( this.placeFile );
    }

    public void deletePlace( Place place ) throws IOException {
        String desc = place.getDescription().replace( " ", "_" );
        this.placeCfg.set( "places." + desc + ".creator", null );
        this.placeCfg.set( "places." + desc + ".world", null );
        this.placeCfg.set( "places." + desc + ".x", null );
        this.placeCfg.set( "places." + desc + ".y", null );
        this.placeCfg.set( "places." + desc + ".z", null );
        this.placeCfg.set( "places." + desc + ".yaw", null );
        this.placeCfg.set( "places." + desc + ".pitch", null );
        this.placeCfg.set( "places." + desc, null );

        this.placeCfg.save( this.placeFile );
    }
}
