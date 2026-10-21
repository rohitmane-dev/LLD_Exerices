#!/usr/bin/env bash
# Compile every source file into out/ and run the demo with assertions on.
set -e
cd "$(dirname "$0")"
javac -d out $(find . -name '*.java')
java -ea -cp out Main
