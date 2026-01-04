# Sillicat
Sillicat is a modern 1.8.9 client designed with philosophies of those that came before like Astolfo, Novoline and similar.<br>
With a focus on being open, yet effective - Sillicat is designed with the user in mind, rather than the utilitarian designs of past.<br>
When using Sillicat, the user will have a smooth, clean and appealing experience whilst retaining customisation of colors, sizes, animations and font size.<br>

## Planned Features
Going into the future, a configuration system (online), a design system along the lines of Novoline/Astolfo and a custom scripting system to allow users to write their own personal bypasses, potentially with the ability to upload them to the community on the online system.

## Rewrite Justification
The old Sillicat had a lot of skidded code, AI code or just plain bad code. The new rewrite plans to negate that by completely rewriting the code system in a modern, and effective way reducing overhead and increasing general efficiency, and ability to work with it, not only for the users but also for developers who wish to contribute.<br>

I personally recommend reviewing https://github.com/AmilieCoding/sillicat-dist/issues/1

## Contribute?
Contributions are more than welcome and are in fact appreciated as they all the client to flourish with a variety of different perspectives on issues. Can't code? That's fine! If you find *any* bugs, open an issue at https://github.com/AmilieCoding/sillicat-dist/issues and the developers (I), will get back to you as soon as possible with a remedy/fix and should an update be needed, an update.

## For developers
This client is developed currently on a pure Linux Gentoo x86 system - how it will play with Windows... I couldn't tell you. However, this is my configuration on KDE Wayland on Gentoo. I advise all developers attempt this configuration *before* making any changes.<br>

**Make sure your client SDK is set to 1.8, make it the exact same version of in step 2**<br>
Create a new launch application, call it what ever you want - Mine is called "Start Client".<br>
Set the Java version to "temurin-1.8" (You can use any distributor, I just prefer Temurin, I've noticed it works best for me personally.)<br>
Set the "main class" to "Start" - make **100%** sure it is "Default Package" and nothing else.<br>
Add the following string to your Program Arguments (It won't run without!) - `-Djava.library.path=./test_natives`<br>
And finally, on the end of working directory add "/test_run" to actually make the client run. You'll thank me later.<br>
You are also going to want to add a patch to the client. You can find the patch in /patches/, the patch is "OpenGlHelper.patch".<br>
You may need to amend this patch to play with your system. Please run `patch -p1 < /patches/OpenGlHelper.patch` to apply it to the client. Again, it may need modification.<br>
You should do this with every other patch to ensure the client runs appropriately. This is a legal compliance measure and will **NEVER** be changed, this system is an **unquestionable** system, regardless of convenience. As for Windows users - good luck.
