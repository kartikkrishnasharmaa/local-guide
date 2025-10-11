import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	war
	id("org.springframework.boot") version "3.2.0"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.20"
	kotlin("plugin.spring") version "1.9.20"
}

group = "com.guider"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

//ext["tomcat.version"] = "10.0.27"

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
//	providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("mysql:mysql-connector-java:8.0.33")
	implementation("com.google.code.gson:gson:2.10.1")
	implementation("id.zelory:compressor:3.0.1")
	implementation("org.apache.poi:poi:5.2.5")
	implementation("org.apache.poi:poi-ooxml:5.2.5")
//	implementation("com.auth0:java-jwt:4.4.0")
	// Google Auth Library for OAuth 2.0
	implementation("com.google.auth:google-auth-library-oauth2-http:1.23.0")

	// Firebase Admin SDK (Optional: Needed if using Firebase SDK directly)
	implementation("com.google.firebase:firebase-admin:9.2.0")

}


tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
