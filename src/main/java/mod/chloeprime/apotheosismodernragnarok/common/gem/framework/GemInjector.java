package mod.chloeprime.apotheosismodernragnarok.common.gem.framework;

import com.google.common.collect.ImmutableList;
import dev.shadowsoffire.apotheosis.loot.LootCategory;
import dev.shadowsoffire.apotheosis.socket.gem.Gem;
import dev.shadowsoffire.apotheosis.socket.gem.GemRegistry;
import dev.shadowsoffire.apotheosis.socket.gem.bonus.GemBonus;
import mod.chloeprime.apotheosismodernragnarok.common.internal.EnhancedGem;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GemInjector {

    public static final String DUMMY = null;

    public static void doInjections() {
        for (GemInjection injection : GemInjectionRegistry.INSTANCE.getValues()) {
            var gem = GemRegistry.INSTANCE.getValue(injection.getInjectionTarget());
            if (gem != null) {
                injectGem(injection, gem);
            }
        }
    }

    private static void injectGem(@Nonnull GemInjection injection, @Nonnull Gem gem) {
        var accessor = (EnhancedGem) gem;
        accessor.amr$reset();

        var newBonuses = Stream.concat(gem.getBonuses().stream(), injection.getBonuses().stream())
                .collect(ImmutableList.toImmutableList());
        var newBonusMap = newBonuses.stream()
                .<Pair<LootCategory, GemBonus>>mapMulti((gemData, mapper) -> gemData.getGemClass().types().forEach(c -> mapper.accept(Pair.of(c, gemData))))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
//        var uuidsNeeded = newBonuses.stream()
//                .mapToInt(GemBonus::getNumberOfUUIDs)
//                .max()
//                .orElse(0);

        accessor.setBonuses(newBonuses);
        accessor.setBonusMap(newBonusMap);
//        accessor.setUuidsNeeded(uuidsNeeded);
    }
}
