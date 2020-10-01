# boomboom

A bomberman clone which I originally released on the Ouya console in 2013.  Like the Ouya console, this game was very unpopular.

Requires a gamepad to play, but supports Xbox, Playstation, and Wii gamepads.

![screenshot of main menu](https://i.imgur.com/jvp858D.jpg)

![screenshot of gameplay](https://i.imgur.com/w05opBH.jpg)

## Building From Source

As this is not an actively developed project, getting it to build may get trickier as time goes on.  I rebuilt the project in December 2017 using Android studio 3.x, and it still builds as of October 2020 using Android Studio 4.0.1.

Some tips:
* Android Studio should more-or-less guide you through installing the necessary SDK version (27 / Android 8.1) and related tools.  I didn't allow it to upgrade Android Gradle Plugin from version 3.0.1 as I vaguely recall compatibility issues with the game engine this is built on (libGDX).
* I had to install Java SE Development Kit 8.

Building a runnable jar:
* One way to build BoomBoom is to run `BUILD_JAR.bat` in the root directory.  The output will be at `desktop/build/libs/desktop-1.0.jar`.  There is also a file `PlayBoomBoom.bat` in the root which will run the jar file.
* (alternate method) In Android Studio, go to View > Tool Windows > Gradle.  Expand boomboom > desktop > Tasks > other.  Run the "dist" task.

Running within Android Studio:
* In Android Studio, go to View > Tool Windows > Gradle.  Expand boomboom > desktop > Tasks > other.  Right click the "run" task and choose the "Create" option.  Then click OK.  This will create a run configuration for you.  You can then run or debug as needed.
* (alternate method) As instructed [here](https://libgdx.badlogicgames.com/documentation/gettingstarted/Running%20and%20Debugging.html#running-desktop-project-in-intellij-android-studio), you can create a new run configuration manually.  Create a run configuration of "Application" type named "DesktopLauncher", and set the Main class to `com.brian.boomboom.desktop.DesktopLauncher`, Working directory to the `boomboom/android/assets` directory, and Use classpath of module `desktop`.
