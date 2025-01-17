package dev.thomasglasser.minejago.platform;

import dev.thomasglasser.minejago.Minejago;
import dev.thomasglasser.minejago.platform.services.*;

import java.util.ServiceLoader;

// Service loaders are a built-in Java feature that allow us to locate implementations of an interface that vary from one
// environment to another. In the context of MultiLoader we use this feature to access a mock API in the common code that
// is swapped out for the platform specific implementation at runtime.
public class Services {

    // In this example we provide a platform helper which provides information about what platform the mod is running on.
    // For example this can be used to check if the code is running on Forge vs Fabric, or to ask the modloader if another
    // mod is loaded.
    public static final PlatformHelper PLATFORM = load(PlatformHelper.class);
    public static final ConfigHelper CONFIG = load(ConfigHelper.class);
    public static final ParticleHelper PARTICLE = load(ParticleHelper.class);
    public static final DataHelper DATA = load(DataHelper.class);
    public static final NetworkHelper NETWORK = load(NetworkHelper.class);
    public static final BlockEntityHelper BLOCK_ENTITY = load(BlockEntityHelper.class);
    public static final ItemHelper ITEM = load(ItemHelper.class);
    public static final PotionHelper POTION = load(PotionHelper.class);

    // This code is used to load a service for the current environment. Your implementation of the service must be defined
    // manually by including a text file in META-INF/services named with the fully qualified class name of the service.
    // Inside the file you should write the fully qualified class name of the implementation to load for the platform. For
    // example our file on Forge points to ForgePlatformHelper while Fabric points to FabricPlatformHelper.
    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Minejago.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}