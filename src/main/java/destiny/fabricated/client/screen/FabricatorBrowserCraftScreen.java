package destiny.fabricated.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import destiny.fabricated.FabricatedMod;
import destiny.fabricated.init.SoundInit;
import destiny.fabricated.items.FabricatorRecipeModuleItem;
import destiny.fabricated.menu.FabricatorBrowserCraftingMenu;
import destiny.fabricated.util.RenderBlitUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public class FabricatorBrowserCraftScreen extends AbstractContainerScreen<FabricatorBrowserCraftingMenu>
{
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FabricatedMod.MODID, "textures/gui/fabricator_icon_bg.png");

    public static final ResourceLocation ARROW_LEFT_TEXTURE = ResourceLocation.fromNamespaceAndPath(FabricatedMod.MODID, "textures/gui/fabricator_arrow_left.png");
    public static final ResourceLocation ARROW_RIGHT_TEXTURE = ResourceLocation.fromNamespaceAndPath(FabricatedMod.MODID, "textures/gui/fabricator_arrow_right.png");

    public List<Recipe<Container>> recipes;
    public RecipeType<?> selectedTypeKey;
    public boolean hasSelected;
    public int page;
    public int selectedType;

    public FabricatorBrowserCraftScreen(FabricatorBrowserCraftingMenu pMenu, Inventory pPlayerInventory, Component pTitle)
    {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 256;
        this.imageHeight = 256;

        this.recipes = new ArrayList<>();
        this.hasSelected = false;
        this.selectedType = -1;
        this.page = 0;
    }

    @Override
    protected void init()
    {
        super.init();
        int baseX = (width - imageWidth) / 2;
        int baseY = (height - imageHeight) / 2 - this.menu.recipeTypes.size()*11+11;

        int i = 0;
        for(FabricatorRecipeModuleItem.RecipeData data : this.menu.recipeTypes)
        {
            i++;
            int x = baseX+118;
            int y = baseY+(i*22)+120;

            this.addWidget(this.createButton(i, x, y, 18, 18, data));
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY)
    {
        if(menu.blockEntity.state == 3)
            return;

        PoseStack pose = graphics.pose();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int baseX = (width - imageWidth) / 2;
        int baseY = (height - imageHeight) / 2 - this.menu.recipeTypes.size()*11+11;

        int o = 0;
        for(FabricatorRecipeModuleItem.RecipeData data : this.menu.recipeTypes)
        {
            o++;
            int x = baseX+118;
            int y = baseY+(o*22)+120;

            pose.pushPose();

            if(pMouseX > x && pMouseX < x+18)
                if(pMouseY > y && pMouseY < y+18)
                    graphics.renderComponentTooltip(Minecraft.getInstance().font, List.of(data.nameComponent), pMouseX, pMouseY);

            pose.translate(x, y, 0);

            pose.pushPose();
            pose.scale(0.07f, 0.07f, 0.07f);
            RenderBlitUtil.blit(TEXTURE, pose, 0, 0, 0, 0, 256, 256);
            pose.popPose();

            ItemStack stack = data.getItem().getDefaultInstance();

            graphics.renderItem(stack, 1, 1);
            pose.popPose();
        }
    }

    public AbstractButton createButton(int number, int x, int y, int width, int height, FabricatorRecipeModuleItem.RecipeData data)
    {
        return new AbstractButton(x, y, width, height, Component.empty())
        {
            public final int id = number;

            @Override
            protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
            {
                defaultButtonNarrationText(pNarrationElementOutput);
            }

            @Override
            public void onPress()
            {
                recipeStuff(ForgeRegistries.RECIPE_TYPES.getValue(data.getKey().location()));
                selectedTypeKey = ForgeRegistries.RECIPE_TYPES.getValue(data.getKey().location());
                selectedType = id;
                hasSelected = true;
                page = 0;
                rebuildWidgets();
            }

            @Override
            public void playDownSound(SoundManager pHandler)
            {
                pHandler.play(SimpleSoundInstance.forUI(SoundInit.FABRICATOR_BUTTON.get(), 1.0F));
            }
        };
    }

    public AbstractButton createScrollButton(int number, int x, int y, int width, int height)
    {
        return new AbstractButton(x, y, width, height, Component.empty())
        {
            public final int id = number;

            @Override
            protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
            {
                defaultButtonNarrationText(pNarrationElementOutput);
            }

            @Override
            public void onPress()
            {
                if(!hasSelected)
                    return;
                if(recipes.isEmpty())
                    return;

                if(id == -1 && page < 1)
                    return;
                if(id == -2 && page < 2)
                    return;
                if(id == -3 && page < 3)
                    return;

                page = page + id;
                if(page >= recipes.size())
                    page = recipes.size()-1;
                if(page < 0)
                    page = 0;
            }

            @Override
            public void playDownSound(SoundManager pHandler)
            {
                pHandler.play(SimpleSoundInstance.forUI(SoundInit.FABRICATOR_BUTTON.get(), 1.0F));
            }
        };
    }

    public <T extends Recipe<?>> void recipeStuff(RecipeType<T> recipeType)
    {
        RecipeManager recipeManager = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        List<Recipe<Container>> recipes = recipeManager.getAllRecipesFor(((RecipeType<Recipe<Container>>) recipeType));

        this.recipes = recipes.stream().sorted(Comparator.comparing(recipe -> recipe.getResultItem(Minecraft.getInstance().level.registryAccess()).getDisplayName().getString())).toList();
        this.recipes = this.recipes.stream().filter(entry -> !entry.getResultItem(Minecraft.getInstance().level.registryAccess()).isEmpty()).toList();
        this.recipes = this.recipes.stream().filter(entry -> !(entry.isSpecial())).toList();

        this.recipes = filterCraftableRecipes(this.recipes, minecraft.player.getInventory(), menu.blockEntity.batchValue);
    }

    public static List<Recipe<Container>> filterCraftableRecipes(List<Recipe<Container>> recipes, Inventory playerInventory, int batchValue) {
        List<ItemStack> inventoryStacks = new ArrayList<>();

        for (int i = 0; i < playerInventory.getContainerSize(); i++) {
            ItemStack stack = playerInventory.getItem(i);
            if (!stack.isEmpty()) {
                inventoryStacks.add(stack.copy());
            }
        }

        List<Recipe<Container>> result = new ArrayList<>();
        for (Recipe<Container> recipe : recipes) {

            List<Ingredient> ingredients = recipe.getIngredients();

            Map<Ingredient, Integer> ingredientCounts = new HashMap<>();
            for (Ingredient ingredient : ingredients)
                if (!ingredient.isEmpty()) {
                    ingredientCounts.merge(ingredient, batchValue, Integer::sum);
                }

            List<ItemStack> available = inventoryStacks.stream().map(ItemStack::copy).collect(Collectors.toList());

            boolean canCraft = true;
            for (Map.Entry<Ingredient, Integer> entry : ingredientCounts.entrySet())
            {
                Ingredient ingredient = entry.getKey();
                int neededCount = entry.getValue();

                int availableCount = 0;

                for (ItemStack stack : available)
                    if (ingredient.test(stack))
                    {
                        int use = Math.min(neededCount - availableCount, stack.getCount());
                        availableCount += use;
                        if (availableCount >= neededCount) break;
                    }

                if (availableCount < neededCount)
                {
                    canCraft = false;
                    break;
                }
            }

            if (canCraft) {
                result.add(recipe);
            }
        }

        return result;
    }
}
