#!/bin/sh

set -eux

java -enableassertions -jar ../logist/logist.jar config/auction.xml auction-main-17 auction-random auction-random
