package eggs.eyeframes.tools;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.HashMap;

@Environment(EnvType.CLIENT)
public class Input {
    private static final HashMap<String, KeyMapping> keybindings = new HashMap<>();

    public static void addKeybinding(String name, InputConstants.Type type, int key, String Category){
        keybindings.put(name,
                KeyBindingHelper.registerKeyBinding(
                new KeyMapping(
                "key.eyeframes." + name,
                type,
                key,
                Category)
        ));
    }

    //just check if it's at this tick
    public static boolean isKeybindingPressed(String name){
        return keybindings.get(name).isDown();
    }

    //run call for every press of the keybind that tick
    public static void forEveryKeypress(String name, Runnable onPress){
        while (keybindings.get(name).consumeClick())
            onPress.run();
    }
}
