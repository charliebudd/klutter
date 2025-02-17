#!/bin/sh
#stop script on failure
set -e

cd ".."

echo " ____________
< Publishing Klutter Tasks module >
 ------------
        \   ^__^
         \  (oo)\_______
            (__)\       )\/\
                ||----w |
                ||     ||"

echo "\0/ Klutter: step: build Tasks module"
echo "------------------"
./gradlew clean -p "lib/klutter-tasks"
./gradlew build -p "lib/klutter-tasks"

echo "\0/ Klutter: step: publish tasks modules"
echo "------------------"
./gradlew publish -p "lib/klutter-tasks"