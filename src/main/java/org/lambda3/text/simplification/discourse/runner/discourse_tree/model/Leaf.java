/*
 * ==========================License-Start=============================
 * DiscourseSimplification : Leaf
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

package org.lambda3.text.simplification.discourse.runner.discourse_tree.model;

import org.lambda3.text.simplification.discourse.utils.PrettyTreePrinter;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeException;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeExtractionUtils;
import org.lambda3.text.simplification.discourse.utils.parseTree.ParseTreeParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public abstract class Leaf<T> extends DiscourseTree {

    private T parseTree;
    private boolean allowSplit; // true, if extraction-rules will be applied on the text

    public Leaf() {
        super("UNKNOWN");
        this.allowSplit = true;
    }

    public Leaf(String extractionRule, T parseTree) {
        super(extractionRule);
        this.parseTree = parseTree;
        this.allowSplit = true;
    }

    // not efficient -> prefer to use constructor with parseTree
    public Leaf(String extractionRule, String text) throws ParseTreeException {
        super(extractionRule);
        this.parseTree = getParseTreeParser().parse(text);
        this.allowSplit = true;
    }

    protected abstract ParseTreeParser<T> getParseTreeParser();
    protected abstract ParseTreeExtractionUtils<T> getParseTreeExtractionUtils();


    public void dontAllowSplit() {
        this.allowSplit = false;
    }

    public T getParseTree() {
        return parseTree;
    }

    public String printParseTree() { return getParseTreeExtractionUtils().printParseTree(parseTree); }

    public void setParseTree(T parseTree) {
        this.parseTree = parseTree;
    }

    public String getText() {
        return getParseTreeExtractionUtils().getText(parseTree);
    }

    public boolean isAllowSplit() {
        return allowSplit;
    }

    // VISUALIZATION ///////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public List<String> getPTPCaption() {
        String simpleContextsStr = "[" + simpleContexts.stream().map(c -> "'" + c.getText() + "':" + c.getRelation()).collect(Collectors.joining(", ")) + "]";
        return Collections.singletonList("'" + getText() + "' " + simpleContextsStr);
    }

    @Override
    public List<PrettyTreePrinter.Edge> getPTPEdges() {
        return new ArrayList<>();
    }
}
