package mod.chloeprime.apotheosismodernragnarok.common;

import com.google.common.collect.Lists;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.minecraft.core.registries.BuiltInRegistries;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class CommonConfig {
    public static final ModConfigSpec.ConfigValue<List<String>> ARMOR_SQUASH_BLACKLIST;
    public static final ModConfigSpec.BooleanValue BOLT_ACTION_SHOTGUN_IS_BOLT_ACTION;
    public static final List<String> DEFAULT_ARMOR_SQUASH_BLACKLIST = Lists.newArrayList(
            "minecraft:player",
            "minecraft:armor_stand",
            "dummmmmmy:target_dummy"
    );

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent event) {
        if (!(event instanceof ModConfigEvent.Loading) && !(event instanceof ModConfigEvent.Reloading)) {
            return;
        }
        asb_dirty = true;
    }

    public static boolean isArmorSquashBlacklist(@Nonnull Entity entity) {
        if (asb_dirty) {
            synchronized (AS_BLACKLIST) {
                if (asb_dirty) {
                    AS_BLACKLIST.clear();
                    var reg = BuiltInRegistries.ENTITY_TYPE;
                    ARMOR_SQUASH_BLACKLIST.get().stream()
                            .map(ResourceLocation::parse)
                            .filter(reg::containsKey)
                            .map(reg::get)
                            .filter(Objects::nonNull)
                            .forEach(AS_BLACKLIST::add);
                }
                asb_dirty = false;
            }
        }
        return AS_BLACKLIST.contains(entity.getType());
    }

    static volatile boolean asb_dirty = true;
    static final Set<EntityType<?>> AS_BLACKLIST = Collections.newSetFromMap(new ConcurrentHashMap<>());
    static final ModConfigSpec SPEC;

    static {
        var builder = new ModConfigSpec.Builder();

        ARMOR_SQUASH_BLACKLIST = builder.
                comment("Entity types that armor squash will not take effect on")
                .define("armor_squash_blacklist", DEFAULT_ARMOR_SQUASH_BLACKLIST);

        BOLT_ACTION_SHOTGUN_IS_BOLT_ACTION = builder
                .comment("If true, bolt action shotgun is bolt action, otherwise is shotgun")
                .define("ba_shotgun_is_ba", false);

        SPEC = builder.build();
    }
}
