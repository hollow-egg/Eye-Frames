package eggs.eyeframes.tools;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import java.util.HashMap;

public class Input {
    private static final HashMap<String, KeyBinding> keybindings = new HashMap<>();

    public static void addKeybinding(String name, InputUtil.Type type, int key, String Category){
        keybindings.put(name,
                KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                "key.eyeframes." + name,
                type,
                key,
                Category)
        ));
    }

    //just check if it's at this tick
    public static boolean isKeybindingPressed(String name){
        return keybindings.get(name).isPressed();
    }

    //run call for every press of the keybind that tick
    public static void forEveryKeypress(String name, Runnable onPress){
        while (keybindings.get(name).wasPressed())
            onPress.run();
    }
}
