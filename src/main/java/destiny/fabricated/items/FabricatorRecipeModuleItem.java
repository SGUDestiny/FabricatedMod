package destiny.fabricated.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
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
import net.minecraft.world.level.block.Blocks;
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

    public static ItemStack create(FabricatorRecipeModuleItem item, List<RecipeData> recipes)
    {
        ItemStack stack = new ItemStack(item);
        List<RecipeData> recipeDatas = new ArrayList<>(recipes);
        item.setRecipeTypes(stack, recipeDatas);

        return stack;
    }

    public static ItemStack createCrafting(FabricatorRecipeModuleItem item)
    {
        ItemStack stack = new ItemStack(item);
        List<RecipeData> recipeTypes = new ArrayList<>();
        recipeTypes.add(new RecipeData(Blocks.CRAFTING_TABLE.asItem(), Component.literal("Crafting"), ResourceKey.create(ForgeRegistries.RECIPE_TYPES.getRegistryKey(), ForgeRegistries.RECIPE_TYPES.getKey(RecipeType.CRAFTING))));

        item.setRecipeTypes(stack, recipeTypes);

        return stack;
    }

    public static ItemStack createSmelting(FabricatorRecipeModuleItem item)
    {
        ItemStack stack = new ItemStack(item);
        List<RecipeData> recipeTypes = new ArrayList<>();
        recipeTypes.add(new RecipeData(Blocks.FURNACE.asItem(), Component.literal("Smelting"), ResourceKey.create(ForgeRegistries.RECIPE_TYPES.getRegistryKey(), ForgeRegistries.RECIPE_TYPES.getKey(RecipeType.SMELTING))));
        recipeTypes.add(new RecipeData(Blocks.BLAST_FURNACE.asItem(), Component.literal("Blasting"), ResourceKey.create(ForgeRegistries.RECIPE_TYPES.getRegistryKey(), ForgeRegistries.RECIPE_TYPES.getKey(RecipeType.BLASTING))));
        recipeTypes.add(new RecipeData(Blocks.SMOKER.asItem(), Component.literal("Smoking"), ResourceKey.create(ForgeRegistries.RECIPE_TYPES.getRegistryKey(), ForgeRegistries.RECIPE_TYPES.getKey(RecipeType.SMOKING))));

        item.setRecipeTypes(stack, recipeTypes);

        return stack;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents,
                                TooltipFlag pIsAdvanced)
    {
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
        this.getRecipeTypes(pStack).forEach(data ->
        {
            if(data != null)
                pTooltipComponents.add(data.nameComponent.copy().withStyle(ChatFormatting.DARK_PURPLE));
        });
    }

    public List<RecipeData> getRecipeTypes(ItemStack stack)
    {
        List<RecipeData> recipes = new ArrayList<>();
        if(stack.getTag() != null && stack.getTag().contains(RECIPE_TYPES))
        {
            ListTag listTag = stack.getTag().getList(RECIPE_TYPES, Tag.TAG_COMPOUND);
            listTag.forEach(tag -> {
                RecipeData data = new RecipeData();
                data.deserialize(((CompoundTag) tag));
                recipes.add(data);
            });
        }

        return recipes;
    }

    public void setRecipeTypes(ItemStack stack, List<RecipeData> recipes)
    {
        ListTag list = new ListTag();
        recipes.forEach(data -> list.add(data.serialize()));

        stack.getOrCreateTag().put(RECIPE_TYPES, list);
    }

    public static class RecipeData
    {
        public static final String RECIPE_KEY = "recipe_key";
        public static final String NAME = "name";
        public static final String ITEM = "item";

        public ResourceKey<RecipeType<?>> key;
        public Component nameComponent;
        public Item item;

        public RecipeData()
        {

        }

        public RecipeData(Item item, Component name, ResourceKey<RecipeType<?>> key)
        {
            this.item = item;
            this.nameComponent = name;
            this.key = key;
        }

        public Item getItem()
        {
            return item;
        }

        public Component getName()
        {
            return nameComponent;
        }

        public ResourceKey<RecipeType<?>> getKey()
        {
            return key;
        }

        public CompoundTag serialize()
        {
            CompoundTag tag = new CompoundTag();

            tag.putString(ITEM, ForgeRegistries.ITEMS.getKey(this.item).toString());
            tag.putString(NAME, Component.Serializer.toJson(this.nameComponent));
            tag.putString(RECIPE_KEY, this.key.location().toString());

            return tag;
        }

        public void deserialize(CompoundTag tag)
        {
            this.item = ForgeRegistries.ITEMS.getValue(ResourceLocation.tryParse(tag.getString(ITEM)));
            this.nameComponent = Component.Serializer.fromJson(tag.getString(NAME));
            this.key = ResourceKey.create(ForgeRegistries.RECIPE_TYPES.getRegistryKey(), ResourceLocation.tryParse(tag.getString(RECIPE_KEY)));
        }
    }
}
