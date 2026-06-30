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

    modImplementation("net.fabricmc:fabric-loader:${commonMod.dep("fabric_loader")}")

    // In older versions, Fabric uses the base Minecraft version for Fabric API. Specify by using {api-ver}+{mc-ver}
    if (commonMod.dep("fabric_api").contains("+")) modApi("net.fabricmc.fabric-api:fabric-api:${commonMod.dep("fabric_api")}")
    else modApi("net.fabricmc.fabric-api:fabric-api:${commonMod.dep("fabric_api")}+${commonMod.mc}")

    commonMod.depOrNull("modmenu")?.let { modMenuVersion ->
        modImplementation("com.terraformersmc:modmenu:${modMenuVersion}")
    }
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