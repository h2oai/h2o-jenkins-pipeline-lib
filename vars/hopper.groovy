import static ai.h2o.ci.HopperUtils.retryHops

def call(String nodeCondition, Closure retryLogic, Closure body) {
    int cnt = 0
    String nextNodeCond = nodeCondition
    boolean done = false
    while (!done) {
        debug "Trying to run on node `${nextNodeCond}`, iteration: ${cnt}"
        node(nextNodeCond) {
            String currentNode = "${env.NODE_NAME}"
            try {
                body()
                done = true
            } catch (Exception e) {
                debug "Hopper body failed on `${currentNode}`, trying another node..."
                nextNodeCond = retryLogic(++cnt, nextNodeCond, currentNode)
                if (nextNodeCond == null) {
                    debug "nextNodeCond=${nextNodeCond}, done=${done}"
                    done = true
                }
            }
        }
    }
}

def call(String nodeCondition, int retries, Closure body) {
    call(nodeCondition, retryHops(retries), body)
}

