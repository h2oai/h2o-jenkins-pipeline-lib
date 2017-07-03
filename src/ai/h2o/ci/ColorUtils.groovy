package ai.h2o.ci

@NonCPS
static def red(s) {
    "\033[1;31m${s}\033[0m"
}

@NonCPS
static def yellow(s) {
    "\033[1;33m${s}\033[0m"
}

@NonCPS
static def green(s) {
    "\033[1;32m${s}\033[0m"
}

@NonCPS
static def blue(s) {
    "\033[1;34m${s}\033[0m"
}

@NonCPS
static def magenta(s) {
    "\033[1;35m${s}\033[0m"
}

@NonCPS
static def cyan(s) {
    "\033[1;36m${s}\033[0m"
}

return this
