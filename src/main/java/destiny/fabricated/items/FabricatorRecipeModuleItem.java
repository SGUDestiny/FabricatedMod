package destiny.fabricated.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FabricatorRecipeModuleItem extends FabricatorModuleItem
{
    public static final String RECIPE_TYPES = "recipe_types";

    public FabricatorRecipeModuleItem(Properties pProperties)
    {
        super(pProperties);
    }

    public static ItemStack create(FabricatorRecipeModuleItem item, List<RecipeType<?>> recipes)
    {
        ItemStack stack = new ItemStack(item);
        List<ResourceKey<RecipeType<?>>> recipeTypes = new ArrayList<>();
        recipes.forEach(type -> recipeTypes.add(ResourceKey.create(ForgeRegistries.RECIPE_TYPES.getRegistryKey(), ForgeRegistries.RECIPE_TYPES.getKey(type))));
        item.setRecipeTypes(stack, recipeTypes);

        return stack;
    }

    public static ItemStack createDefault(FabricatorRecipeModuleItem item)
    {
        ItemStack stack = new ItemStack(item);
        List<ResourceKey<RecipeType<?>>> recipeTypes = new ArrayList<>();
        recipeTypes.add(ResourceKey.create(ForgeRegistries.RECIPE_TYPES.getRegistryKey(), ForgeRegistries.RECIPE_TYPES.getKey(RecipeType.CRAFTING)));
        recipeTypes.add(ResourceKey.create(ForgeRegistries.RECIPE_TYPES.getRegistryKey(), ForgeRegistries.RECIPE_TYPES.getKey(RecipeType.SMELTING)));


        item.setRecipeTypes(stack, recipeTypes);

        return stack;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
                                TooltipFlag pIsAdvanced)
    {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        this.getRecipeTypes(pStack).forEach(key ->
        {
            if(key.location() != null)
                pTooltipComponents.add(Component.literal(key.location().toString()).withStyle(ChatFormatting.DARK_PURPLE));
        });
    }

    public List<ResourceKey<RecipeType<?>>> getRecipeTypes(ItemStack stack)
    {
        List<ResourceKey<RecipeType<?>>> recipes = new ArrayList<>();
        if(stack.getTag() != null && stack.getTag().contains(RECIPE_TYPES))
        {
            ListTag listTag = stack.getTag().getList(RECIPE_TYPES, Tag.TAG_STRING);
            listTag.forEach(tag -> {
                String raw = tag.getAsString();
                ResourceKey<RecipeType<?>> resourceKey = ResourceKey.create(ForgeRegistries.RECIPE_TYPES.getRegistryKey(), ResourceLocation.tryParse(raw));
                recipes.add(resourceKey);
            });
        }

        return recipes;
    }

    public void setRecipeTypes(ItemStack stack, List<ResourceKey<RecipeType<?>>> recipes)
    {
        ListTag list = new ListTag();
        recipes.forEach(key -> list.add(StringTag.valueOf(key.location().toString())));

        stack.getOrCreateTag().put(RECIPE_TYPES, list);
    }
}
