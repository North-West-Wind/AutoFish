plugins {
    id("multiloader-common")
    id("fabric-loom-compat")
    kotlin("jvm") version "2.2.0"
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
}

loom {
    if (stonecutter.eval(commonMod.mc, "<=1.21.11")) {
        mixin {
            useLegacyMixinAp = false
        }
    }
}

dependencies {
    minecraft("com.mojang:minecraft:${commonMod.mc}")

    if (stonecutter.eval(commonMod.mc, "<=1.21.11")) {
        mappings(loom.layered {
            officialMojangMappings()
            commonMod.depOrNull("parchment")?.let { parchmentVersion ->
                parchment("org.parchmentmc.data:parchment-${commonMod.mc}:$parchmentVersion@zip")
            }
        })
    }

    modCompileOnly("net.fabricmc:fabric-loader:${commonMod.dep("fabric_loader")}")
}

val commonJava: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

val commonResources: Configuration by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
}

artifacts {
    afterEvaluate {
        val mainSourceSet = sourceSets.main.get()
        mainSourceSet.java.sourceDirectories.files.forEach {
            add(commonJava.name, it)
        }
        mainSourceSet.resources.sourceDirectories.files.forEach {
            add(commonResources.name, it)
        }
    }
}