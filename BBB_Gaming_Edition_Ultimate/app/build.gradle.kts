plugins { id("com.android.application") }

android {
    namespace = "com.bbb.gaming"
    compileSdk = 35

    defaultConfig {
            applicationId = "com.bbb.gaming"
                minSdk = 24
                    targetSdk = 35
                        versionCode = 1
                            versionName = "1.0"
    }

    externalNativeBuild {
            cmake {
                        path = file("src/main/cpp/CMakeLists.txt")
                                version = "3.22.1"
            }
    }
}
            }
    }
}