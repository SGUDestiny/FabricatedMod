package destiny.fabricated.recipes;

import destiny.fabricated.recipes.containers.FabricatorContainer;
import destiny.fabricated.util.MathUtil;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.FireworkRocketRecipe;
import net.minecraft.world.item.crafting.FireworkStarRecipe;
import net.minecraft.world.item.crafting.ShulkerBoxColoring;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FireworkRocketFabricationType extends FabricationType<FireworkRocketRecipe>
{
    @Override
    public List<Fabrication> assembleFabrications(FabricatorContainer container, FireworkRocketRecipe recipe, RegistryAccess registryAccess)
    {
        List<Fabrication> fabrications = new ArrayList<>();

        List<ItemStack> paper = MathUtil.findStacks(Items.PAPER, container.getItems(), true);
        List<ItemStack> stars = MathUtil.findStacks(Items.FIREWORK_STAR, container.getItems(), true);
        int gunpowder = MathUtil.matchStacks(MathUtil.mergeItemStacks(container.getItems()), List.of(new ItemStack(Items.GUNPOWDER)));
        if (gunpowder != 0)
            gunpowder = Math.min(gunpowder, 3);

        if (!paper.isEmpty() && paper.get(0).getCount() > 0 && gunpowder > 0)
            for (int i = 1; i <= gunpowder; i++)
            {
                ItemStack firework = new ItemStack(Items.FIREWORK_ROCKET, 3);
                CompoundTag tag = firework.getOrCreateTagElement("Fireworks");

                tag.putByte("Flight", (byte) i);
                fabrications.add(new Fabrication(List.of(firework), List.of(new ItemStack(Items.PAPER), new ItemStack(Items.GUNPOWDER, i))));

                for (ItemStack star : stars)
                {
                    if (star.getTag() != null && star.getTag().contains("Explosion"))
                    {
                        ItemStack fireworkB = new ItemStack(Items.FIREWORK_ROCKET, 3);
                        CompoundTag tagB = firework.getOrCreateTagElement("Fireworks");

                        tagB.putByte("Flight", (byte) i);

                        ListTag listTag = new ListTag();
                        listTag.add(star.getTagElement("Explosion"));
                        tagB.put("Explosions", listTag);
                        fabrications.add(new Fabrication(List.of(fireworkB), List.of(new ItemStack(Items.PAPER), new ItemStack(Items.GUNPOWDER, i), star.copyWithCount(1))));
                    }
                }
            }

        fabrications.sort(Comparator.comparing(fabrication ->
        {
            if(fabrication.outputs.get(0).getTagElement("Fireworks") != null)
                return fabrication.outputs.get(0).getTagElement("Fireworks").getByte("Flight");
            return (byte) 0;
        }));
        return fabrications;
    }
}
