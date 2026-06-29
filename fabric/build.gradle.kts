plugins {
    id("multiloader-loader")
    id("fabric-loom-compat")
    kotlin("jvm") version "2.2.0"
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
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

    implementation("net.fabricmc:fabric-loader:${commonMod.dep("fabric_loader")}")
    implementation("net.fabricmc.fabric-api:fabric-api:${commonMod.dep("fabric_api")}+${commonMod.mc}")
}

loom {
    runs {
        getByName("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
        }
        getByName("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
        }
    }

    if (stonecutter.eval(commonMod.mc, "<=1.21.11")) {
        mixin {
            useLegacyMixinAp = true
            defaultRefmapName = "${mod.id}.refmap.json"
        }
    }
}