/*
 * ==========================License-Start=============================
 * DiscourseSimplification : SimplificationContent
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

package org.lambda3.text.simplification.discourse.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimplificationContent extends Content {
	private List<OutSentence> sentences;

	// for deserialization
	public SimplificationContent() {
	    this.sentences = new ArrayList<>();
	}

	public void addSentence(OutSentence sentence) {
	    this.sentences.add(sentence);
    }

    public void addElement(Element element) {
        sentences.get(element.getSentenceIdx()).addElement(element);
    }

    public List<OutSentence> getSentences() {
        return sentences;
    }

    public Element getElement(String id) {
        for (OutSentence sentence : sentences) {
            Element e = sentence.getElement(id);
            if (e != null) {
                return e;
            }
        }

        return null;
    }

    public List<Element> getElements() {
        List<Element> res = new ArrayList<>();
        sentences.forEach(s -> res.addAll(s.getElements()));

        return res;
    }

    public String defaultFormat(boolean resolve) {
	    StringBuilder strb = new StringBuilder();
        for (OutSentence outSentence : getSentences()) {
            strb.append("\n# " + outSentence.getOriginalSentence() + "\n");
            for (Element element : outSentence.getElements()) {
                String text = element.getTranslatedText().orElse(element.getText());
                strb.append("\n" + element.getId() + "\t" + element.getContextLayer() + "\t" + text + "\n");
                for (SimpleContext simpleContext : element.getSimpleContexts()) {
                    String cText = simpleContext.getTranslatedText().orElse(simpleContext.getText());
                    strb.append("\t" + "S:" + simpleContext.getRelation() + "\t" + cText + "\n");
                }
                for (LinkedContext linkedContext : element.getLinkedContexts()) {
                    if (resolve) {
                        Element target = getElement(linkedContext.getTargetID());
                        String targetText = target.getTranslatedText().orElse(target.getText());
                        strb.append("\t" + "L:" + linkedContext.getRelation() + "\t" + targetText+ "\n");
                    } else {
                        strb.append("\t" + "L:" + linkedContext.getRelation() + "\t" + linkedContext.getTargetID() + "\n");
                    }
                }
            }
        }

        return strb.toString();
    }

    public String flatFormat(boolean resolve) {
        StringBuilder strb = new StringBuilder();
        for (OutSentence outSentence : getSentences()) {
            for (Element element : outSentence.getElements()) {
                String text = element.getTranslatedText().orElse(element.getText());
                strb.append(outSentence.getOriginalSentence() + "\t" + element.getId() + "\t" + element.getContextLayer() + "\t" + text);
                for (SimpleContext simpleContext : element.getSimpleContexts()) {
                    String cText = simpleContext.getTranslatedText().orElse(simpleContext.getText());
                    strb.append("\t" + "S:" + simpleContext.getRelation() + "(" + cText + ")");
                }
                for (LinkedContext linkedContext : element.getLinkedContexts()) {
                    if (resolve) {
                        Element target = getElement(linkedContext.getTargetID());
                        String targetText = target.getTranslatedText().orElse(target.getText());
                        strb.append("\t" + "L:" + linkedContext.getRelation() + "(" + targetText + ")");
                    } else {
                        strb.append("\t" + "L:" + linkedContext.getRelation() + "(" + linkedContext.getTargetID() + ")");
                    }
                }
                strb.append("\n");
            }
        }

        return strb.toString();
    }

    @Override
    public String toString() {
        return getSentences().stream().map(s -> s.toString()).collect(Collectors.joining("\n"));
    }
}
