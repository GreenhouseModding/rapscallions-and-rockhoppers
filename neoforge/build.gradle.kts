import house.greenhouse.examplemod.gradle.Properties
import house.greenhouse.examplemod.gradle.Versions
import org.apache.tools.ant.filters.LineContains
import org.gradle.jvm.tasks.Jar

plugins {
    id("conventions.loader")
    id("net.neoforged.moddev")
    id("me.modmuss50.mod-publish-plugin")
}

neoForge {
    version = Versions.NEOFORGE
    parchment {
        minecraftVersion = Versions.PARCHMENT_MINECRAFT
        mappingsVersion = Versions.PARCHMENT
    }
    addModdingDependenciesTo(sourceSets["test"])

    val at = project(":common").file("src/main/resources/${Properties.MOD_ID}.cfg")
    if (at.exists())
        setAccessTransformers(at)
    validateAccessTransformers = true

    runs {
        configureEach {
            systemProperty("forge.logging.markers", "REGISTRIES")
            systemProperty("forge.logging.console.level", "debug")
            systemProperty("neoforge.enabledGameTestNamespaces", Properties.MOD_ID)
        }
        create("client") {
            client()
            ideName = "NeoForge Client (:${project.name})"
            gameDirectory.set(file("runs/client"))
            sourceSet = sourceSets["test"]
            jvmArguments.set(setOf("-Dmixin.debug.verbose=true", "-Dmixin.debug.export=true"))
        }
        create("server") {
            server()
            ideName = "NeoForge Server (:${project.name})"
            gameDirectory.set(file("runs/server"))
            programArgument("--nogui")
            sourceSet = sourceSets["test"]
            jvmArguments.set(setOf("-Dmixin.debug.verbose=true", "-Dmixin.debug.export=true"))
        }
    }

    mods {
        register(Properties.MOD_ID) {
            sourceSet(sourceSets["main"])
            sourceSet(sourceSets["test"])
        }
    }
}

dependencies {
    implementation("net.tslat.smartbrainlib:SmartBrainLib-neoforge-${Versions.MINECRAFT}:${Versions.SMART_BRAIN_LIB}")
    jarJar("net.tslat.smartbrainlib:SmartBrainLib-neoforge-${Versions.MINECRAFT}:${Versions.SMART_BRAIN_LIB}")
}

tasks {
    named<ProcessResources>("processResources").configure {
        filesMatching("*.mixins.json") {
            filter<LineContains>("negate" to true, "contains" to setOf("refmap"))
        }
    }
}

publishMods {
    file.set(tasks.named<Jar>("jar").get().archiveFile)
    modLoaders.add("neoforge")
    changelog = rootProject.file("CHANGELOG.md").readText()
    displayName = "v${Versions.MOD} (NeoForge ${Versions.MINECRAFT})"
    version = "${Versions.MOD}+${Versions.MINECRAFT}-neoforge"
    type = STABLE

    curseforge {
        projectId = Properties.CURSEFORGE_PROJECT_ID
        accessToken = providers.environmentVariable("CURSEFORGE_TOKEN")

        minecraftVersions.add(Versions.MINECRAFT)
        javaVersions.add(JavaVersion.VERSION_21)

        clientRequired = true
        serverRequired = true
    }

    modrinth {
        projectId = Properties.MODRINTH_PROJECT_ID
        accessToken = providers.environmentVariable("MODRINTH_TOKEN")

        minecraftVersions.add(Versions.MINECRAFT)
    }

    github {
        accessToken = providers.environmentVariable("GITHUB_TOKEN")
        parent(project(":common").tasks.named("publishGithub"))
    }
}