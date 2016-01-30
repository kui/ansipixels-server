#!/bin/sh

### BEGIN INIT INFO
# Provides:          ansipixel
# Required-Start:    $network $local_fs $remote_fs
# Required-Stop:     $network $local_fs $remote_fs
# Should-Start:      $network
# Should-Stop:       $network
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: start ansipixel server
### END INIT INFO

user="www-data"
group="www-data"

usage() {
    echo "Usage: $0 {start|stop|status}"
}

set -eu
cd "$(dirname $(readlink -f "$0"))/.."

if [ $# -eq 0 ]; then
    usage
    exit
fi

case "$1" in
    start|stop|status)
        sudo -u$user -g$group "./scripts/$1";;
    *)
        usage;;
esac
