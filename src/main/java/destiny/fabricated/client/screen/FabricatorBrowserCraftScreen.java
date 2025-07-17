package destiny.fabricated.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import destiny.fabricated.FabricatedMod;
import destiny.fabricated.init.NetworkInit;
import destiny.fabricated.init.SoundInit;
import destiny.fabricated.items.FabricatorRecipeModuleItem;
import destiny.fabricated.menu.FabricatorBrowserCraftingMenu;
import destiny.fabricated.network.packets.ServerboundBrowserMenuPacket;
import destiny.fabricated.tooltip.RecipeTooltipComponent;
import destiny.fabricated.util.RenderBlitUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
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

import static destiny.fabricated.client.screen.FabricatorCraftScreen.*;

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

    public int returnScroll;

    public FabricatorBrowserCraftScreen(FabricatorBrowserCraftingMenu pMenu, Inventory pPlayerInventory, Component pTitle)
    {
        super(pMenu, pPlayerInventory, pTitle);
        this.imageWidth = 256;
        this.imageHeight = 256;

        this.recipes = new ArrayList<>();
        this.selectedType = pMenu.type;
        this.returnScroll = pMenu.item;
        this.hasSelected = selectedType != -1;
        if(this.hasSelected)
        {
            this.selectedTypeKey = ForgeRegistries.RECIPE_TYPES.getValue(menu.blockEntity.getRecipeTypes().get(selectedType).key.location());
            selectedType += 1;
        }
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

        {
            int x = baseX+122;
            int y = baseY+(this.menu.recipeTypes.size()*22)+140;
            this.addWidget(this.createRecipeBrowserButton(x, y, 10, 10));
        }

        {
            int x = baseX+140;
            int y = baseY+(this.selectedType*22)+108;
            this.addWidget(this.createScrollButton(x, y, 10, 10, -1));

            x += 13;
            this.addWidget(this.createScrollButton(x, y, 10, 10, 1));
        }

        int o = 0;
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 6; x++)
            {
                o++;
                int xO = baseX + x * 22 + 140;
                int yO = baseY + (y * 22) + (selectedType * 22) + 120;

                this.addWidget(this.createRecipeButton(xO, yO, 18, 18, o));
            }
        }

    }

    @Override
    protected void renderBg(GuiGraphics graphics, float pPartialTick, int pMouseX, int pMouseY)
    {
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

        if(hasSelected)
        {
            rebuildWidgets();
            if(recipes.isEmpty())
            {
                if (menu.blockEntity.getLevel().getGameTime() % 20 == 0)
                    recipeStuff(selectedTypeKey);
                return;
            }

            {
                int x = baseX+122;
                int y = baseY+(this.menu.recipeTypes.size()*22)+140;

                pose.pushPose();
                pose.translate(x, y, 0);
                RenderBlitUtil.blit(RECIPE_BROWSER_BUTTON_TEXTURE, pose, 0, 0, 0, 0, 10, 10, 10, 10);
                pose.popPose();
            }

            pose.pushPose();
            pose.translate(baseX+140, baseY+(selectedType*22)+108, 0);
            RenderBlitUtil.blit(ARROW_LEFT_TEXTURE, pose, 0, 0, 0, 0, 10, 10, 10, 10);
            pose.translate(13, 0, 0);
            RenderBlitUtil.blit(ARROW_RIGHT_TEXTURE, pose, 0, 0, 0, 0, 10, 10, 10, 10);
            pose.translate(13, 1, 0);
            font.drawInBatch(String.valueOf(page+1), 0, 0, 0x5CB8FF, true, graphics.pose().last().pose(), graphics.bufferSource(), Font.DisplayMode.NORMAL, 0, LightTexture.FULL_BRIGHT);
            pose.popPose();

            int recipeI = 24*page;
            for (int y = 0; y < 4; y++)
                for (int x = 0; x < 6; x++)
                {
                    try
                    {
                        Recipe<Container> recipe = recipes.get(recipeI);
                        recipeI++;
                        int xO = baseX + x * 22 + 140;
                        int yO = baseY + (y * 22) + (selectedType * 22) + 120;

                        pose.pushPose();
                        ItemStack stack = recipe.getResultItem(Minecraft.getInstance().level.registryAccess());

                        pose.pushPose();
                        if (pMouseX > xO && pMouseX < xO + 18) if (pMouseY > yO && pMouseY < yO + 18)
                            graphics.renderTooltip(Minecraft.getInstance().font, List.of(stack.getHoverName()), Optional.of(new RecipeTooltipComponent(recipe)), pMouseX, pMouseY);

                        pose.translate(xO, yO, 0);
                        pose.pushPose();
                        pose.scale(0.07f, 0.07f, 0.07f);
                        RenderBlitUtil.blit(TEXTURE, pose, 0, 0, 0, 0, 256, 256);
                        pose.popPose();
                        graphics.renderItem(stack, 1, 1);
                        pose.popPose();
                    } catch (IndexOutOfBoundsException e)
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

    public AbstractButton createRecipeBrowserButton(int x, int y, int width, int height)
    {
        return new AbstractButton(x, y, width, height, Component.empty()) {
            @Override
            public void onPress()
            {
                menu.switching = true;
                ServerboundBrowserMenuPacket packet = new ServerboundBrowserMenuPacket(false, returnScroll+1, selectedType-1, menu.blockEntity.getBlockPos());
                NetworkInit.sendToServer(packet);
            }

            @Override
            protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
            {
                defaultButtonNarrationText(pNarrationElementOutput);
            }
        };
    }

    public AbstractButton createScrollButton(int x, int y, int width, int height, int increment)
    {
        return new AbstractButton(x, y, width, height, Component.empty())
        {
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

                page = page + increment;
                if(page < 0)
                    page = 0;
                if(page > getPageCount())
                    page = getPageCount();
            }

            @Override
            public void playDownSound(SoundManager pHandler)
            {
                pHandler.play(SimpleSoundInstance.forUI(SoundInit.FABRICATOR_BUTTON.get(), 1.0F));
            }
        };
    }

    public AbstractButton createRecipeButton(int x, int y, int width, int height, int id)
    {
        return new AbstractButton(x, y, width, height, Component.empty()) {
            public final int number = id;
            @Override
            public void onPress()
            {
                menu.switching = true;
                NetworkInit.sendToServer(new ServerboundBrowserMenuPacket(false, number+(page*24), selectedType-1, menu.blockEntity.getBlockPos()));
            }

            @Override
            protected void updateWidgetNarration(NarrationElementOutput pNarrationElementOutput)
            {
                defaultButtonNarrationText(pNarrationElementOutput);
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

        this.recipes = recipes.stream().filter(recipe -> hasRequiredItems(minecraft.player.getInventory(), getItems(recipe), 1)).toList();

        this.recipes = this.recipes.stream().filter(entry -> !entry.getResultItem(Minecraft.getInstance().level.registryAccess()).isEmpty()).toList();
        this.recipes = this.recipes.stream().filter(entry -> !(entry.isSpecial())).toList();

        this.recipes = this.recipes.stream().sorted(Comparator.comparing(recipe -> recipe.getResultItem(Minecraft.getInstance().level.registryAccess()).getDisplayName().getString())).toList();
    }

    public int getPageCount()
    {
        return this.recipes.size()/24;
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

            Map<List<ItemStack>, Integer> ingredientCounts = new HashMap<>();
            for (Ingredient ingredient : ingredients)
                if (!ingredient.isEmpty()) {
                    ingredientCounts.merge(List.of(ingredient.getItems()), 1, Integer::sum);
                }

            List<ItemStack> available = inventoryStacks.stream().map(ItemStack::copy).collect(Collectors.toList());

            boolean canCraft = true;
            for (Map.Entry<List<ItemStack>, Integer> entry : ingredientCounts.entrySet())
            {
                List<ItemStack> ingredient = entry.getKey();
                int neededCount = entry.getValue();

                int availableCount = 0;

                for (ItemStack stack : available)
                    if (ingredient.stream().anyMatch(ing -> stack.is(ing.getItem())))
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
