import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.9"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
	kotlin("kapt") version "1.9.25"
}

allprojects {
	group = "com.chaltteok"
	version = "0.0.1-SNAPSHOT"
	description = "Demo project for Spring Boot"

	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")

	configurations {
		all {
			exclude(group = "org.springframework.boot", module = "spring-boot-starter-logging")
		}
	}

	dependencies {
		implementation("org.jetbrains.kotlin:kotlin-reflect")

		//log4j2
		implementation("org.springframework.boot:spring-boot-starter-log4j2")
		implementation("com.lmax:disruptor:3.4.4")
		implementation("io.github.oshai:kotlin-logging-jvm:5.1.0")

		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
		testImplementation("io.mockk:mockk:1.14.5")
		testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	}

	val springCloudVersion = "2025.0.0"

	dependencyManagement {
		imports {
			mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
		}
	}

	tasks.withType<KotlinCompile> {
		kotlinOptions {
			freeCompilerArgs += "-Xjsr305=strict"
			jvmTarget = "21"
		}
	}
}



