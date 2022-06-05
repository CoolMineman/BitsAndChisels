import io.github.coolcrabs.brachyura.dependency.JavaJarDependency;
import io.github.coolcrabs.brachyura.fabric.FabricLoader;
import io.github.coolcrabs.brachyura.fabric.FabricMaven;
import io.github.coolcrabs.brachyura.fabric.SimpleFabricProject;
import io.github.coolcrabs.brachyura.fabric.Yarn;
import io.github.coolcrabs.brachyura.fabric.FabricContext.ModDependencyCollector;
import io.github.coolcrabs.brachyura.fabric.FabricContext.ModDependencyFlag;
import io.github.coolcrabs.brachyura.maven.Maven;
import io.github.coolcrabs.brachyura.maven.MavenId;
import io.github.coolcrabs.brachyura.minecraft.Minecraft;
import io.github.coolcrabs.brachyura.minecraft.VersionMeta;
import net.fabricmc.mappingio.tree.MappingTree;

public class Buildscript extends SimpleFabricProject {

    @Override
    public VersionMeta createMcVersion() {
        return Minecraft.getVersion("1.19-rc2");
    }

    @Override
    public int getJavaVersion() {
        return 17;
    }

    @Override
    public MappingTree createMappings() {
        return Yarn.ofMaven(FabricMaven.URL, FabricMaven.yarn("1.19-rc2+build.1")).tree;
    }

    @Override
    public FabricLoader getLoader() {
        return new FabricLoader(FabricMaven.URL, FabricMaven.loader("0.14.6"));
    }

    @Override
    public void getModDependencies(ModDependencyCollector d) {
        // Libraries
        String[][] fapiModules = new String[][] {
            {"fabric-registry-sync-v0", "0.9.14+92cf9a3ecd"},
            {"fabric-resource-loader-v0", "0.5.2+9e7660c6cd"},
            {"fabric-renderer-api-v1", "1.0.7+9ff28f40cd"},
            {"fabric-item-groups-v0", "0.3.22+9ff28f40cd"},
            {"fabric-object-builder-api-v1", "4.0.4+9ff28f40cd"},
            {"fabric-rendering-v1", "1.10.13+9ff28f40cd"},
            {"fabric-networking-api-v1", "1.0.25+9ff28f40cd"},
            {"fabric-api-base", "0.4.8+e62f51a3cd"},
            {"fabric-models-v0", "0.3.14+9ff28f40cd"},
            {"fabric-renderer-indigo", "0.6.5+9ff28f40cd"},
            {"fabric-entity-events-v1", "1.4.15+9ff28f40cd"},
            {"fabric-events-interaction-v0", "0.4.25+9ff28f40cd"},
            {"fabric-rendering-data-attachment-v1", "0.3.11+9ff28f40cd"},
            {"fabric-mining-level-api-v1", "2.1.6+9ff28f40cd"}
        };
        for (String[] module : fapiModules) {
            d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", module[0], module[1]), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
        }
        jij(d.addMaven("https://storage.googleapis.com/devan-maven/", new MavenId("net.devtech:Stacc:1.3.3"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE));
        // Compat
        d.addMaven("https://maven.shedaniel.me/", new MavenId("me.shedaniel:RoughlyEnoughItems-api-fabric:6.0.247-alpha"), ModDependencyFlag.COMPILE);
        d.addMaven("https://maven.vram.io", new MavenId("io.vram:frex-fabric-mc118:6.0.236"), ModDependencyFlag.COMPILE);
        // Compat but for bruh moments
        d.addMaven("https://oskarstrom.net/maven", new MavenId("net.oskarstrom:DashLoader:2.0"), ModDependencyFlag.COMPILE);
        d.add(new JavaJarDependency(Maven.getMavenFileDep("https://gitlab.com/api/v4/projects/21830712/packages/maven", new MavenId("io.github.flemmli97", "flan", "1.18-1.6.6"), "-fabric-api.jar").file, null, null), ModDependencyFlag.COMPILE);
        d.addMaven("https://api.modrinth.com/maven", new MavenId("maven.modrinth", "goml-reserved", "1.5.0-beta.4+1.18.2"), ModDependencyFlag.COMPILE);
    }
}
