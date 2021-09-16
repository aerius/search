#!/usr/bin/env bash

SOURCE_DIR='..'

# Exit on error
set -e

# Change current directory to directory of script so it can be called from everywhere
SCRIPT_PATH=$(readlink -f "${0}")
SCRIPT_DIR=$(dirname "${SCRIPT_PATH}")
cd "${SCRIPT_DIR}"

# service
cp -auv "${SOURCE_DIR}"/search-service/target/search-service-*.jar \
        service/app.jar
