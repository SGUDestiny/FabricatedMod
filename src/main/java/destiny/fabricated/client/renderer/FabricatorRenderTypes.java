package destiny.fabricated.client.renderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.DefaultedVertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class FabricatorRenderTypes extends RenderType
{

    public FabricatorRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize,
                                 boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState,
                                 Runnable pClearState)
    {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }

    public static RenderType fabricatorGlow(ResourceLocation resourceLocation)
    {
        return create("fabricator_glow", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true,
                RenderType.CompositeState.builder()
                        .setShaderState(RenderStateShard.RENDERTYPE_ENTITY_ALPHA_SHADER)
                        .setTextureState(new TextureStateShard(resourceLocation, false, false))
                        .setTransparencyState(NO_TRANSPARENCY)
                        .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                        .setCullState(CULL)
                        .createCompositeState(true));
    }
}
