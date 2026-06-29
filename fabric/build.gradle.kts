plugins {
    id("multiloader-loader")
    id("net.fabricmc.fabric-loom") version "1.16.3"
    kotlin("jvm") version "2.2.0"
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
    id("dev.kikugie.fletching-table.fabric") version "0.1.0-alpha.22"
}

stonecutter {

}

dependencies {
    minecraft("com.mojang:minecraft:${commonMod.mc}")
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
}