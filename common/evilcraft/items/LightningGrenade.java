package evilcraft.items;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import evilcraft.api.config.ExtendedConfig;
import evilcraft.entities.item.EntityLightningGrenade;

public class LightningGrenade extends AbstractGrenade {
    
    private static LightningGrenade _instance = null;
    
    public static void initInstance(ExtendedConfig eConfig) {
        if(_instance == null)
            _instance = new LightningGrenade(eConfig);
        else
            eConfig.showDoubleInitError();
    }
    
    public static LightningGrenade getInstance() {
        return _instance;
    }

    private LightningGrenade(ExtendedConfig eConfig) {
        super(eConfig);
    }

    @Override
    protected EntityThrowable getThrowableEntity(ItemStack itemStack, World world, EntityPlayer player) {
        return new EntityLightningGrenade(world, player);
    }
}
