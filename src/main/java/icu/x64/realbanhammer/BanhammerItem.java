package icu.x64.realbanhammer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BanhammerItem extends SwordItem {
    public BanhammerItem() {
        super(ToolMaterials.NETHERITE, 0,-0.35f, new FabricItemSettings().maxCount(1));
    }
    @Override
    public boolean isDamageable() {
        return false;
    }
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("desc.realbanhammer.banhammer"));
    }
}
