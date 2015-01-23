QT += quick androidextras

ANDROID_PACKAGE_SOURCE_DIR = $$PWD/android-sources

SOURCES += \
    main.cpp \
    notificationclient.cpp

OTHER_FILES += \
    qml/main.qml \
    android-sources/src/org/qtproject/example/notification/NotificationClient.java \
    android-sources/AndroidManifest.xml \
    android-sources/src/org/qtproject/example/notification/AlarmReceiver.java \
    android-sources/src/org/qtproject/example/notification/BootUpReceiver.java \
    android-sources/src/org/qtproject/example/notification/DaemonService.java \
    android-sources/src/org/qtproject/example/notification/WifiHandler.java \
    android-sources/src/org/qtproject/example/notification/PackageHandler.java

RESOURCES += \
    main.qrc

HEADERS += \
    notificationclient.h
