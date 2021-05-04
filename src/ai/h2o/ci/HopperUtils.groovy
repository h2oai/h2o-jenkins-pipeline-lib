package ai.h2o.ci
import com.cloudbees.groovy.cps.NonCPS

@NonCPS
public static String newNodeCond(String nodeCondition, String currentNode) {
    return "${nodeCondition} && !${currentNode}"
}

public static Closure retryHops(int maxRetries) {
    return { cnt, nodeCondition, currentNode ->
        if (cnt >= maxRetries) {
            throw new RuntimeException("Hopper reached retry limit ${maxRetries} >= ${cnt}...")
        }
        return newNodeCond(nodeCondition, currentNode)
    }
}

return this
