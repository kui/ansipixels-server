#!/bin/sh
set -eux
cd "$(dirname "$0")"

ln -s "$(pwd)/init.sh" /etc/init.d/ansipixels
