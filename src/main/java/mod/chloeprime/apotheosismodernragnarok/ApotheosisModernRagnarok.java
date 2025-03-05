package mod.chloeprime.apotheosismodernragnarok;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import mod.chloeprime.apotheosismodernragnarok.client.MagicalShotAffixVisuals;
import mod.chloeprime.apotheosismodernragnarok.common.ModContent;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.ArmorSquashAffix;
import mod.chloeprime.apotheosismodernragnarok.common.affix.content.BulletSaverAffix;
import mod.chloeprime.apotheosismodernragnarok.common.gem.framework.GemInjector;
import mod.chloeprime.apotheosismodernragnarok.common.util.debug.DamageAmountDebug;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLLoader;
import org.slf4j.Logger;

import javax.annotation.Nullable;

@Mod(ApotheosisModernRagnarok.MOD_ID)
public class ApotheosisModernRagnarok {

    public static final String MOD_ID = "apotheosis_modern_ragnarok";

    public static ResourceLocation loc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @Nullable
    private static final Logger LOGGER = LogUtils.getLogger();

    @SuppressWarnings("CallToPrintStackTrace")
    public static void logError(String message, Throwable throwable) {
        if (LOGGER != null) {
            LOGGER.error(message, throwable);
        } else {
            System.out.println("[ApotheosisModernRagnarok/Error] " + message);
            System.out.println("[ApotheosisModernRagnarok/Error] Stacktrace:");
            throwable.printStackTrace();
        }
    }

    public ApotheosisModernRagnarok(net.neoforged.fml.ModContainer modContainer, IEventBus bus) {
        ModContent.init0(bus);
        ModContent.init1(modContainer);
        bus.addListener(this::setup);
        // load class
        if (FMLLoader.getDist() == Dist.CLIENT) {
            ResourceLocation blueMuzzleFlash = MagicalShotAffixVisuals.BLUE_MUZZLE_FLASH;
            Codec<BulletSaverAffix> codec = BulletSaverAffix.CODEC;
            String dummy = GemInjector.DUMMY;
        } else {
            Codec<ArmorSquashAffix> codec = ArmorSquashAffix.CODEC;
        }
    }

    private void setup(FMLCommonSetupEvent e) {
        e.enqueueWork(ModContent::setup);
        e.enqueueWork(() -> {
            if (!FMLLoader.isProduction()) {
                NeoForge.EVENT_BUS.register(new DamageAmountDebug());
            }
        });
    }
}
