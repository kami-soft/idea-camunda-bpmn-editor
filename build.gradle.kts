plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
    id("com.github.node-gradle.node") version "3.5.1"
}

group = "dev.camunda.bpmn"
version = "0.0.6"

repositories {
    mavenCentral()
}

intellij {
    version.set("2023.2.6")
    type.set("IC")
    plugins.set(listOf("com.intellij.platform.images", "org.intellij.groovy"))
}

node {
    version.set("16.14.0")
    download.set(true)
}

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("242.*")
    }

    val buildNpm = register<com.github.gradle.node.npm.task.NpmTask>("buildNpm") {
        args.set(listOf("run", "build"))
        workingDir.set(file("${project.projectDir}/bpmn-editor-ui"))
    }

    val copyNpmBuild = register<Copy>("copyNpmBuild") {
        from("${project.projectDir}/bpmn-editor-ui/public")
        into("${project.projectDir}/src/main/resources/bpmn-editor-ui")
    }

    buildPlugin {
        dependsOn(buildNpm)
        finalizedBy(copyNpmBuild)
    }

    clean {
        delete(
            "${project.projectDir}/bpmn-editor-ui/public",
            "${project.projectDir}/src/main/resources/bpmn-editor-ui",
        )
    }
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")
}