package club.athlas.loademup.api.loader;

import club.athlas.loademup.api.object.SimulationResult;
import org.bukkit.plugin.Plugin;

import java.io.File;

public interface Simulator {

    SimulationResult reload(Plugin plugin);

    SimulationResult load(File file);

    SimulationResult unload(Plugin plugin);

}
