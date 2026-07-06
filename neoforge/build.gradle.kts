plugins {
    id("multiloader-loader")
    id("net.neoforged.moddev") version "2.0.141"
    kotlin("jvm") version "2.2.0"
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
}

neoForge {
    version = commonMod.dep("neoforge")

    runs {
        register("client") {
            client()
            ideName = "NeoForge Client (${project.path})"
            gameDirectory = rootProject.layout.projectDirectory.dir("runs/client")
        }
        register("server") {
            server()
            ideName = "NeoForge Server (${project.path})"
            gameDirectory = rootProject.layout.projectDirectory.dir("runs/server")
        }
    }

    commonMod.depOrNull("parchment")?.let {
        parchment {
            mappingsVersion = it
            minecraftVersion = commonMod.mc
        }
    }

    mods {
        register(commonMod.id) {
            sourceSet(sourceSets.main.get())
        }
    }
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}