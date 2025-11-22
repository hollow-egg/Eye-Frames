package eggs.eyeframes;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EyeFrames implements ModInitializer {
	public static final String MOD_ID = "eyeframes";
	public static final Logger EYEFRAMES_LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
        EYEFRAMES_LOGGER.info("Initializing Eye Frames");
	}
}