plugins {
    id("multiloader-loader")
    id("net.minecraftforge.gradle") version "[7.0.17,8)"
    id("net.minecraftforge.renamer") version "1.1.2"
    kotlin("jvm") version "2.2.0"
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
}

// Version will be added after renaming
if (stonecutter.eval(commonMod.mc, "<=1.20.4")) version = "${commonMod.version}-${stonecutterBuild.current.version}"

minecraft {
    if (stonecutter.eval(commonMod.mc, "<1.17")) mappings("parchment", "${commonMod.mc}-${commonMod.dep("parchment")}")
    mappings("official", commonMod.mc)

    val at = rootProject.file("src/${loader}/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformer.from(at)
    }

    runs {
        configureEach {
            systemProperty("eventbus.api.strictRuntimeChecks", "true")
            systemProperty("forge.enabledGameTestNamespaces", commonMod.id)
        }

        register("client") {
            workingDir = rootProject.file("runs/client")
        }

        register("server") {
            workingDir = rootProject.file("runs/server")
            args("--nogui")
        }

        register("gameTestServer") {
            workingDir = rootProject.file("runs/gameTestServer")
        }

        register("data") {
            workingDir = rootProject.file("runs/data")
            args("--mod", commonMod.id, "--all", "--output", rootProject.file("src/${loader}/generated/resources"), "--existing", rootProject.file("src/${loader}/resources"))
        }
    }
}

sourceSets.main {
    resources.srcDir("src/generated/resources")
}

repositories {
    minecraft.mavenizer(this) // In Kotlin, it = this
    maven(fg.forgeMaven)
    maven(fg.minecraftLibsMaven)
}

dependencies {
    implementation(minecraft.dependency("net.minecraftforge:forge:${commonMod.mc}-${commonMod.dep("forge")}"))
}

// The renamer plugin is required for Forge <= 1.20.4
if (stonecutter.eval(commonMod.mc, "<=1.20.4")) {
    tasks.register<Delete>("deleteJar") {
        description = "Deletes the JAR before renaming"
        delete(layout.buildDirectory.file("libs/${commonMod.id}-${version}.jar"))
    }

    renamer.classes("renameJar", tasks.named<Jar>("jar")) {
        map.from(minecraft.dependency.toSrgFile)
        archiveClassifier = loader
        finalizedBy("deleteJar")
    }
}