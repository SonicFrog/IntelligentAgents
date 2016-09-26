#!/bin/sh

set -eux

[[ -x "$(which java)" ]] || echo "Java not found!" && exit 1

java -jar build/libs/bouvier-rousset-in-standalone.jar
