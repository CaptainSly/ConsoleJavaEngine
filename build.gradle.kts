
plugins {
    // Apply the java-library plugin for API and implementation separation.
    `java-library`
    id("maven-publish")
 }

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    maven {
    	url = uri("https://jitpack.io")
    }
}

dependencies {
	implementation("com.googlecode.lanterna:lanterna:3.1.2")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"]) // Or the component type you need to publish (e.g., kotlin)
            
            groupId = "com.spireprod.cje" // Your group ID
            artifactId = "console-java-engine" // The artifact ID
            version = "0.1.5-Talos" // Version
        }
    }

    repositories {
        maven {
            name = "Reposilite"
            url = uri("http://maven.reignleif.com/releases") // Your Reposilite URL
            isAllowInsecureProtocol = true
            credentials {
                username = findProperty("repo.username") as String? ?: System.getenv("REPO_USERNAME")
                password = findProperty("repo.password") as String? ?: System.getenv("REPO_PASSWORD")
            }
        }
    }
}
