plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.3.5"
	id("io.spring.dependency-management") version "1.1.6"
	//id("org.graalvm.buildtools.native") version "0.10.3"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

//---
springBoot {
	buildInfo()
}

tasks.bootBuildImage {
	//dependsOn(tasks.check) // <-- require tests to pass before building the image

	// https://docs.spring.io/spring-boot/gradle-plugin/packaging-oci-image.html#build-image.customization
	// default JVM -> base
	// graalvm -> tiny
	//builder.set("paketobuildpacks/builder-jammy-full")

	docker {
		// enable local build (without push) for testing
		val cr = project.properties["registry"]?.toString() ?: "ghcr.io"
		val crUser = project.properties["registryUser"]?.toString() ?: ""
		val crPassword = project.properties["registryPassword"]?.toString() ?: ""

		val crImage = project.properties["registryImage"]?.toString() ?: "${cr}/mkoertgen/hello.buildpacks/${rootProject.name}"
		imageName.set("${crImage}:${version}")

		publish.set(crUser.isNotBlank() && crPassword.isNotBlank())
		publishRegistry {
			username.set(crUser)
			password.set(crPassword)
			url.set(cr)
		}
	}
}

