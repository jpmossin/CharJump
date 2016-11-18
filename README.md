## CharJump

An IntelliJ plugin for quickly jumping to any visible character in the active document.

Inspired by and similar to https://github.com/johnlindquist/AceJump, but with a stronger focus on
making jumping to a specific character/position as easy and fast as possible.

Should work in all editors based on the Intellij platform. 
Install by unzipping a [distribution](https://github.com/jpmossin/charjump/releases) into your plugins folder 
,e.g ~/.IntelliJIdea2016.2/config/plugins/ (Will be available through the standard Jetbrains plugin repo once the plugin
has been approved by Jetbrains).


##### Usage
Activate with Ctrl+Comma (configurable) and enter the character for the position you wish to jump to.
A single-character label will then be shown for each matching position in the document. <br>

The below image shows an example of searching for "o", with the caret located at line 23: <br>
![charjump.png](https://github.com/jpmossin/CharJump/raw/master/charjump.png)

Positions close to the current caret position can be reached by a single character,
while a two (or in extreme cases three) characters are needed for positions further away when there are
many matching positions.
 
After entering a target character, the jump can be aborted by pressing Space.  

##### Development
Build with:
```
./gradlew buildPlugin
```
Start up a local sandboxed IDEA with the plugin running using: 
```
./gradlew runIdea
```
