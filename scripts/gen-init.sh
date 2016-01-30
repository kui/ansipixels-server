#!/bin/sh
set -eu
cd "$(dirname "$0")/.."

usage() {
    echo "Usage: $0 USERNAME"
}

if [ $# != 1 ]; then
    usage
    exit
fi

project_dir="$(pwd)"
user="$1"

cat <<EOF
#!/bin/sh
### BEGIN INIT INFO
# Provides:          ansipixel
# Required-Start:    \$network \$local_fs \$remote_fs
# Required-Stop:     \$network \$local_fs \$remote_fs
# Should-Start:      \$network
# Should-Stop:       \$network
# Default-Start:     2 3 4 5
# Default-Stop:      0 1 6
# Short-Description: start ansipixel server
### END INIT INFO

dir=$project_dir

usage() {
    echo "Usage: \$0 {start|stop|restart|status|update}"
}

set -eu

if [ \$# -eq 0 ]; then
    usage
    exit
fi

case "\$1" in
    start|stop|status)
        sudo -u$user "\${dir}/scripts/\$1"
        ;;
    restart)
        sudo -u$user "\${dir}/scripts/stop"
        sudo -u$user "\${dir}/scripts/start"
        ;;
    update)
        cd "\${dir}"
        sudo -u$user git pull
        sudo -u$user ./mvnw compile
        ;;
    *)
        usage;;
esac
EOF
