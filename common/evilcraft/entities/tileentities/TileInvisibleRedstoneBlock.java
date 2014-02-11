package evilcraft.entities.tileentities;

import net.minecraft.world.World;
import evilcraft.api.entities.tileentitites.EvilCraftTileEntity;

public class TileInvisibleRedstoneBlock extends EvilCraftTileEntity {
    
    // Destroy redstone block after 1 redstone tick (= 2 game ticks)
    private static final int TICK_DESTRUCTION_THRESHOLD = 2;
    
    private final long tickCreated;
    
    public TileInvisibleRedstoneBlock(World world) {
        tickCreated = world.getTotalWorldTime();
    }
    
    @Override
    public void updateEntity() {
        if (worldObj.getTotalWorldTime() - tickCreated >= TICK_DESTRUCTION_THRESHOLD)
            worldObj.setBlockToAir(xCoord, yCoord, zCoord);
    }
}
