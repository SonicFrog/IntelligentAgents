#!/bin/sh

set -eux

[[ -x "$(which java)" ]] || (echo "Java not found!" && exit 1)

[[ -x "$(which gradle)" ]] || (echo "Gradle not found!" && exit 1)

gradle buildJar
java -enableassertions -jar build/libs/bouvier-rousset-in-standalone.jar
