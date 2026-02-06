# SimpleCalculator Android APK

Simple calculator app built with native Android SDK.

## 📱 Features

- Basic arithmetic operations (add, subtract, multiply, divide)
- Clear, backspace, and decimal point support
- Clean and intuitive UI

## 🔧 Building the APK

### Option 1: GitHub Actions (Recommended for ARM systems)

1. Create a new GitHub repository
2. Push this project to GitHub
3. Go to **Actions** tab
4. **workflow_dispatch** to trigger build manually, or push to main branch
5. Download APK from **Artifacts** section

```bash
# Initialize and push to GitHub
git init
git add .
git commit -m "Initial commit"
git branch -M main
git remote add origin https://github.com/YOUR_USERNAME/YOUR_REPO.git
git push -u origin main
```

### Option 2: Local Build (Requires x86-64 system)

```bash
# Set Android SDK path
export ANDROID_HOME=/path/to/android/sdk
export ANDROID_JAR=$ANDROID_HOME/platforms/android-34/android.jar

# Create build directory
mkdir -p build

# Compile resources
aapt2 compile --dir app/src/main/res -o build/resources.zip

# Link resources and create APK
aapt2 link \
  -I $ANDROID_JAR \
  --manifest app/src/main/AndroidManifest.xml \
  build/resources.zip \
  -o build/Calculator-unsigned.apk \
  --min-sdk-version 21 \
  --target-sdk-version 34

# Compile Java source
javac -source 8 -target 8 \
  -bootclasspath $ANDROID_JAR \
  -d build \
  app/src/main/java/com/example/calculator/MainActivity.java

# Convert to DEX
d8 build --output build --lib $ANDROID_JAR

# Add classes.dex to APK
cd build
zip -u Calculator-unsigned.apk classes.dex
cd ..

# Create debug keystore
keytool -genkey -v -keystore debug.keystore -alias androiddebugkey \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -storepass android -keypass android \
  -dname "CN=Android Debug,O=Android,C=US"

# Sign APK
apksigner sign \
  --ks debug.keystore \
  --ks-pass pass:android \
  --key-pass pass:android \
  build/Calculator-unsigned.apk

# Rename
mv build/Calculator-unsigned.apk build/SimpleCalculator.apk
```

## 📦 Project Structure

```
.
├── app/
│   ├── build.gradle            # App configuration
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml # App manifest
│       ├── java/com/example/calculator/
│       │   └── MainActivity.java
│       └── res/
│           ├── drawable/       # App icons
│           ├── layout/         # UI layouts
│           └── values/         # String resources
├── .github/workflows/
│   └── android.yml            # GitHub Actions workflow
├── build.gradle               # Project configuration
└── settings.gradle            # Project settings
```

## 🚀 Installing APK

```bash
# Install on connected device
adb install build/SimpleCalculator.apk

# Or transfer to device and install manually
```

## ⚠️ ARM64 Build Note

If you're on an ARM64 system (like Termux), native build won't work because
Android SDK build-tools are only available as x86-64 binaries.

**Solution:** Use GitHub Actions for cloud builds (free, fast, native x86-64).

## 📄 License

For educational purposes.
