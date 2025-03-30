# android-fortune

This repo is based on https://github.com/316k/android-fortune
A friend was asking to have something similar to this.

History:
V0.1 - Initial Version, based on the 316k repo but updated to
have it at least compiled on my system.

V0.2 - After 10 iterations I found a way to use my own files.
Copy Download_fortunes into your Download folder and name it 'fortunes'
Grant File access permissions for the app:
  long click on the app, click App info, click permissions
Also added some debugging to check whats going on.

---
TODO:
* find a name and refactor the code to use the new name
* create an icon and add it
* make it check for files permissions
* some better error handling
* handling of different files:
** filename=category name would be easiest
** preferences dialog to select which files/categories to show
** whats best to put it into arraylist? Lets stay with a global array
   and add a second array with just the category name of the entry ;)

---

A Unix fortune viewer (app & widget) for Android.

By default, you will see the traditional Unix Fortunes, but you can add your own fortune files. Everything in `sdcard/fortunes/` will be added to the default fortunes.
