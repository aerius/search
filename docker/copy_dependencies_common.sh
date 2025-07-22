#!/usr/bin/env bash

# Do not execute this script yourself, make use of the 'copy_dependencies.sh' scripts as present in the profile directories. e.g.: in directory 'nature'.

SOURCE_DIR='..'
DOCKER_COMMON_DIR='common'

# Exit on error
set -e

# Change current directory to directory of script so it can be called from everywhere
SCRIPT_PATH=$(readlink -f "${0}")
SCRIPT_DIR=$(dirname "${SCRIPT_PATH}")
cd "${SCRIPT_DIR}"

# include functions
source "${CICD_SCRIPTS_DIR}"/docker/images/v1.0/functions.sh

# Change current directory to previous one so scripts calling this one can function properly
cd - > /dev/null
