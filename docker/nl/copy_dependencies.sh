#!/usr/bin/env bash

SOURCE_DIR='../../'
DOCKER_COMMON_DIR='../common'

# Exit on error
set -e

# Change current directory to directory of script so it can be called from everywhere
SCRIPT_PATH=$(readlink -f "${0}")
SCRIPT_DIR=$(dirname "${SCRIPT_PATH}")
cd "${SCRIPT_DIR}"

# include functions
source "${CICD_SCRIPTS_DIR}"/docker/images/v1.0/functions.sh

# Also copy common dependencies
../copy_dependencies_common.sh "${@}"
