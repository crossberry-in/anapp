#!/bin/bash

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
ANDROID_JAR=""
BUILD_DIR="build"
APK_NAME="SimpleCalculator"

echo -e "${GREEN}=== SimpleCalculator APK Builder ===${NC}"
echo ""

# Check Android SDK
if [ -z "$ANDROID_HOME" ]; then
    echo -e "${YELLOW}ANDROID_HOME not set${NC}"
    echo "Please set ANDROID_HOME environment variable"
    exit 1
fi

# Set Android JAR path
if [ -f "$ANDROID_HOME/platforms/android-34/android.jar" ]; then
    ANDROID_JAR="$ANDROID_HOME/platforms/android-34/android.jar"
elif [ -f "$ANDROID_HOME/platforms/android-33/android.jar" ]; then
    ANDROID_JAR="$ANDROID_HOME/platforms/android-33/android.jar"
else
    echo -e "${RED}Error: android.jar not found${NC}"
    exit 1
fi

echo -e "${GREEN}Using Android JAR: $ANDROID_JAR${NC}"
echo ""

# Create build directory
echo "Creating build directory..."
mkdir -p "$BUILD_DIR"

# Compile resources
echo "Compiling resources..."
aapt2 compile --dir app/src/main/res -o "$BUILD_DIR/resources.zip"

# Link resources and create APK
echo "Linking resources and creating APK..."
aapt2 link \
  -I "$ANDROID_JAR" \
  --manifest app/src/main/AndroidManifest.xml \
  "$BUILD_DIR/resources.zip" \
  -o "$BUILD_DIR/$APK_NAME-unsigned.apk" \
  --min-sdk-version 21 \
  --target-sdk-version 34

# Compile Java source
echo "Compiling Java source..."
javac -source 8 -target 8 \
  -bootclasspath "$ANDROID_JAR" \
  -d "$BUILD_DIR" \
  app/src/main/java/com/example/calculator/MainActivity.java

# Convert to DEX
echo "Converting to DEX..."
d8 "$BUILD_DIR" --output "$BUILD_DIR" --lib "$ANDROID_JAR"

# Add classes.dex to APK
echo "Adding classes.dex to APK..."
cd "$BUILD_DIR"
zip -u "$APK_NAME-unsigned.apk" classes.dex
cd ..

# Generate debug keystore if not exists
if [ ! -f "debug.keystore" ]; then
    echo "Generating debug keystore..."
    keytool -genkey -v -keystore debug.keystore -alias androiddebugkey \
      -keyalg RSA -keysize 2048 -validity 10000 \
      -storepass android -keypass android \
      -dname "CN=Android Debug,O=Android,C=US"
fi

# Sign APK
echo "Signing APK..."
apksigner sign \
  --ks debug.keystore \
  --ks-pass pass:android \
  --key-pass pass:android \
  "$BUILD_DIR/$APK_NAME-unsigned.apk"

# Rename APK
mv "$BUILD_DIR/$APK_NAME-unsigned.apk" "$BUILD_DIR/$APK_NAME.apk"

echo ""
echo -e "${GREEN}=== Build Successful! ===${NC}"
echo -e "${GREEN}APK location: $BUILD_DIR/$APK_NAME.apk${NC}"
ls -lh "$BUILD_DIR/$APK_NAME.apk"
echo ""
echo "To install on device:"
echo "  adb install $BUILD_DIR/$APK_NAME.apk"
