package ai.h2o.ci

def pipe(command){
    sh(script: command, returnStdout: true)
}

return this
