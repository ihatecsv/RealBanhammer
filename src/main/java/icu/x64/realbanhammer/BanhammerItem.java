package icu.x64.realbanhammer;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.util.Rarity;

public class BanhammerItem extends SwordItem {
    public BanhammerItem() {
        super(ToolMaterials.NETHERITE, 9000,-0.35f, new FabricItemSettings().maxCount(1).rarity(Rarity.EPIC));
    }

    @Override
    public boolean isDamageable() {
        return false;
    }
}
