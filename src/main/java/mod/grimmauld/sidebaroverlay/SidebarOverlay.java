package mod.grimmauld.sidebaroverlay;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
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
		IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			modBus.addListener(Manager::init);
			modBus.addListener(Manager::onInterModProcess);
		});
	}
}
