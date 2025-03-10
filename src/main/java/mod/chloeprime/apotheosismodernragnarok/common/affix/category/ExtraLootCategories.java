package mod.chloeprime.apotheosismodernragnarok.common.affix.category;

import com.tacz.guns.api.item.gun.FireMode;
import com.tacz.guns.resource.index.CommonGunIndex;
import com.tacz.guns.resource.pojo.data.gun.Bolt;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import mod.chloeprime.apotheosismodernragnarok.common.CommonConfig;
import mod.chloeprime.apotheosismodernragnarok.common.util.mixin.LootCategoryRegister;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;

public class ExtraLootCategories {
    public static LootCategory SHOTGUN;
    public static LootCategory FULL_AUTO;
    public static LootCategory SEMI_AUTO;
    public static LootCategory BOLT_ACTION;
    public static LootCategory HEAVY_WEAPON;

    public static Set<LootCategory> all() {
        return Collections.unmodifiableSet(ALL_GUNS);
    }

    public static boolean isGun(LootCategory category) {
        return ALL_GUNS.contains(category);
    }

    public static void init() {
        BOLT_ACTION = register("bolt_action", GunPredicate.matchIndex(ExtraLootCategories::isBoltAction).and(ExtraLootCategories::isBoltActionShotgunBoltAction), EquipmentSlotGroup.MAINHAND, true);
        SHOTGUN     = register("shotgun",     GunPredicate.matchIndex(index -> index.getBulletData().getBulletAmount() > 4), EquipmentSlotGroup.MAINHAND, true);
        FULL_AUTO   = register("full_auto",   GunPredicate.supports(FireMode.AUTO), EquipmentSlotGroup.MAINHAND, true);
        SEMI_AUTO   = register("semi_auto",   GunPredicate.supports(FireMode.SEMI, FireMode.BURST), EquipmentSlotGroup.MAINHAND, true);
        HEAVY_WEAPON= register("heavy_weapon",new ShieldBreakerTest(), EquipmentSlotGroup.MAINHAND, false);
    }

    public static boolean isBoltAction(CommonGunIndex index) {
        if (index.getGunData().getBolt() == Bolt.MANUAL_ACTION) {
            return true;
        }
        // 让EMX莫尔斯类的单发装弹武器不被判断成全自动
        return ((index.getGunData().getBolt() == Bolt.OPEN_BOLT ? 0 : 1) + index.getGunData().getAmmoAmount()) == 1;
    }

    private static boolean isBoltActionShotgunBoltAction(ItemStack stack) {
        if (!SHOTGUN.isValid(stack)) {
            return true;
        }
        return CommonConfig.BOLT_ACTION_SHOTGUN_IS_BOLT_ACTION.get();
    }

    private static final Set<LootCategory> ALL_GUNS = new LinkedHashSet<>(8);

    private static LootCategory register(String path, Predicate<ItemStack> predicate, EquipmentSlotGroup slots, boolean isGun) {
//        var registered = Apoth.LootCategories.register(ApotheosisModernRagnarok.loc(path).toString(), predicate, slots); TODO test it
        var registered = LootCategoryRegister.reg(ApotheosisModernRagnarok.loc(path).toString(), predicate, slots);
        if (isGun) {
            ALL_GUNS.add(registered);
        }
        return registered;
    }
}
