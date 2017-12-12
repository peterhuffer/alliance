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

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.commons.collections.CollectionUtils;
import org.codice.ddf.internal.country.converter.api.CountryCodeConverter;
import org.codice.imaging.nitf.core.common.DateTime;

/** General NITF utility functions */
public class NitfUtilities {

  private static CountryCodeConverter countryCodeConverter;

  @SuppressWarnings("squid:S1118" /* Used by Blueprint */)
  public NitfUtilities(CountryCodeConverter converter) {
    countryCodeConverter = converter;
  }

  /**
   * Gets the alpha3 country code for a fips country code by delegating to the {@link
   * CountryCodeConverter} service.
   *
   * @see CountryCodeConverter
   * @param fips The fips country code.
   * @return The alpha3 country code. Returns an empty list if fips = null, empty string, or the
   *     mapping doesn't exist.
   */
  public static List<String> fipsToAlpha3CountryCode(@Nullable String fips) {
    return countryCodeConverter.convertFipsToIso3(fips);
  }

  @Nullable
  public static Date convertNitfDate(@Nullable DateTime nitfDateTime) {
    if (nitfDateTime == null || nitfDateTime.getZonedDateTime() == null) {
      return null;
    }

    ZonedDateTime zonedDateTime = nitfDateTime.getZonedDateTime();
    Instant instant = zonedDateTime.toInstant();

    return Date.from(instant);
  }

  @Nullable
  public static String fipsToSingleIsoOrException(@Nullable String fipsCode) {
    List<String> countryCodes = countryCodeConverter.convertFipsToIso3(fipsCode);

    if (countryCodes.size() > 1) {
      throw new NitfParsingException(
          String.format(
              "Found %s while converting %s, but expected only 1 conversion value.",
              countryCodes, fipsCode),
          fipsCode);
    }

    if (CollectionUtils.isEmpty(countryCodes)) {
      return null;
    }
    return countryCodes.get(0);
  }
}
