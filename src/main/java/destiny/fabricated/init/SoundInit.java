package destiny.fabricated.init;

import destiny.fabricated.FabricatedMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundInit {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, FabricatedMod.MODID);

    public static RegistryObject<SoundEvent> FABRICATOR_OPEN = registerSoundEvent("fabricator_open");
    public static RegistryObject<SoundEvent> FABRICATOR_FABRICATE = registerSoundEvent("fabricator_fabricate");
    public static RegistryObject<SoundEvent> FABRICATOR_CLOSE = registerSoundEvent("fabricator_close");

    private static RegistryObject<SoundEvent> registerSoundEvent(String sound)
    {
        return SOUNDS.register(sound, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FabricatedMod.MODID, sound)));
    }

    public static void register(IEventBus bus)
    {
        SOUNDS.register(bus);
    }
}
