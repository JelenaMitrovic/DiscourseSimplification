/*
 * ==========================License-Start=============================
 * DiscourseSimplification : GeoFluentTranslator
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

import com.typesafe.config.Config;
import io.redlink.ssix.geofluent.GeoFluentClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 *
 */
public class GeofluentTranslator {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private GeoFluentClient geoFluentClient;
    private String language;
    private String english;

    public GeofluentTranslator(Config config) {
        // Load key and secret from config
        String key = config.getString("translator.geofluent.key");
        String secret = config.getString("translator.geofluent.secret");
        this.language = config.getString("translator.geofluent.language");
        this.english = "en-xn";

        this.geoFluentClient = new GeoFluentClient(key, secret);
        this.geoFluentClient.init();
        try {
            logger.debug("GeofluentTranslator initialized, supported languages: {}", this.geoFluentClient.languages());
        } catch (Exception e) {
            logger.error("Could not initialize GeofluentTranslator service", e);
            throw new TranslationException(e);
        }
    }

    public String doTranslate(String text, boolean reverse) {
        String translated;
        String from = (reverse)? english : language;
        String to = (reverse)? language : english;
        try {
            translated = this.geoFluentClient.translate(text, from, to);
        } catch (IOException e) {
            logger.error("IOException when translating '{}' from {} to {}",
                text, from, to);
            throw new TranslationException(e);
        } catch (URISyntaxException e) {
            logger.error("Could not send translation request to service.", e);
            throw new TranslationException(e);
        }

        return translated;
    }
}
