installRelease:
	./gradlew installRelease
	adb shell am start -W -n "com.example.proxytest/com.example.proxytest.MainActivity"
installReleaseWithProguardRule:
	./gradlew installReleaseWithProguard
	adb shell am start -W -n "com.example.proxytest/com.example.proxytest.MainActivity"