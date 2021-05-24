package mod.grimmauld.sidebaroverlay;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SidebarOverlay.MODID)
public class SidebarOverlay {
	public static final String MODID = "sidebaroverlay";
	public static final String NAME = "Sidebar Overlay";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public SidebarOverlay() {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> FMLJavaModLoadingContext.get().getModEventBus().addListener(Manager::init));
	}
}
