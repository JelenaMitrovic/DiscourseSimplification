/*
 * ==========================License-Start=============================
 * DiscourseExtraction : DiscourseExtractor
 *
 * Copyright © 2017 Lambda³
 *
 * GNU General Public License 3
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see http://www.gnu.org/licenses/.
 * ==========================License-End==============================
 */

package org.lambda3.text.simplification.discourse.runner.discourse_extraction;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import org.lambda3.text.simplification.discourse.model.SimpleContext;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.model.Element;
import org.lambda3.text.simplification.discourse.model.LinkedContext;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.Coordination;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.DiscourseTree;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.Leaf;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.Subordination;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class DiscourseExtractor {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<Relation> ignoredRelations;

    private final Config config;
    private LinkedHashMap<Leaf, Element> processedLeaves;

    public DiscourseExtractor(Config config) {
        this.config = config;

        // create ignored relations from config
        this.ignoredRelations = new ArrayList<>();
        for (String valueName : this.config.getStringList("ignored-relations")) {
            try {
                Relation relation = Relation.valueOf(valueName);
                ignoredRelations.add(relation);
            } catch (IllegalArgumentException e) {
                logger.error("Failed to create enum value of {}", valueName);
                throw new ConfigException.BadValue("ignored-relations." + valueName, "Failed to create enum value.");
            }
        }

        this.processedLeaves = new LinkedHashMap<Leaf, Element>();
    }

    public List<Element> doDiscourseExtraction(DiscourseTree discourseTree) {
        this.processedLeaves = new LinkedHashMap<Leaf, Element>();

        extractRec(discourseTree, 0);

        return processedLeaves.values().stream().collect(Collectors.toList());
    }

    private void AddAsLinkedContext(Leaf leaf, Leaf targetLeaf, Relation targetRelation) {
        Element leafElement = processedLeaves.get(leaf);

        // linked context
        Element targetElement = processedLeaves.get(targetLeaf);
        leafElement.addLinkedContext(new LinkedContext(targetElement.getId(), targetRelation));
    }

    private void addSimpleContexts(Leaf leaf, List<SimpleContext> simpleContexts) {
        Element leafElement = processedLeaves.get(leaf);

        // simple contexts
        simpleContexts.forEach(sc -> leafElement.addSimpleContext(sc));
    }

    private void extractRec(DiscourseTree node, int contextLayer) {

        if (node instanceof Leaf) {
            Leaf leaf = (Leaf)node;

            // create new element
            Element element = new Element(
                    leaf.getText(),
                    leaf.getSentenceIdx(),
                    contextLayer
            );
            leaf.getSimpleContexts().forEach(sc -> element.addSimpleContext(sc));

            processedLeaves.put(leaf, element);
        }

        if (node instanceof Coordination) {
            Coordination coordination = (Coordination) node;

            // recursion
            for (DiscourseTree child : coordination.getCoordinations()) {
                extractRec(child, contextLayer);
            }

            // set simple contexts
            coordination.getLeaves().forEach(l -> addSimpleContexts(l, node.getSimpleContexts()));

            // set links
            if (!ignoredRelations.contains(coordination.getRelation())) {
                for (DiscourseTree child : coordination.getCoordinations()) {
                    List<Leaf> childNLeaves = child.getCorePathLeaves();

                    // forward direction
                    for (DiscourseTree sibling : coordination.getOtherFollowingCoordinations(child)) {
                        List<Leaf> siblingNLeaves = sibling.getCorePathLeaves();

                        for (Leaf childNLeaf : childNLeaves) {
                            for (Leaf siblingNLeaf : siblingNLeaves) {
                                AddAsLinkedContext(childNLeaf, siblingNLeaf, coordination.getRelation());
                            }
                        }
                    }

                    // inverse direction
                    for (DiscourseTree sibling : coordination.getOtherPrecedingCoordinations(child)) {
                        List<Leaf> siblingNLeaves = sibling.getCorePathLeaves();

                        for (Leaf childNLeaf : childNLeaves) {
                            for (Leaf siblingNLeaf : siblingNLeaves) {
                                AddAsLinkedContext(childNLeaf, siblingNLeaf, coordination.getRelation().getInverseRelation());
                            }
                        }
                    }
                }
            }
        }

        if (node instanceof Subordination) {
            Subordination subordination = (Subordination) node;

            // recursion
            extractRec(subordination.getSuperordination(), contextLayer);
            extractRec(subordination.getSubordination(), contextLayer + 1);

            // set simple contexts
            subordination.getLeaves().forEach(l -> addSimpleContexts(l, node.getSimpleContexts()));

            // set links
            if (!ignoredRelations.contains(subordination.getRelation())) {
                List<Leaf> superordinationNLeaves = subordination.getSuperordination().getCorePathLeaves();
                List<Leaf> subordinationNLeaves = subordination.getSubordination().getCorePathLeaves();

                for (Leaf superordinationNLeaf : superordinationNLeaves) {
                    for (Leaf subordinationNLeaf : subordinationNLeaves) {
                        AddAsLinkedContext(superordinationNLeaf, subordinationNLeaf, subordination.getRelation());
                    }
                }
            }
        }
    }
}
