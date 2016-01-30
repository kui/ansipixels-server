PROJECT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
MVNW="${PROJECT_DIR}/mvnw"
PID_FILE="${PROJECT_DIR}/pid"
MAIN_CLASS="Main"
LOG_LEVEL="info"
PORT="60354"
STDOUT_LOG="log/stdout"
STDERR_LOG="log/stderr"
CURL="curl"

is_running() {
    i="$(pid)"
    if [ -n "$i" ] && kill -0 $i >/dev/null 2>&1; then
        return
    fi
    rm -f "$PID_FILE"
    return 1
}

pid() {
    [ -e "$PID_FILE" ] && cat "$PID_FILE"
}

abort() {
    echo Abort: "$@" >&2
    exit 1
}

wait_starting() {
    c=20
    while is_running && ! check_service; do
        echo -n .
        sleep 1
        c=$((c-1))

        if [ $c == 0 ]; then
            abort "Timeout to wait starting"
        fi
    done
    echo

    if ! is_running; then
        abort "Stopped"
    fi
}

check_service() {
    ${CURL} -s -o/dev/null "http://localhost:${PORT}"
}
