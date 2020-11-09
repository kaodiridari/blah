1. Build, Debug, Run
----------------------- 
With eclipse (we want to debug ...):
https://openjfx.io/openjfx-docs/
short:
download sdk, create eclipse-user-libary with jars in sdk's lib folder
and in Run-configuration add vm-arguments
--module-path "C:\Users\user\Downloads\openjfx-15.0.1_windows-x64_bin-sdk\javafx-sdk-15.0.1\lib" --add-modules javafx.controls,javafx.fxml

With maven use the javafx-maven-plugin run with goals: clean javafx:run
Make sure you use same fx version in pom and eclipse.

On the Windows Path (Enviroment) must be ffmpeg.exe, because this is started via ProcessBuilder. 

For building the whole thing you need:
1. mvn package (eclipse: Run as maven build ... In the "goals" text-field type package)
2. open cmd.exe, change to target-folder enter in the commandline:
C:\Users\user\Documents\java\swing\blah\target>java --module-path "C:\Users\user\Downloads\openjfx-15.0.1_windows-x64_bin-sdk\javafx-sdk-15.0.1\lib" --add-modules javafx.controls,javafx.fxml,javafx.media -jar blah-0.0.1-SNAPSHOT-jar-with-dependencies.jar