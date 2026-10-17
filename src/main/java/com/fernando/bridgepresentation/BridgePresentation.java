package com.fernando.bridgepresentation;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(BridgePresentation.MODID)
public class BridgePresentation {
    public static final String MODID = "bridgepresentation";
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<Item> SLIDE_REMOTE = ITEMS.register("slide_remote",
            () -> new Item(new Item.Properties().stacksTo(1).fireResistant()));
    public static java.nio.file.Path configPath() {
        return FMLPaths.CONFIGDIR.get().resolve("bridgepresentation.json");
    }

    public BridgePresentation() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ITEMS.register(bus);
        bus.addListener(this::addCreative);
        MinecraftForge.EVENT_BUS.register(new CommonEvents());
        if (FMLEnvironment.dist == Dist.CLIENT) DistExecutorClient.register(bus);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            event.accept(SLIDE_REMOTE);
        }
    }

    public static class CommonEvents {
        @SubscribeEvent
        public void clientSetup(FMLClientSetupEvent event) {
            // Intentionally empty: client registrations happen on the mod event bus.
        }
    }

    private static class DistExecutorClient {
        static void register(IEventBus bus) {
            bus.addListener(ClientEvents::registerKeys);
            bus.addListener(ClientEvents::clientSetup);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ClientEvents {
        public static KeyMapping EDIT_KEY;
        @SubscribeEvent
        public static void registerKeys(RegisterKeyMappingsEvent event) {
            EDIT_KEY = new KeyMapping("key.bridgepresentation.editor",
                    InputConstants.Type.KEYSYM, InputConstants.KEY_O, "key.categories.bridgepresentation");
            event.register(EDIT_KEY);
        }
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            MinecraftForge.EVENT_BUS.register(new PresentationRenderer());
            MinecraftForge.EVENT_BUS.register(new EditorKeyHandler());
        }
    }
}
