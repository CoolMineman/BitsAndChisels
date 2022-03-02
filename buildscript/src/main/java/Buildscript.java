import io.github.coolcrabs.brachyura.dependency.JavaJarDependency;
import io.github.coolcrabs.brachyura.fabric.FabricLoader;
import io.github.coolcrabs.brachyura.fabric.FabricMaven;
import io.github.coolcrabs.brachyura.fabric.FabricProject;
import io.github.coolcrabs.brachyura.fabric.Yarn;
import io.github.coolcrabs.brachyura.maven.Maven;
import io.github.coolcrabs.brachyura.maven.MavenId;
import io.github.coolcrabs.brachyura.minecraft.Minecraft;
import io.github.coolcrabs.brachyura.minecraft.VersionMeta;
import net.fabricmc.mappingio.tree.MappingTree;

public class Buildscript extends FabricProject {

    @Override
    public VersionMeta createMcVersion() {
        return Minecraft.getVersion("1.18.2");
    }

    @Override
    public int getJavaVersion() {
        return 17;
    }

    @Override
    public MappingTree createMappings() {
        return Yarn.ofMaven(FabricMaven.URL, FabricMaven.yarn("1.18.2+build.1")).tree;
    }

    @Override
    public FabricLoader getLoader() {
        return new FabricLoader(FabricMaven.URL, FabricMaven.loader("0.13.3"));
    }

    @Override
    public void getModDependencies(ModDependencyCollector d) {
        // Libraries
        String[][] fapiModules = new String[][] {
            {"fabric-registry-sync-v0", "0.9.5+55dca1a4d2"},
            {"fabric-resource-loader-v0", "0.4.16+55dca1a4d2"},
            {"fabric-renderer-api-v1", "0.4.12+d882b915d2"},
            {"fabric-item-groups-v0", "0.3.8+3ac43d95d2"},
            {"fabric-object-builder-api-v1", "2.0.1+d882b915d2"},
            {"fabric-rendering-v1", "1.10.6+54e5b2ecd2"},
            {"fabric-networking-api-v1", "1.0.20+d882b915d2"},
            {"fabric-api-base", "0.4.3+d7c144a8d2"},
            {"fabric-models-v0", "0.3.5+d7c144a8d2"},
            {"fabric-renderer-indigo", "0.4.16+d8c7b9aed2"},
            {"fabric-entity-events-v1", "1.4.7+d7c144a8d2"},
            {"fabric-events-interaction-v0", "0.4.18+d7c144a8d2"},
            {"fabric-rendering-data-attachment-v1", "0.3.6+d7c144a8d2"},
            {"fabric-mining-level-api-v1", "2.0.2+d1027f7dd2"}
        };
        for (String[] module : fapiModules) {
            d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", module[0], module[1]), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE, ModDependencyFlag.JIJ);
        }
        d.addMaven("https://storage.googleapis.com/devan-maven/", new MavenId("net.devtech:Stacc:1.2.3"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE, ModDependencyFlag.JIJ);
        // Compat
        d.addMaven("https://maven.shedaniel.me/", new MavenId("me.shedaniel:RoughlyEnoughItems-api-fabric:6.0.247-alpha"), ModDependencyFlag.COMPILE);
        d.addMaven("https://maven.vram.io", new MavenId("io.vram:frex-fabric-mc118:6.0.236"), ModDependencyFlag.COMPILE);
        // Compat but for bruh moments
        d.addMaven("https://oskarstrom.net/maven", new MavenId("net.oskarstrom:DashLoader:2.0"), ModDependencyFlag.COMPILE);
        d.add(new JavaJarDependency(Maven.getMavenFileDep("https://gitlab.com/api/v4/projects/21830712/packages/maven", new MavenId("io.github.flemmli97", "flan", "1.18-1.6.6"), "-fabric-api.jar").file, null, null), ModDependencyFlag.COMPILE);
    }
}
