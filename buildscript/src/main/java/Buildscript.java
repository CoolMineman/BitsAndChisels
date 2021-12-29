import io.github.coolcrabs.brachyura.decompiler.BrachyuraDecompiler;
import io.github.coolcrabs.brachyura.dependency.JavaJarDependency;
import io.github.coolcrabs.brachyura.fabric.FabricLoader;
import io.github.coolcrabs.brachyura.fabric.FabricMaven;
import io.github.coolcrabs.brachyura.fabric.FabricProject;
import io.github.coolcrabs.brachyura.fabric.Yarn;
import io.github.coolcrabs.brachyura.maven.Maven;
import io.github.coolcrabs.brachyura.maven.MavenId;
import net.fabricmc.mappingio.tree.MappingTree;

public class Buildscript extends FabricProject {

    @Override
    public String getMcVersion() {
        return "1.18";
    }

    @Override
    public MappingTree createMappings() {
        return Yarn.ofMaven(FabricMaven.URL, FabricMaven.yarn("1.18+build.1")).tree;
    }

    @Override
    public FabricLoader getLoader() {
        return new FabricLoader(FabricMaven.URL, FabricMaven.loader("0.12.8"));
    }

    @Override
    public void getModDependencies(ModDependencyCollector d) {
        // Libraries
        String[][] fapiModules = new String[][] {
            {"fabric-registry-sync-v0", "0.8.5+3ac43d9514"},
            {"fabric-resource-loader-v0", "0.4.11+3ac43d9514"},
            {"fabric-renderer-api-v1", "0.4.9+3ac43d9514"},
            {"fabric-item-groups-v0", "0.3.3+3ac43d9514"},
            {"fabric-object-builder-api-v1", "1.10.13+3ac43d9514"},
            {"fabric-rendering-v1", "1.10.3+6b21378a14"},
            {"fabric-networking-api-v1", "1.0.18+3ac43d9514"},
            {"fabric-api-base", "0.4.1+b4f4f6cd14"},
            {"fabric-models-v0", "0.3.3+3ac43d9514"},
            {"fabric-renderer-indigo", "0.4.12+3ac43d9514"},
            {"fabric-entity-events-v1", "1.4.5+6b21378a14"},
            {"fabric-events-interaction-v0", "0.4.15+3ac43d9514"},
            {"fabric-rendering-data-attachment-v1", "0.3.3+d154e2c614"},
            {"fabric-tool-attribute-api-v1", "1.3.4+7de09f5514"},
            {"fabric-tag-extensions-v0", "1.2.5+3ac43d9514"},
            {"fabric-mining-level-api-v1", "1.0.3+3ac43d9514"}
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

    @Override
    public BrachyuraDecompiler decompiler() {
        return null; // mixin bugs
    }
}
