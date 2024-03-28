package cn.ussshenzhou.madparticle.mixin;

import cn.ussshenzhou.madparticle.particle.CustomParticleRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.resources.FallbackResourceManager;
import net.minecraft.server.packs.resources.MultiPackResourceManager;
import net.minecraft.server.packs.resources.Resource;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author USS_Shenzhou
 */
@Mixin(MultiPackResourceManager.class)
public class MultiPackResourceManagerMixin {

    @Shadow
    @Final
    private Map<String, FallbackResourceManager> namespacedManagers;

    @Inject(method = "listResources", at = @At("RETURN"))
    private void madParticleFakeResourceJson(String pPath, Predicate<ResourceLocation> pFilter, CallbackInfoReturnable<Map<ResourceLocation, Resource>> cir) {
        if (!"particles".equals(pPath)) {
            return;
        }
        Map<ResourceLocation, Resource> map = cir.getReturnValue();
        PackResources resources = namespacedManagers.values().stream().toList().get(0).fallbacks.get(0).resources();
        CustomParticleRegistry.CUSTOM_PARTICLE_TYPES.forEach(particleType -> {
            ResourceLocation original = BuiltInRegistries.PARTICLE_TYPE.getKey(particleType);
            String s = String.format(
                    """
                            {
                              "textures": [
                              %s
                              ]
                            }
                            """,
                    CustomParticleRegistry.listToJsonString(CustomParticleRegistry.CUSTOM_PARTICLES_TYPE_NAMES_AND_TEXTURES.get(original))
            );
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(s.getBytes());
            ResourceLocation jsonLocation = new ResourceLocation(original.getNamespace(), "particles/" + original.getPath() + ".json");
            map.put(jsonLocation, new Resource(resources, () -> byteArrayInputStream));
        });
    }

    @Inject(method = "listResources", at = @At("RETURN"))
    private void madParticleCustomTexture(String pPath, Predicate<ResourceLocation> pFilter, CallbackInfoReturnable<Map<ResourceLocation, Resource>> cir) {
        if (!"textures/particle".equals(pPath)) {
            return;
        }
        Map<ResourceLocation, Resource> map = cir.getReturnValue();
        PackResources resources = namespacedManagers.values().stream().toList().get(0).fallbacks.get(0).resources();
        CustomParticleRegistry.ALL_TEXTURES.forEach(textureResourceLocation -> {
            try {
                //noinspection resource
                FileInputStream fileInputStream = new FileInputStream(CustomParticleRegistry.textureResLocToFile(textureResourceLocation));
                ResourceLocation pngLocation = new ResourceLocation(textureResourceLocation.getNamespace(), "textures/particle/" + textureResourceLocation.getPath() + ".png");
                map.put(pngLocation, new Resource(resources, () -> fileInputStream));
            } catch (IOException ignored) {
                LogUtils.getLogger().error("Failed to find file of texture {}. Check if it is a valid name.", textureResourceLocation);
            }
        });
    }
}
