package icu.x64.realbanhammer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.server.BannedPlayerEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.explosion.Explosion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;

public class RealBanhammer implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("banhammer");
    public static final Item BANHAMMER = new Item(new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC));
    @Override
    public void onInitialize() {
        Registry.register(Registry.ITEM, new Identifier("realbanhammer", "banhammer"), BANHAMMER);
        LOGGER.info("RealBanhammer plugin initialized!");

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            Item itemInHand = player.getMainHandStack().getItem();
            if (!world.isClient && itemInHand == BANHAMMER) {

                world.createExplosion(null, entity.getX(), entity.getY(), entity.getZ(), 0.0F, Explosion.DestructionType.NONE);

                if (entity instanceof PlayerEntity) {
                    PlayerEntity targetPlayer = (PlayerEntity) entity;
                    MinecraftServer server = world.getServer();

                    if (server != null && targetPlayer instanceof ServerPlayerEntity) {

                        targetPlayer.getInventory().clear();

                        Date expiryTime = null; // If you want a permanent ban. Otherwise, set a date.
                        String reason = "Hit by the Banhammer!";

                        BannedPlayerEntry banListEntry = new BannedPlayerEntry(targetPlayer.getGameProfile(), new Date(), "Banhammer", expiryTime, reason); // Changed to "Banhammer" for display name
                        server.getPlayerManager().getUserBanList().add(banListEntry);

                        LOGGER.info(player.getEntityName() + " banned " + targetPlayer.getEntityName() + " with a banhammer!");

                        ((ServerPlayerEntity) targetPlayer).networkHandler.disconnect(Text.of(reason));
                    }
                } else if (entity instanceof LivingEntity) {
                    entity.remove(Entity.RemovalReason.DISCARDED);
                }
            }

            return ActionResult.PASS;
        });
    }
}
