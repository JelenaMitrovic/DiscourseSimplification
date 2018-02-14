/*
 * ==========================License-Start=============================
 * DiscourseSimplification : Extraction
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

package org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction;

import org.lambda3.text.simplification.discourse.model.SimpleContext;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.*;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 */
public class Extraction {
    private String extractionRule;
    private boolean referring;
    private String cuePhrase; // optional
    private Relation relation;
    private boolean contextRight;
    private List<Leaf> constituents;

    /***
     *
     * Creates a new extraction
     *
     * @param extractionRule The name of the extraction rule.
     * @param referring True, if the sentence refers to the previous unit. This requires the sentence to be the only constituent.
     * @param cuePhrase The cue phrase that indicates the relation. Can be null.
     * @param relation The classified relation.
     * @param contextRight In case of a subordination: is the right constituent contextual (True) or the left one (False).
     * @param constituents The (simplified) sentences.
     */
    public Extraction(String extractionRule, boolean referring, String cuePhrase, Relation relation, boolean contextRight, List<Leaf> constituents) {
        if ((referring) && (constituents.size() != 1)) {
            throw new AssertionError("Referring relations should have one constituent");
        }

        if ((!referring) && (!relation.isCoordination()) && (constituents.size() != 2)) {
            throw new AssertionError("(Non-referring) subordinate relations rules should have two constituents");
        }

        this.extractionRule = extractionRule;
        this.referring = referring;
        this.cuePhrase = (cuePhrase == null)? null : cuePhrase;
        this.relation = relation;
        this.contextRight = contextRight;
        this.constituents = constituents;
    }

    public Optional<DiscourseTree> generate(Leaf currChild) {

        if (relation.isCoordination()) {
            if (referring) {

                // find previous node to use as a reference
                Optional<DiscourseTree> prevNode = currChild.getPreviousNode();
                if ((prevNode.isPresent()) && (prevNode.get().usableAsReference())) {

                    // use prev node as a reference
                    prevNode.get().useAsReference();

                    Coordination res = new Coordination(
                        extractionRule,
                        relation,
                        cuePhrase,
                        Collections.emptyList(),
                        currChild.getSimpleContexts()
                    );
                    res.addCoordination(prevNode.get()); // set prev node as a reference
                    res.addCoordination(constituents.get(0));

                    return Optional.of(res);
                }
            } else {
                return Optional.of(new Coordination(
                    extractionRule,
                    relation,
                    cuePhrase,
                    constituents.stream().collect(Collectors.toList()),
                    currChild.getSimpleContexts()
                ));
            }
        } else {
            if (referring) {

                // find previous node to use as a reference
                Optional<DiscourseTree> prevNode = currChild.getPreviousNode();
                if ((prevNode.isPresent()) && (prevNode.get().usableAsReference())) {

                    // use prev node as a reference
                    prevNode.get().useAsReference();

                    Subordination res = new Subordination(
                        extractionRule,
                        relation,
                        cuePhrase,
                        new TmpLeaf(), // tmp
                        constituents.get(0),
                        contextRight,
                        currChild.getSimpleContexts()
                    );
                    res.replaceLeftConstituent(prevNode.get()); // set prev node as a reference

                    return Optional.of(res);
                }
            } else {
                return Optional.of(new Subordination(
                    extractionRule,
                    relation,
                    cuePhrase,
                    constituents.get(0),
                    constituents.get(1),
                    contextRight,
                    currChild.getSimpleContexts()
                ));
            }
        }

        return Optional.empty();
    }
}
