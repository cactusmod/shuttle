/*******************************************************************************
 * Copyright (c) 2025 Cactus Team.
 * This code is part of the Cactus-Client distribution. All Rights reserved.
 ******************************************************************************/

package com.dwarslooper.cactus.shuttle.util;

import com.dwarslooper.cactus.shuttle.Shuttle;
import com.dwarslooper.cactus.shuttle.addon.ShuttleAddon;
import com.dwarslooper.cactus.shuttle.annotation.Depends;
import com.dwarslooper.cactus.shuttle.annotation.LoadBefore;
import com.dwarslooper.cactus.shuttle.handler.IAddonHandler;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;

import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class DependencySorter {

    public static List<ShuttleAddon> sortAddonsByDependency(IAddonHandler handler) {
        MutableGraph<String> graph = GraphBuilder.directed().allowsSelfLoops(false).build();
        Map<String, ShuttleAddon> addonMap = new HashMap<>();

        Collection<ShuttleAddon> addons = handler.getAddons();

        for (ShuttleAddon addon : addons) {
            graph.addNode(addon.id());
            addonMap.put(addon.id(), addon);
        }

        for (ShuttleAddon addon : addons) {
            Class<?> clazz = addon.lifecycle().getClass();

            Depends pre = clazz.getAnnotation(Depends.class);
            if (pre != null) {
                for (String dep : pre.value()) {
                    if (graph.nodes().contains(dep)) {
                        graph.putEdge(dep, addon.id()); // dep must load before addon
                    } else {
                        Shuttle.getLogger().warn("Addon '{}' depends on '{}', but said mod / addon is not present", addon.id(), dep);
                    }
                }
            }

            LoadBefore post = clazz.getAnnotation(LoadBefore.class);
            if (post != null) {
                for (String dep : post.value()) {
                    if (graph.nodes().contains(dep)) {
                        graph.putEdge(addon.id(), dep); // addon must load before dep
                    }
                }
            }
        }

        List<String> sortedIds = DependencySorter.topologicalSort(graph);
        List<ShuttleAddon> sorted = new ArrayList<>(sortedIds.size());

        for (String id : sortedIds) {
            sorted.add(addonMap.get(id));
        }

        return sorted;
    }

    public static <T> List<T> topologicalSort(Graph<T> graph) {
        Map<T, Set<T>> successors = new HashMap<>();

        for (T node : graph.nodes()) {
            Set<T> successor = graph.successors(node);
            successors.put(node, successor.isEmpty() ? ImmutableSet.of() : new HashSet<>(successor));
        }

        List<T> result = new ArrayList<>();
        Set<T> visited = new HashSet<>();
        Set<T> visiting = new HashSet<>();

        for (T node : graph.nodes()) {
            if (sort(successors, visited, visiting, result::add, node)) {
                throw new IllegalStateException("Cycle detected in dependencies involving '%s'".formatted(node));
            }
        }

        return result;
    }

    public static <T> boolean sort(Map<T, Set<T>> successors, Set<T> visited, Set<T> visiting, Consumer<T> reversedOrderConsumer, T now) {
        if (visited.contains(now)) {
            return false;
        } else if (visiting.contains(now)) {
            return true;
        } else {
            visiting.add(now);

            for(T object : successors.getOrDefault(now, ImmutableSet.of())) {
                if (sort(successors, visited, visiting, reversedOrderConsumer, object)) {
                    return true;
                }
            }

            visiting.remove(now);
            visited.add(now);
            reversedOrderConsumer.accept(now);
            return false;
        }
    }

}
