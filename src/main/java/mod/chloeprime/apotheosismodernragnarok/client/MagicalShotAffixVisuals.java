package mod.chloeprime.apotheosismodernragnarok.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tacz.guns.client.resource.pojo.display.ammo.AmmoParticle;
import com.tacz.guns.client.resource.serialize.Vector3fSerializer;
import mod.chloeprime.apotheosismodernragnarok.ApotheosisModernRagnarok;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import org.joml.Vector3f;

public class MagicalShotAffixVisuals {
    public static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Vector3f.class, new Vector3fSerializer())
            .create();
    public static final ResourceLocation BLUE_MUZZLE_FLASH = ApotheosisModernRagnarok.loc("textures/blue_muzzle_flash.png");
    public static final AmmoParticle MAGIC_PARTICLE = GSON.fromJson("""
            {
              "name": "electric_spark",
              "delta": [
                0,
                0,
                0
              ],
              "speed": 0.1,
              "life_time": 20,
              "count": 30
            }""", AmmoParticle.class);

    static {
        MAGIC_PARTICLE.setParticleOptions(ParticleTypes.ELECTRIC_SPARK);
    }

    public static boolean isMagicGunState;
}
