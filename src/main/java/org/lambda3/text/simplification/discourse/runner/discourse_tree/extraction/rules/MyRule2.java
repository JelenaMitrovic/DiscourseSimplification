/*
 * ==========================License-Start=============================
 * DiscourseSimplification : MyRule2
 *
 * Copyright © 2018 Lambda³
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

package org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.rules;

import org.lambda3.text.simplification.discourse.model.SimpleContext;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.Relation;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.Extraction;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.extraction.ExtractionRule;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.Leaf;
import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.impl.MyLeaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class MyRule2 extends ExtractionRule {

    @Override
    public Optional<Extraction> extract(Leaf leaf) throws ParseTreeException {
        List<MyLeaf> constituents = new ArrayList<>();
        constituents.add(new MyLeaf(
                getClass().getSimpleName(), "Das ist ein erster Satz"
        ));
        constituents.add(new MyLeaf(
                getClass().getSimpleName(), "Das ist ein zweiter Satz"
        ));
        constituents.forEach(c -> c.dontAllowSplit());



        return Optional.of(new Extraction(
                getClass().getSimpleName(),
                false,
                null,
                Relation.UNKNOWN_COORDINATION,
                false,
                constituents.stream().collect(Collectors.toList())
        ));
    }
}
