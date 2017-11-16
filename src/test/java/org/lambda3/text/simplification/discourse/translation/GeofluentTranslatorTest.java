/*
 * ==========================License-Start=============================
 * DiscourseSimplification : GeofluentTranslatorTest
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

package org.lambda3.text.simplification.discourse.translation;

import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

/**
 *
 */
public class GeofluentTranslatorTest {
    private org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());
    private static GeofluentTranslator translator;

    @BeforeAll
    static void init() {
        translator  = new GeofluentTranslator(ConfigFactory.load().getConfig("discourse-simplification"));
    }

    @Test
    void testTranslation() {
        String input = "Heute scheint die Sonne.";
        log.info("Input           '{}'", input);

        String translated = translator.doTranslate(input, false);
        log.info("Translated      '{}'", translated);

        String back = translator.doTranslate(translated, true);
        log.info("Translated back '{}'", back);
    }
}
