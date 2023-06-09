#!/bin/bash

# Terminate on errors
set -e

printf "Synchronising submodules... "
git submodule sync --recursive >> /dev/null
git submodule update --recursive --remote --init >> /dev/null
printf "DONE\n\n"

file="./protos/jellyfish/peer_notifications.proto"

printf "Compiling: file $file\n"
protoc -I=./protos --java_out=JellyfishClient/src/main/java/ --kotlin_out=JellyfishClient/src/main/java/ $file
printf "DONE\n"
