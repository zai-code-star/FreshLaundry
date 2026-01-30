package com.example.freshlaundry.admin.kurir

import org.osmdroid.util.GeoPoint
import java.util.*
import kotlin.math.*

data class Node(val point: GeoPoint, val g: Double, val h: Double, val parent: Node?) {
    val f: Double get() = g + h
}

class AStar(
    private val graph: Map<GeoPoint, List<GeoPoint>>,
    private val start: GeoPoint,
    private val end: GeoPoint
) {
    private lateinit var nearestStart: GeoPoint
    private lateinit var nearestEnd: GeoPoint

    private fun findNearestNodes(): Boolean {
        val candidates = graph.keys.toList()

        nearestStart = candidates.minByOrNull { it.distanceToAsDouble(start) } ?: return false
        nearestEnd = candidates.minByOrNull { it.distanceToAsDouble(end) } ?: return false

        return true
    }

    fun findPath(): List<GeoPoint> {
        if (!findNearestNodes()) return emptyList()

        val openList = PriorityQueue<Node>(compareBy { it.f })
        val cameFrom = mutableMapOf<GeoPoint, Node>()
        val gScore = mutableMapOf<GeoPoint, Double>().withDefault { Double.POSITIVE_INFINITY }

        val startNode = Node(nearestStart, 0.0, heuristic(nearestStart, nearestEnd), null)
        openList.add(startNode)
        gScore[nearestStart] = 0.0

        while (openList.isNotEmpty()) {
            val current = openList.poll()

            if (current.point == nearestEnd) return reconstructPath(current)

            val neighbors = graph[current.point] ?: continue
            for (neighbor in neighbors) {
                val tentativeG = gScore.getValue(current.point) + current.point.distanceToAsDouble(neighbor)
                if (tentativeG < gScore.getValue(neighbor)) {
                    gScore[neighbor] = tentativeG
                    val h = heuristic(neighbor, nearestEnd)
                    val neighborNode = Node(neighbor, tentativeG, h, current)
                    cameFrom[neighbor] = current
                    openList.add(neighborNode)
                }
            }
        }

        return emptyList()
    }

    private fun reconstructPath(node: Node): List<GeoPoint> {
        val path = mutableListOf<GeoPoint>()
        var current: Node? = node
        while (current != null) {
            path.add(current.point)
            current = current.parent
        }
        return path.reversed()
    }

    private fun heuristic(a: GeoPoint, b: GeoPoint): Double {
        // Haversine approximation (distance in meters)
        return a.distanceToAsDouble(b)
    }
}
