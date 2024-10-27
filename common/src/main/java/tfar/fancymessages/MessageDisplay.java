package tfar.fancymessages;

import net.minecraft.network.chat.Component;

import java.util.List;

public record MessageDisplay(Component subtitle, List<Component> messages) {
}
