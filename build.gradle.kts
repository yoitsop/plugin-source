buildscript {
    repositories {
        gradlePluginPortal()
    }
}

plugins {
    checkstyle
}



apply<BootstrapPlugin>()

subprojects {
    var subprojectName = name
    group = "com.openosrs.externals"


    project.extra["PluginProvider"] = "OP#9663"
    project.extra["ProjectUrl"] = "https://github.com/yoitsop/plugin-release/"
    project.extra["PluginLicense"] = "3-Clause BSD License"
    project.extra["GithubUrl"] = "https://github.com/yoitsop/plugin-release"
    project.extra["ProjectSupportUrl"] = "https://github.com/yoitsop/plugin-release"


    repositories {
        jcenter {
            content {
                excludeGroupByRegex("com\\.openosrs.*")
                excludeGroupByRegex("com\\.runelite.*")
            }
        }

        exclusiveContent {
            forRepository {
                maven {
                    url = uri("https://repo.runelite.net")
                }
            }
            filter {
                includeModule("net.runelite", "discord")
                includeModule("net.runelite.jogl", "jogl-all")
                includeModule("net.runelite.gluegen", "gluegen-rt")
            }
        }

        exclusiveContent {
            forRepository {
                mavenLocal()
            }
            filter {
                includeGroupByRegex("com\\.openosrs.*")
            }
        }
    }

    apply<JavaPlugin>()

    
    configure<JavaPluginConvention> {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    tasks {
        withType<JavaCompile> {
            options.encoding = "UTF-8"
        }

        withType<Jar> {
            doLast {
                copy {
                    from("./build/libs/")
                    into("../release/")
                }

                val externalManagerDirectory: String = project.findProperty("externalManagerDirectory")?.toString() ?: System.getProperty("user.home") + "/.runelite/externalmanager"
                val releaseToExternalModules: List<String> = project.findProperty("releaseToExternalmanager")?.toString()?.split(",") ?: emptyList()
                if (releaseToExternalModules.contains(subprojectName) || releaseToExternalModules.contains("all")) {
                    copy {
                        from("./build/libs/")
                        into(externalManagerDirectory)
                    }
                }
            }
        }

        withType<AbstractArchiveTask> {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
            dirMode = 493
            fileMode = 420
        }
    }
}
