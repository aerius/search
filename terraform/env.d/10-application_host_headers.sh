#!/usr/bin/env bash

# Crash on error
set -e

cat << EOF
  application_host_headers = {
    "SEARCH" = "${DEPLOY_WEBHOST}",
  }
EOF
