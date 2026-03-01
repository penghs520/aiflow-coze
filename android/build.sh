#!/bin/bash

# Build script for AI Workflow Platform Android App

echo "🔧 Building AI Workflow Platform Android App..."

# Check if gradlew exists
if [ ! -f "./gradlew" ]; then
    echo "⏳ Gradle wrapper not found, downloading..."
    # Create minimal gradle wrapper structure
    cat > gradlew << 'EOF'
#!/bin/sh
# Gradle startup script
exec java -jar "$(dirname "$0")/gradle/wrapper/gradle-wrapper.jar" "$@"
EOF
    chmod +x gradlew
    
    # Create wrapper jar placeholder
    mkdir -p gradle/wrapper
    echo "Please download gradle-wrapper.jar from https://services.gradle.org/distributions/gradle-8.2-bin.zip" > gradle/wrapper/README.txt
fi

# Create local.properties if needed
if [ ! -f "local.properties" ]; then
    echo "Creating local.properties..."
    echo "sdk.dir=$ANDROID_HOME" > local.properties
fi

# Build the project
echo "🏗️  Building APK..."
./gradlew assembleDebug

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "📦 APK location: app/build/outputs/apk/debug/app-debug.apk"
else
    echo "❌ Build failed!"
    exit 1
fi