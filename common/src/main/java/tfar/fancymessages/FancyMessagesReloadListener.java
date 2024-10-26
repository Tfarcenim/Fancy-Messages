package tfar.fancymessages;

import com.google.gson.JsonObject;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;


public class FancyMessagesReloadListener extends SimplePreparableReloadListener<JsonObject> {

    @Override
    protected JsonObject prepare(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        return MessageHandler.loadFromFile();
    }

    @Override
    protected void apply(JsonObject pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        MessageHandler.load(pObject);
    }
}
