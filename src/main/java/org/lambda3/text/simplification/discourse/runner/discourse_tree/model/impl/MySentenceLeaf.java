/*
 * ==========================License-Start=============================
 * DiscourseSimplification : MySentenceLeaf
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

package org.lambda3.text.simplification.discourse.runner.discourse_tree.model.impl;

import org.lambda3.text.simplification.discourse.runner.discourse_tree.model.SentenceLeaf;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeParser;
import org.lambda3.text.simplification.discourse.utils.parseTree.impl.MyParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.parseTree.impl.MyParseTreeParser;

public class MySentenceLeaf extends SentenceLeaf {
    private static ParseTreeParser parseTreeParser = new MyParseTreeParser();
    private static ParseTreeExtractionUtils parseTreeExtractionUtils = new MyParseTreeExtractionUtils();

    public MySentenceLeaf(String sentence, int sentenceIdx) throws ParseTreeException {
        super(sentence, sentenceIdx);
    }

    @Override
    protected ParseTreeParser getParseTreeParser() {
        return parseTreeParser;
    }

    @Override
    protected ParseTreeExtractionUtils getParseTreeExtractionUtils() {
        return parseTreeExtractionUtils;
    }
}
