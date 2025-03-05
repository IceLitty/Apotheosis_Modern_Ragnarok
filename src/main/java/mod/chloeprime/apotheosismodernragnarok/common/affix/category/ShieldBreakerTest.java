package mod.chloeprime.apotheosismodernragnarok.common.affix.category;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Predicate;

import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/**
 * Source from {@link dev.shadowsoffire.apotheosis.adventure.loot.ShieldBreakerTest} at 1.20.1 ver
 */
@SuppressWarnings("JavadocReference")
@EventBusSubscriber(
        modid = ApotheosisModernRagnarok.MOD_ID,
        bus = Bus.GAME
)
class ShieldBreakerTest implements Predicate<ItemStack> {
    private static Map<Level, Zombies> zombieCache = new IdentityHashMap();

    ShieldBreakerTest() {
    }

    public boolean test(ItemStack t) {
        try {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            Level level = null;
            if (server != null) {
                level = server.getLevel(Level.OVERWORLD);
            } else if (FMLEnvironment.dist.isClient()) {
                level = ShieldBreakerTest.Client.getLevel();
            }

            if (level != null) {
                Zombies zombies = (Zombies)zombieCache.computeIfAbsent(level, Zombies::new);
                return t.canDisableShield(zombies.target.getOffhandItem(), zombies.target, zombies.attacker);
            } else {
                return t.canDisableShield(Items.SHIELD.getDefaultInstance(), (LivingEntity)null, (LivingEntity)null);
            }
        } catch (Exception ex) {
            ApotheosisModernRagnarok.logError("Failed to execute ShieldBreakerTest", ex);
            return false;
        }
    }

    @SubscribeEvent
    public static void unload(LevelEvent.Unload e) {
        zombieCache.remove(e.getLevel());
    }

    private static record Zombies(Zombie attacker, Zombie target) {
        public Zombies(Level level) {
            this(new Zombie(level), new Zombie(level));
            this.target.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.SHIELD));
        }

        private Zombies(Zombie attacker, Zombie target) {
            this.attacker = attacker;
            this.target = target;
        }

        public Zombie attacker() {
            return this.attacker;
        }

        public Zombie target() {
            return this.target;
        }
    }

    private static class Client {
        private Client() {
        }

        static Level getLevel() {
            return Minecraft.getInstance().level;
        }
    }
}
