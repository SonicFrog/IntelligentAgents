#!/bin/sh

set -eux

[ -x "$(which java)" ] || (echo "Java not found!" && exit 1)

[ -x "$(which gradle)" ] || (echo "Gradle not found!" && exit 1)

find -name '*.java' | xargs -n 1 -P 4 javac -cp ../logist/logist.jar
mkdir -p bin
rsync -a --delete --exclude='*.java' src/ bin/
find src -not -name '*.java' -type f -delete
java -enableassertions -jar ../logist/logist.jar config/reactive.xml reactive-random
