QT += quick androidextras

ANDROID_PACKAGE_SOURCE_DIR = $$PWD/android-sources

SOURCES += \
    main.cpp \
    notificationclient.cpp

OTHER_FILES += \
    qml/main.qml \
    android-sources/AndroidManifest.xml \
    android-sources/src/com/entwickeln/enmenu/WifiHandler.java \
    android-sources/src/com/entwickeln/enmenu/PackageHandler.java \
    android-sources/src/com/entwickeln/enmenu/BootUpReceiver.java \
    android-sources/src/com/entwickeln/enmenu/MainActivity.java \
    android-sources/src/com/entwickeln/enmenu/ScreenHandler.java

RESOURCES += \
    main.qrc

HEADERS += \
    notificationclient.h
