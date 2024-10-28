package tfar.fancymessages;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageHandler {

    static final File file = new File("config/fancymessages.json");
    static final Gson gson = new Gson();

    public static final String ADV = "advancements";

    private static Map<ResourceLocation, MessageDisplay> advancementMessages = new HashMap<>();
    private static Map<ResourceLocation, MessageDisplay> biomeMessages = new HashMap<>();
    private static Map<String, MessageDisplay> questMessages = new HashMap<>();

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
        JsonObject object = new JsonObject();
        saveMessageGroup(object,ADV,access,advancementMessages);
        saveMessageGroup(object,"biomes",access,biomeMessages);
        saveMessageGroupGeneric(object,"quests",access,questMessages);
        createFile(object);
    }

    public static void saveMessageGroup(JsonObject object, String category, RegistryAccess access,Map<ResourceLocation,MessageDisplay> map) {
        JsonObject advObject = new JsonObject();
        for (Map.Entry<ResourceLocation, MessageDisplay> entry : map.entrySet()) {
            JsonObject o = new JsonObject();
            o.addProperty("subtitle",Component.Serializer.toJson(entry.getValue().subtitle(),access));

            JsonArray array = new JsonArray();

            for (Component component : entry.getValue().messages()) {
                array.add(Component.Serializer.toJson(component,access));
            }

            o.add("messages",array);

            advObject.add(entry.getKey().toString(),o);
        }
        object.add(category,advObject);
    }

    public static void saveMessageGroupGeneric(JsonObject object, String category, RegistryAccess access,Map<String,MessageDisplay> map) {
        JsonObject advObject = new JsonObject();
        for (Map.Entry<String, MessageDisplay> entry : map.entrySet()) {
            JsonObject o = new JsonObject();
            o.addProperty("subtitle",Component.Serializer.toJson(entry.getValue().subtitle(),access));

            JsonArray array = new JsonArray();

            for (Component component : entry.getValue().messages()) {
                array.add(Component.Serializer.toJson(component,access));
            }

            o.add("messages",array);

            advObject.add(entry.getKey(),o);
        }
        object.add(category,advObject);
    }

    public static void load(JsonObject pObject, RegistryAccess registryAccess) {
        advancementMessages.clear();
        biomeMessages.clear();
        questMessages.clear();

        parseMessageGroup(pObject,ADV,registryAccess,advancementMessages);
        parseMessageGroup(pObject,"biomes",registryAccess,biomeMessages);
        parseMessageGroupGeneric(pObject,"quests",registryAccess,questMessages);

    }

    static void parseMessageGroup(JsonObject object,String category,RegistryAccess access,Map<ResourceLocation,MessageDisplay> map) {
        JsonObject advancements = GsonHelper.getAsJsonObject(object,category);
        for (Map.Entry<String,JsonElement> entry: advancements.asMap().entrySet()) {
            try {
                ResourceLocation id = ResourceLocation.parse(entry.getKey());
                JsonObject o = entry.getValue().getAsJsonObject();
                Component title = Component.Serializer.fromJson(o.get("subtitle"),access);

                JsonArray messages = o.get("messages").getAsJsonArray();
                List<Component> lines = new ArrayList<>();
                for (JsonElement element : messages) {
                    Component message = Component.Serializer.fromJson(element,access);
                    lines.add(message);
                }

                map.put(id,new MessageDisplay(title,lines));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static void parseMessageGroupGeneric(JsonObject object,String category,RegistryAccess access,Map<String,MessageDisplay> map) {
        JsonObject advancements = GsonHelper.getAsJsonObject(object,category);
        for (Map.Entry<String,JsonElement> entry: advancements.asMap().entrySet()) {
            try {
                String id = entry.getKey();
                JsonObject o = entry.getValue().getAsJsonObject();
                Component title = Component.Serializer.fromJson(o.get("subtitle"),access);

                JsonArray messages = o.get("messages").getAsJsonArray();
                List<Component> lines = new ArrayList<>();
                for (JsonElement element : messages) {
                    Component message = Component.Serializer.fromJson(element,access);
                    lines.add(message);
                }

                map.put(id,new MessageDisplay(title,lines));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<ResourceLocation, MessageDisplay> getAdvancementMessages() {
        return advancementMessages;
    }

    public static Map<ResourceLocation, MessageDisplay> getBiomeMessages() {
        return biomeMessages;
    }

    public static Map<String, MessageDisplay> getQuestMessages() {
        return questMessages;
    }
}
