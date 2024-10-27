package tfar.fancymessages;

import com.google.gson.JsonObject;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;


public class FancyMessagesReloadListener extends SimplePreparableReloadListener<JsonObject> {

    private final RegistryAccess registryAccess;

    public FancyMessagesReloadListener(RegistryAccess registryAccess) {

        this.registryAccess = registryAccess;
    }

    @Override
    protected JsonObject prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return MessageHandler.loadFromFile();
    }

    @Override
    protected void apply(JsonObject pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        MessageHandler.load(pObject,registryAccess);
    }
}
