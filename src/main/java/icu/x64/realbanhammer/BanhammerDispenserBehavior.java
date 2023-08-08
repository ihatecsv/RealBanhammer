package icu.x64.realbanhammer;

import net.minecraft.block.DispenserBlock;
import net.minecraft.block.dispenser.ItemDispenserBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.BlockPointer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class BanhammerDispenserBehavior extends ItemDispenserBehavior {
    @Override
    public ItemStack dispenseSilently(BlockPointer pointer, ItemStack stack) {
        World world = pointer.getWorld();
        if (!world.isClient) {
            Direction direction = pointer.getBlockState().get(DispenserBlock.FACING);
            BlockPos posInFront = pointer.getPos().offset(direction);
            List<Entity> entitiesInFront = world.getEntitiesByType(TypeFilter.instanceOf(Entity.class), new Box(posInFront), null);

            for (Entity entity : entitiesInFront) {
                if (entity instanceof LivingEntity) {
                    RealBanhammer.executeBanhammerEffect(world, entity, null);
                }
            }
        }
        return stack;
    }
}