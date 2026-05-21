plugins {
    id("me.devnatan.inventoryframework.library")
    alias(libs.plugins.shadowjar)
    id("io.papermc.paperweight.userdev")
}

inventoryFramework {
    publish = true
}

dependencies {
    paperweight.paperDevBundle("26.1.2.build.+")
    //compileOnly(libs.paperSpigot)
    implementation(projects.inventoryFrameworkPlatformBukkit)
}

java {
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.shadowJar {
    archiveBaseName.set("inventory-framework")
    archiveAppendix.set("paper")

    dependencies {
        exclude {
            it.moduleGroup == "org.jetbrains.kotlin"
        }
    }
}