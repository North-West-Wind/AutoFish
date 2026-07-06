val IS_CI = System.getenv("CI") == "true"

plugins {
    id("dev.kikugie.stonecutter")
    id("net.neoforged.moddev") version "2.0.141" apply false
    id("net.fabricmc.fabric-loom") version "1.17-SNAPSHOT" apply false
    id("net.fabricmc.fabric-loom-remap") version "1.17-SNAPSHOT" apply false
}

stonecutter {
    parameters {
        replacements.string(current.parsed < "1.21.11") {
            replace("Identifier", "ResourceLocation")
        }
    }
}

if (IS_CI) stonecutter active null
else stonecutter active "26.2"