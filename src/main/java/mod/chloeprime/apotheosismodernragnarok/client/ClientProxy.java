package mod.chloeprime.apotheosismodernragnarok.client;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.loading.FMLLoader;

import java.util.Objects;

public class ClientProxy {
    public static final boolean DEDICATED_SERVER = FMLLoader.getDist().isDedicatedServer();

    public static void runDeferred(Level level, Runnable code) {
        if (DEDICATED_SERVER || !level.isClientSide) {
            Objects.requireNonNull(level.getServer()).execute(code);
        } else {
            ClientProxyImpl.runDeferred(code);
        }
    }

    public static Iterable<Entity> getEntities(Level level) {
        return level.isClientSide
                ? ClientProxyImpl.getEntitiesFromClientLevel(level)
                : ((ServerLevel) level).getEntities().getAll();
    }

    public static boolean hasLang(String langKey) {
        return DEDICATED_SERVER || ClientProxyImpl.hasLang(langKey);
    }
}
