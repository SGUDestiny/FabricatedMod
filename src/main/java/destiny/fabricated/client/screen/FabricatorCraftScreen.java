package destiny.fabricated.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import destiny.fabricated.FabricatedMod;
import destiny.fabricated.init.SoundInit;
import destiny.fabricated.items.FabricatorRecipeModuleItem.RecipeData;
import destiny.fabricated.menu.FabricatorCraftingMenu;
import destiny.fabricated.tooltip.RecipeTooltipComponent;
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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public class FabricatorCraftScreen extends AbstractContainerScreen<FabricatorCraftingMenu>
{
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(FabricatedMod.MODID, "textures/gui/fabricator_icon_bg.png");
    public static final ResourceLocation ARROW_UP_TEXTURE = ResourceLocation.fromNamespaceAndPath(FabricatedMod.MODID, "textures/gui/fabricator_arrow_up.png");
    public static final ResourceLocation ARROW_DOWN_TEXTURE = ResourceLocation.fromNamespaceAndPath(FabricatedMod.MODID, "textures/gui/fabricator_arrow_down.png");
    public List<Recipe<Container>> recipes;
    public int selectedRecipe;
    public boolean hasSelected;
    public int scrollAmount;
    public int selectedType;

    public FabricatorCraftScreen(FabricatorCraftingMenu pMenu, Inventory pPlayerInventory, Component pTitle)
    {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 256;
        this.imageHeight = 256;

        this.recipes = new ArrayList<>();
        this.hasSelected = false;
        this.selectedType = -1;
        this.selectedRecipe = 0;
        this.scrollAmount = 0;
    }

    @Override
    protected void init()
    {
        super.init();
        int baseX = (width - imageWidth) / 2;
        int baseY = (height - imageHeight) / 2 - this.menu.recipeTypes.size()*11+11;

        for (int i = 0; i < this.menu.recipeTypes.size(); i++)
        {
            int x = baseX+118;
            int y = baseY+(i*22)+120;

            this.addWidget(this.createButton(i, x, y, 18, 18, this.menu.recipeTypes.get(i)));
        }

        {
            int y = baseY+(4*22)+(selectedType*22)+32;
            int x = baseX+140;
            this.addWidget(this.createCraftButton(4, x, y, 18, 18));
        }

        for(int i = 0; i<10; i++)
        {
            if(i == 4)
                continue;

            int y = baseY+(i*22)+(selectedType*22)+32;
            int x = baseX+140;
            this.addWidget(this.createScrollButton(i-4, x, y, 18, 18));
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

        for (int i = 0; i < this.menu.recipeTypes.size(); i++)
        {
            int x = baseX+118;
            int y = baseY+(i*22)+120;

            RecipeData data = this.menu.recipeTypes.get(i);
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

        if(hasSelected)
        {
            int baseI = 2;
            int iO = 3;
            int recipeI = -1;
            int amountToScroll = scrollAmount;
            if(scrollAmount == 1)
            {
                iO -= 1;
                amountToScroll -= 1;
            }
            if(scrollAmount == 2)
            {
                iO -= 2;
                amountToScroll -= 2;
            }
            if(scrollAmount == 3)
            {
                iO -= 3;
                amountToScroll -= 3;
            }
            if(scrollAmount >= 4)
            {
                iO -= 4;
                amountToScroll -= 4;
            }

            int recipeListSize = 9;
            for (int i = baseI; i < baseI+recipeListSize; i++)
            {

                recipeI++;
                iO++;

                float alpha = 1F;
                if(scrollAmount == 3)
                    recipeListSize = 8;
                if(scrollAmount == 2)
                    recipeListSize = 7;
                if(scrollAmount == 1)
                    recipeListSize = 6;
                if(scrollAmount == 0)
                    recipeListSize = 5;

                if(i == baseI && scrollAmount >= 3)
                {
                    alpha = 0.25f;
                }
                if(i == baseI && scrollAmount == 2)
                {
                    alpha = 0.5f;

                }
                if(i == baseI && scrollAmount == 1)
                {
                    alpha = 0.75f;
                }

                if(i == baseI+1 && scrollAmount >= 3)
                {
                    alpha = 0.5f;
                }
                if(i == baseI+1 && scrollAmount == 2)
                {
                    alpha = 0.75f;
                }

                if(i == baseI+2 && scrollAmount >= 3)
                    alpha = 0.75f;

                if(i == baseI+recipeListSize-1)
                    alpha = 0.25f;
                if(i == baseI+recipeListSize-2)
                    alpha = 0.5f;
                if(i == baseI+recipeListSize-3)
                    alpha = 0.75f;

                try
                {
                    Recipe<Container> recipe = recipes.get(recipeI+amountToScroll);
                    if(recipe != null)
                    {
                        int x = baseX+140;
                        int y = baseY+(iO*22)+(selectedType*22)+32;

                        pose.pushPose();
                        ItemStack stack = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());

                        pose.pushPose();
                        if(pMouseX > x && pMouseX < x+18)
                            if(pMouseY > y && pMouseY < y+18)
                                graphics.renderTooltip(Minecraft.getInstance().font, List.of(stack.getHoverName()), Optional.of(new RecipeTooltipComponent(recipe)) ,pMouseX, pMouseY);

                        pose.translate(x, y, 0);

                        pose.pushPose();

                        pose.scale(0.07f, 0.07f, 0.07f);
                        RenderSystem.enableBlend();
                        RenderSystem.setShaderColor(1f, 1f, 1f, alpha);
                        RenderBlitUtil.blit(TEXTURE, pose, 0, 0, 0, 0, 256, 256);

                        RenderSystem.disableBlend();
                        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
                        pose.popPose();

                        graphics.renderItem(stack, 1, 1);
                        pose.popPose();
                    }
                }
                catch (IndexOutOfBoundsException e)
                {
                    break;
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick)
    {
        //renderBackground(graphics);
        super.render(graphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(graphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY)
    {

    }

    public AbstractButton createButton(int number, int x, int y, int width, int height, RecipeData data)
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
                selectedType = id;
                hasSelected = true;
                scrollAmount = 0;
                rebuildWidgets();
            }

            @Override
            public void playDownSound(SoundManager pHandler)
            {
                pHandler.play(SimpleSoundInstance.forUI(SoundInit.FABRICATOR_BUTTON.get(), 1.0F));
            }
        };
    }

    public AbstractButton createCraftButton(int number, int x, int y, int width, int height)
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
                if(menu.blockEntity.state == 3)
                    return;

                ItemStack stackToCraft = recipes.get(scrollAmount).getResultItem(Minecraft.getInstance().level.registryAccess());
                menu.blockEntity.fabricate(menu.level, menu.blockEntity.getBlockPos(), menu.blockEntity, stackToCraft, getItems(recipes.get(scrollAmount)));
            }

            @Override
            public void playDownSound(SoundManager pHandler) {}
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

                if(id == -1 && scrollAmount < 1)
                    return;
                if(id == -2 && scrollAmount < 2)
                    return;
                if(id == -3 && scrollAmount < 3)
                    return;

                scrollAmount = scrollAmount + id;
                if(scrollAmount >= recipes.size())
                    scrollAmount = recipes.size()-1;
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

        this.recipes = filterCraftableRecipes(this.recipes, minecraft.player.getInventory());
    }

    public static List<Recipe<Container>> filterCraftableRecipes(List<Recipe<Container>> recipes, Inventory playerInventory) {
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
                    ingredientCounts.merge(ingredient, 1, Integer::sum);
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

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    public List<ItemStack> getItems(Recipe<Container> recipe)
    {
        List<ItemStack> items = new ArrayList<>();
        recipe.getIngredients().forEach(ingredient ->
        {
            ItemStack stack = ingredient.getItems().length == 0 ? ItemStack.EMPTY :  ingredient.getItems()[0];
            items.add(stack);
        });

        HashMap<Item, Integer> map = new HashMap<>();
        for(ItemStack stack : items)
        {
            if(stack.isEmpty())
                continue;

            boolean merged = false;
            for(Item key : map.keySet())
                if(stack.is(key))
                {
                    map.put(key, map.get(key) + stack.getCount());
                    merged = true;
                    break;
                }
            if(!merged)
                map.put(stack.getItem(), stack.getCount());
        }

        List<ItemStack> result = new ArrayList<>();
        map.forEach((item, count) -> result.add(new ItemStack(item, count)));

        return result;
    }


}
