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
        return Minecraft.getVersion("1.20.4");
    }

    @Override
    public int getJavaVersion() {
        return 17;
    }

    @Override
    public MappingTree createMappings() {
        return Yarn.ofMaven(FabricMaven.URL, FabricMaven.yarn("1.20.4+build.3")).tree;
    }

    @Override
    public FabricLoader getLoader() {
        return new FabricLoader(FabricMaven.URL, FabricMaven.loader("0.15.7"));
    }


    @Override
    public void getModDependencies(ModDependencyCollector d) {
        // Libraries
        String[][] fapiModules = new String[][] {
            {"fabric-registry-sync-v0", "2.2.6+b3afc78b77"},
            {"fabric-resource-loader-v0", "0.11.7+f7923f6d77"},
            {"fabric-renderer-api-v1", "3.1.0+c154966e77"},
            {"fabric-item-group-api-v1", "4.0.7+b3afc78b77"},
            {"fabric-object-builder-api-v1", "11.0.6+b3afc78b77"},
            {"fabric-rendering-v1", "3.0.6+b3afc78b77"},
            {"fabric-networking-api-v1", "1.3.8+b3afc78b77"},
            {"fabric-api-base", "0.4.29+b04edc7a77"},
            {"fabric-models-v0", "0.3.35+b3afc78b77"},
            {"fabric-renderer-indigo", "1.4.0+c154966e77"},
            {"fabric-entity-events-v1", "1.5.21+b3afc78b77"},
            {"fabric-events-interaction-v0", "0.6.0+b3afc78b77"},
            {"fabric-rendering-data-attachment-v1", "0.3.33+b3afc78b77"},
            {"fabric-lifecycle-events-v1", "2.2.20+b3afc78b77"},
            {"fabric-mining-level-api-v1", "2.1.47+b3afc78b77"}
        };
        for (String[] module : fapiModules) {
            d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", module[0], module[1]), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
        }
        jij(d.addMaven(Maven.MAVEN_LOCAL, new MavenId("net.devtech:stacc:1.8.0"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE));
        // Compat
        d.addMaven("https://maven.shedaniel.me/", new MavenId("me.shedaniel:RoughlyEnoughItems-api-fabric:9.0.475"), ModDependencyFlag.COMPILE);
        d.addMaven("https://maven.vram.io", new MavenId("io.vram:frex-fabric-mc118:6.0.236"), ModDependencyFlag.COMPILE);
        // Compat but for bruh moments
        d.addMaven("https://oskarstrom.net/maven", new MavenId("net.oskarstrom:DashLoader:2.0"), ModDependencyFlag.COMPILE);
        d.add(new JavaJarDependency(Maven.getMavenFileDep("https://gitlab.com/api/v4/projects/21830712/packages/maven", new MavenId("io.github.flemmli97", "flan", "1.18-1.6.6"), "-fabric-api.jar").file, null, null), ModDependencyFlag.COMPILE);
        d.addMaven("https://api.modrinth.com/maven", new MavenId("maven.modrinth", "goml-reserved", "1.5.0-beta.4+1.18.2"), ModDependencyFlag.COMPILE);
    }
}
