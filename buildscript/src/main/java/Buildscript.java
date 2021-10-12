import io.github.coolcrabs.brachyura.fabric.FabricLoader;
import io.github.coolcrabs.brachyura.fabric.FabricMaven;
import io.github.coolcrabs.brachyura.fabric.FabricProject;
import io.github.coolcrabs.brachyura.fabric.Yarn;
import io.github.coolcrabs.brachyura.maven.MavenId;
import net.fabricmc.mappingio.tree.MappingTree;

public class Buildscript extends FabricProject {

    @Override
    public String getMcVersion() {
        return "1.17.1";
    }

    @Override
    public MappingTree createMappings() {
        return Yarn.ofMaven(FabricMaven.URL, FabricMaven.yarn("1.17.1+build.61")).tree;
    }

    @Override
    public FabricLoader getLoader() {
        return new FabricLoader(FabricMaven.URL, FabricMaven.loader("0.12.0"));
    }

    @Override
    public String getModId() {
        return "bitsandchisels";
    }

    @Override
    public String getVersion() {
        return "2.5.4";
    }

    @Override
    public void getModDependencies(ModDependencyCollector d) {
        // Libraries
        String[][] fapiModules = new String[][] {
            {"fabric-resource-loader-v0", "0.4.8+a00e834b18"},
            {"fabric-renderer-api-v1", "0.4.4+cbda931818"},
            {"fabric-item-groups-v0", "0.2.10+b7ab612118"},
            {"fabric-object-builder-api-v1", "1.10.9+cbda931818"},
            {"fabric-rendering-v1", "1.8.2+ffb6d41e18"},
            {"fabric-networking-api-v1", "1.0.13+cbda931818"},
            {"fabric-api-base", "0.3.0+a02b446318"},
            {"fabric-models-v0", "0.3.0+a02b446318"},
            {"fabric-renderer-indigo", "0.4.8+cbda931818"},
            {"fabric-entity-events-v1", "1.2.3+87cc6e4c18"},
            {"fabric-networking-blockentity-v0", "0.2.11+a02b446318"},
            {"fabric-events-interaction-v0", "0.4.10+fc40aa9d18"},
            {"fabric-rendering-data-attachment-v1", "0.1.5+a02b446318"},
            {"fabric-tool-attribute-api-v1", "1.2.12+b7ab612118"},
            {"fabric-tag-extensions-v0", "1.2.1+b06cb95b18"}
        };
        for (String[] module : fapiModules) {
            d.addMaven(FabricMaven.URL, new MavenId(FabricMaven.GROUP_ID + ".fabric-api", module[0], module[1]), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE);
        }
        d.addMaven("https://storage.googleapis.com/devan-maven/", new MavenId("net.devtech:Stacc:1.2.3"), ModDependencyFlag.RUNTIME, ModDependencyFlag.COMPILE, ModDependencyFlag.JIJ);
        // Compat
        d.addMaven("https://maven.shedaniel.me/", new MavenId("me.shedaniel:RoughlyEnoughItems-api-fabric:6.0.247-alpha"), ModDependencyFlag.COMPILE);
        d.addMaven("https://maven.vram.io", new MavenId("io.vram:frex-fabric-mc117:6.0.46"), ModDependencyFlag.COMPILE);
        d.addMaven("https://oskarstrom.net/maven", new MavenId("net.oskarstrom:DashLoader:2.0"), ModDependencyFlag.COMPILE);
    } 

}
