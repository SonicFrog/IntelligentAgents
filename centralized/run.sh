#!/bin/sh

set -eux

java -enableassertions -jar ../logist/logist.jar config/centralized.xml centralized-random
