package tfar.fancymessages;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;
import tfar.fancymessages.platform.Services;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

public class MessageHandler {

    static final File file = new File("config/fancymessages.json");
    static final Gson gson = new Gson();

    public static final String ADV = "advancements";

    private static Map<ResourceLocation, MessageDisplay> advancementMessages = new HashMap<>();
    private static Map<ResourceLocation, Component> biomeMessages = new HashMap<>();
    private static Map<ResourceLocation, Component> questMessages = new HashMap<>();

    public static void createBlankFile() {
        createFile(createBlankJson());
    }

    public static void createFile(JsonObject object) {
        Gson gson = new Gson();
        JsonWriter writer = null;
        try {
            writer = gson.newJsonWriter(new FileWriter(file));
            writer.setIndent("    ");
            gson.toJson(object, writer);
        } catch (Exception e) {
            FancyMessages.LOG.error("Couldn't save config");
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    static JsonObject createBlankJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.add(ADV,new JsonObject());
        jsonObject.add("biomes",new JsonObject());
        jsonObject.add("quests",new JsonObject());
        return jsonObject;
    }

    public static JsonObject loadFromFile() {
        if (!file.exists()) {
            createBlankFile();
        }

        Reader reader = null;
        try {
            reader = new FileReader(file);
            JsonReader jsonReader = new JsonReader(reader);
          //  FancyMessages.LOG.info("Loading existing config");
            return gson.fromJson(jsonReader, JsonObject.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
        }
        return null;
    }

    public static void saveToFile(RegistryAccess access) {
        MinecraftServer server = Services.PLATFORM.getServer();
        JsonObject object = new JsonObject();
        JsonObject advObject = new JsonObject();
        for (Map.Entry<ResourceLocation, MessageDisplay> entry : advancementMessages.entrySet()) {
            JsonObject o = new JsonObject();
            o.addProperty("subtitle",Component.Serializer.toJson(entry.getValue().subtitle(),access));
            o.addProperty("message",Component.Serializer.toJson(entry.getValue().message(),access));

            advObject.add(entry.getKey().toString(),o);
        }
        object.add(ADV,advObject);
        createFile(object);
    }

    public static void load(JsonObject pObject, RegistryAccess registryAccess) {
        advancementMessages.clear();
        biomeMessages.clear();
        questMessages.clear();
        JsonObject advancements = GsonHelper.getAsJsonObject(pObject,ADV);
        for (Map.Entry<String,JsonElement> entry: advancements.asMap().entrySet()) {
            try {
                ResourceLocation id = ResourceLocation.parse(entry.getKey());
                JsonObject o = entry.getValue().getAsJsonObject();
                Component title = Component.Serializer.fromJson(o.get("subtitle"),registryAccess);
                Component message = Component.Serializer.fromJson(o.get("message"),registryAccess);

                advancementMessages.put(id,new MessageDisplay(title,message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<ResourceLocation, MessageDisplay> getAdvancementMessages() {
        return advancementMessages;
    }
}
