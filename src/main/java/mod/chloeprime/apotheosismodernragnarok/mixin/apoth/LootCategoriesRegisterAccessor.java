package mod.chloeprime.apotheosismodernragnarok.mixin.apoth;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import dev.shadowsoffire.apotheosis.Apoth;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.placebo.registry.DeferredHelper;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.affix.category.ExtraLootCategories;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

public interface LootCategoriesRegisterAccessor {
    @Mixin(value = Apoth.LootCategories.class, remap = false)
    public interface LootCategoryAccessor {
        @Invoker("register")
        public static LootCategory register(String path, Predicate<ItemStack> filter, EquipmentSlotGroup slots) {
            throw new AssertionError();
        }
    }
    @Mixin(value = Apoth.LootCategories.class, remap = false)
    public static class LootCategoryMixin {
        @Inject(method = "<clinit>", at = @At("HEAD"))
        private static void injected(CallbackInfo ci) {
            ExtraLootCategories.init();
        }
    }
    @Mixin(value = DeferredHelper.class, remap = false)
    public static class DeferredHelperMixin {
        @ModifyExpressionValue(
                method = "register(Ljava/lang/String;Lnet/minecraft/resources/ResourceKey;Ljava/util/function/Supplier;)V",
                at = @At(value = "INVOKE", ordinal = 0, target = "Lnet/minecraft/resources/ResourceLocation;fromNamespaceAndPath(Ljava/lang/String;Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;")
        )
        private static ResourceLocation overrideRegister(ResourceLocation original, String path) {
            if (path.startsWith(ApotheosisModernRagnarok.MOD_ID + "_")) {
                return ResourceLocation.fromNamespaceAndPath(ApotheosisModernRagnarok.MOD_ID, path.substring(ApotheosisModernRagnarok.MOD_ID.length() + 1));
            } else {
                return original;
            }
        }
    }
}

