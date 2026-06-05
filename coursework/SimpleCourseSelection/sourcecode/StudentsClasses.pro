QT       += core gui

greaterThan(QT_MAJOR_VERSION, 4): QT += widgets

CONFIG += c++11

# You can make your code fail to compile if it uses deprecated APIs.
# In order to do so, uncomment the following line.
#DEFINES += QT_DISABLE_DEPRECATED_BEFORE=0x060000    # disables all the APIs deprecated before Qt 6.0.0

SOURCES += \
    addstudentclassesdialog.cpp \
    addstudentdialog.cpp \
    classes.cpp \
    classesdialog.cpp \
    classeslistdialog.cpp \
    deletestudentclassesdialog.cpp \
    deletestudentdialog.cpp \
    displayclassstudentsdialog.cpp \
    faileddialog.cpp \
    main.cpp \
    mainwindow.cpp \
    searchstudentdialog.cpp \
    searchstudentresultdialog.cpp \
    student.cpp \
    studentlistdialog.cpp \
    successdialog.cpp

HEADERS += \
    addstudentclassesdialog.h \
    addstudentdialog.h \
    classes.h \
    classesdialog.h \
    classeslistdialog.h \
    deletestudentclassesdialog.h \
    deletestudentdialog.h \
    displayclassstudentsdialog.h \
    faileddialog.h \
    mainwindow.h \
    searchstudentdialog.h \
    searchstudentresultdialog.h \
    student.h \
    studentlistdialog.h \
    successdialog.h

FORMS += \
    addstudentclassesdialog.ui \
    addstudentdialog.ui \
    classesdialog.ui \
    classeslistdialog.ui \
    deletestudentclassesdialog.ui \
    deletestudentdialog.ui \
    displayclassstudentsdialog.ui \
    faileddialog.ui \
    mainwindow.ui \
    searchstudentdialog.ui \
    searchstudentresultdialog.ui \
    studentlistdialog.ui \
    successdialog.ui

# Default rules for deployment.
qnx: target.path = /tmp/$${TARGET}/bin
else: unix:!android: target.path = /opt/$${TARGET}/bin
!isEmpty(target.path): INSTALLS += target
