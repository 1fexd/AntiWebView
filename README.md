# AntiWebView

Have you ever used horrible apps like Reddit which display ALL external links opened through their
shitty app in a Android WebView ("in-app browser") which, depending on the WebView implementation your phone is using, does not allow you to open the clicked link
in a browser?

AntiWebView is a simple LSPosed/LSPatch module which hooks WebViews in any app and displays a
notification allowing the user to open the link in their browser.

## Demo


https://github.com/1fexd/AntiWebView/assets/58902674/7137325d-7e86-4140-99d7-38022a7b76b8


The default browser in this case is set to another app of
mine, [LinkSheet](https://github.com/1fexd/LinkSheet), which restores the Android <12 Url-App-Link
Chooser

## Installation

Tested Android versions: 13

### Root / Magisk / LSPosed

* Download and install [Magisk](https://github.com/topjohnwu/Magisk)
* Download and install [LSPosed](https://github.com/LSPosed/LSPosed)
* Install AntiWebView from [releases](https://github.com/1fexd/AntiWebView/releases)
    * The app won't show up in your launcher, don't worry.
* Open the LSPosed manager, tap "Modules" in the navbar on the bottom
* AntiWebView should show up as a module
* Tap "Enable module"
* Select the apps you want AntiWebView to hook into
    * If the apps are running, you need to force-stop for AntiWebView to work
    * If you are running Android 13+, make sure the app has the Notification permission (can be set
      in the app's settings)
* When a selected app uses a WebView, a notification will now pop up which, when tapped, opens your
  default browser

### Non-Root / LSPatch / Shizuku

* Download and install [LSPatch](https://github.com/LSPosed/LSPatch)
* Download and install [Shizuku](https://github.com/RikkaApps/Shizuku)
    * Launch Shizuku and start it via either ADB or Wireless debugging (Android 11+)
    * Shizuku must be manually restarted after every boot - but the only step where it is actually
      required is when installed in LSPatch later on - so you probably don't have to have it running
      all the time
* Install AntiWebView from [releases](https://github.com/1fexd/AntiWebView/releases)
    * The app won't show up in your launcher, don't worry.
* Obtain an APK of the app you want to use AntiWebView with
    * Reputable sources are [APKMirror](apkmirror.com) and [APKPure](apkpure.com) (these sites
      provide bundles (`*.apks`, *`.xapk` etc.) as well as actual APK files - make sure to download
      the APK version)
    * The app you want to install must not yet be installed on your device - if it is, uninstall it
* Open the LSPatch manager and tap the banner at the top to grant it permission to use Shizuku
* Tap "Manage" in the navbar on the bottom, then tap the "+" button
* Give LSPatch access to a new directory where the patched files will be stored
* When the "New Patch" dialog is shown after that, tap "Select apk(s) from storage"
* Choose APK file obtained previously
* Select "Portable" in the "Patch Mode" chooser
* Tap "Embed modules", then check AntiWebView
* Tap "Start Patch"
* After the patch is done, tap "Install"
    * If you are running Android 13+, make sure the app has the Notification permission (can be set
      in the app's settings)
* If the patched app now uses a WebView, a notification will now pop up which, when tapped, opens
  your default browser
