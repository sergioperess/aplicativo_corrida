plugins {
    alias(libs.plugins.androidLibrary)
    id("maven-publish")
}

android {
    namespace = "avancada.application.funclibrary"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    publishing {
        // Configuração do Maven Publishing para a variante release
        singleVariant("release") {
            withSourcesJar() // Gera o arquivo JAR de fontes
        }
    }
}

publishing {
    publications {
        // Define a publicação chamada "bar"
        create<MavenPublication>("bar") {
            groupId = "avancada.application.av1_avancada"
            artifactId = "funcionalidades"
            version = "v1"

            // Adiciona o componente "release" do Android para ser publicado
            afterEvaluate {
                from(components["release"])
            }
        }
    }

    // Define onde a publicação será colocada (repositório local, por exemplo)
    repositories {
        maven {
            name = "GithubPackages"
            url = uri("https://maven.pkg.github.com/sergioperess/aplicativo_corrida")
            credentials{
                username = System.getenv("USERNAME")
                password = System.getenv("API_TOKEN")
            }
        }
    }
}

dependencies {
    implementation("avancada.application.av1_avancada:funcionalidades:v1")
    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
