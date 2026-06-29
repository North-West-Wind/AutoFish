// Disable building for versions, only for branches (fabric, forge, neoforge, common).
tasks.matching { it.name == "build" || it.name.startsWith("compile") || it.name == "classes" || it.name == "jar" || it.name == "javadoc" }.configureEach {
    enabled = false
}
