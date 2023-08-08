package icu.x64.realbanhammer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;

public class RealBanhammer implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("banhammer");
    public static final Item BLACK_BANHAMMER = new BanhammerItem();
    public static final Item RED_BANHAMMER = new BanhammerItem();

    public boolean isBanhammer(Item item) {
        return item == BLACK_BANHAMMER || item == RED_BANHAMMER;
    }
    public static void executeBanhammerEffect(World world, Entity entity, @Nullable PlayerEntity player) {
        if (!world.isClient) {
            world.createExplosion(null, entity.getX(), entity.getY(), entity.getZ(), 0.0F, Explosion.DestructionType.NONE);
            if (entity instanceof PlayerEntity targetPlayer) {
                MinecraftServer server = world.getServer();

                if (server != null && targetPlayer instanceof ServerPlayerEntity) {
                    targetPlayer.getInventory().clear();

                    Date expiryTime = null;
                    String reason = "Hit by the Banhammer!";

                    BannedPlayerEntry banListEntry = new BannedPlayerEntry(targetPlayer.getGameProfile(), new Date(), "Banhammer", expiryTime, reason);
                    server.getPlayerManager().getUserBanList().add(banListEntry);

                    if (player != null) {
                        LOGGER.info(player.getEntityName() + " banned " + targetPlayer.getEntityName() + " with a banhammer!");
                    } else {
                        LOGGER.info("A dispenser banned " + targetPlayer.getEntityName() + " with a banhammer!");
                    }

                    ((ServerPlayerEntity) targetPlayer).networkHandler.disconnect(Text.of(reason));
                }
            } else if (entity instanceof LivingEntity) {
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("realbanhammer", "black_banhammer"), BLACK_BANHAMMER);
        Registry.register(Registry.ITEM, new Identifier("realbanhammer", "red_banhammer"), RED_BANHAMMER);

        // DispenserBlock.registerBehavior(BLACK_BANHAMMER, new BanhammerDispenserBehavior());
        // DispenserBlock.registerBehavior(RED_BANHAMMER, new BanhammerDispenserBehavior());

        LOGGER.info("RealBanhammer mod initialized!");

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            ItemStack itemStackInHand = player.getMainHandStack();
            Item itemInHand = itemStackInHand.getItem();
            if (!world.isClient && isBanhammer(itemInHand)) {
                executeBanhammerEffect(world, entity, player);
                itemStackInHand.setDamage(0);
            }
            return ActionResult.PASS;
        });

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack itemStack = player.getStackInHand(hand);
            if (isBanhammer(itemStack.getItem())) {
                if (itemStack.getDamage() != 0) {
                    itemStack.setDamage(0);
                }

                player.swingHand(hand);

                if(!world.isClient) {
                    HitResult hitResult = player.raycast(5.0D, 0.0F, true);
                    if (hitResult.getType() == HitResult.Type.BLOCK) {
                        BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
                        world.createExplosion(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0.0F, Explosion.DestructionType.NONE);
                    }
                }
            }
            return TypedActionResult.pass(itemStack);
        });

        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) -> {
            ItemStack itemStack = player.getMainHandStack();
            if (isBanhammer(itemStack.getItem())) {
                if (itemStack.getDamage() != 0) {
                    itemStack.setDamage(0);
                }
                return false;
            }
            return true;
        });
    }
}
