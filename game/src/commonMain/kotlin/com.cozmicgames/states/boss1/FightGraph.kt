package com.cozmicgames.states.boss1

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class FightGraph(private val boss: Boss1) {
    abstract class Node {
        lateinit var graph: FightGraph

        val boss get() = graph.boss

        var isDone = false
            private set

        private var isFirstUpdate = true

        open fun onBegin() {}

        open fun onUpdate(delta: Duration) {}

        open fun onEnd() {}

        fun setDone() {
            isDone = true
        }

        fun update(delta: Duration) {
            if (isFirstUpdate) {
                onBegin()
                isFirstUpdate = false
            }

            onUpdate(delta)
        }
    }

    private val nodes = arrayListOf<Node>()

    fun addNode(node: Node) {
        node.graph = this
        nodes += node
    }

    fun update(delta: Duration) {
        if (nodes.isEmpty())
            return

        val currentNode = nodes.first()

        if (!currentNode.isDone)
            currentNode.update(delta)
        else {
            currentNode.onEnd()
            nodes.removeAt(0)
        }
    }
}

class SequenceNode(private val nodes: List<FightGraph.Node>) : FightGraph.Node() {
    private var currentNode = 0

    override fun onUpdate(delta: Duration) {
        if (currentNode >= nodes.size) {
            setDone()
            return
        }

        nodes[currentNode].onUpdate(delta)

        if (nodes[currentNode].isDone) {
            currentNode++
        }
    }
}

class ParallelNode(private val nodes: List<FightGraph.Node>) : FightGraph.Node() {
    override fun onUpdate(delta: Duration) {
        var allDone = true

        for (node in nodes) {
            node.onUpdate(delta)

            if (!node.isDone) {
                allDone = false
            }
        }

        if (allDone) {
            setDone()
        }
    }
}

class WaitNode(private val duration: Duration) : FightGraph.Node() {
    private var timer = 0.0.seconds

    override fun onUpdate(delta: Duration) {
        timer += delta

        if (timer >= duration)
            setDone()
    }
}

class AttackNode(private val attack: Attack) : FightGraph.Node() {
    override fun onBegin() {
        boss.movementController.performAttack(attack) {
            setDone()
        }
    }
}
