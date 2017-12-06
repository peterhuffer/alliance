/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.alliance.transformer.nitf;

import java.nio.file.Paths;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.codice.ddf.platform.util.properties.PropertiesLoader;
import org.codice.imaging.nitf.core.common.DateTime;

public class NitfUtilities {

  private static final String FIPS_TO_ISO_3_PROPERTY_PATH =
      Paths.get(System.getProperty("karaf.etc"), "fipsToIso.properties").toString();

  private static final Map<String, String> FIPS_TO_ISO3_MAP = getFipsMap();

  private NitfUtilities() {}

  /**
   * Get the alpha3 country code for a fips country code.
   *
   * @param fips The fips country code.
   * @return The alpha3 country code. Returns null if fips = null, empty string, or the mapping
   *     doesn't exist.
   */
  public static String fipsToAlpha3CountryCode(String fips) {
    if (StringUtils.isEmpty(fips)) {
      return null;
    }
    return FIPS_TO_ISO3_MAP.get(fips);
  }

  private static Map<String, String> getFipsMap() {
    Map<String, String> map =
        PropertiesLoader.getInstance()
            .toMap(PropertiesLoader.getInstance().loadProperties(FIPS_TO_ISO_3_PROPERTY_PATH));
    // It is possible that there are multiple iso3 entries per fips entry.
    // If there are multiple iso3 entries, take only the first one.
    for (Map.Entry<String, String> entry : map.entrySet()) {
      entry.setValue(entry.getValue().split(",")[0]);
    }
    return map;
  }

  public static Date convertNitfDate(DateTime nitfDateTime) {
    if (nitfDateTime == null || nitfDateTime.getZonedDateTime() == null) {
      return null;
    }

    ZonedDateTime zonedDateTime = nitfDateTime.getZonedDateTime();
    Instant instant = zonedDateTime.toInstant();

    return Date.from(instant);
  }
}
