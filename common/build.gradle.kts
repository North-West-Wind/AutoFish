plugins {
    id("multiloader-common")
    id("net.neoforged.moddev") version "2.0.141"
    kotlin("jvm") version "2.2.0"
    id("com.google.devtools.ksp") version "2.2.0-2.0.2"
}

neoForge {
    neoFormVersion = commonMod.dep("neoform")
    // Automatically enable AccessTransformers if the file exists
    val at = rootProject.file("src/main/resources/META-INF/accesstransformer.cfg")
    if (at.exists()) {
        accessTransformers.from(at.absolutePath)
    }
}

dependencies {
    // Fabric and NeoForge both bundle Fabric Mixin, so it is safe to use it in common
    // If you need to update, check what version they are using to see what is compatible
    // https://github.com/neoforged/NeoForge/blob/26.2.x/gradle.properties#L37
    // https://github.com/FabricMC/fabric-loader/blob/master/gradle.properties#L12
    compileOnly("net.fabricmc:sponge-mixin:0.17.3+mixin.0.8.7")
    // Fabric and NeoForge both bundle MixinExtras, so it is safe to use it in common
    annotationProcessor("io.github.llamalad7:mixinextras-common:0.5.3")
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