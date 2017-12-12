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

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.codice.ddf.internal.country.converter.api.CountryCodeConverter;
import org.codice.imaging.nitf.core.common.DateTime;
import org.codice.imaging.nitf.core.common.impl.DateTimeImpl;

public class NitfTestCommons {

  // This method is needed even though the NitfUtilties object created is not used. It will populate
  // the static CountryCodeConverter reference of the NitfUtilies for use in these tests
  public static void setupNitfUtilities(String fromCode, List<String> toCodes) {
    CountryCodeConverter mockCountryCodeConverter = mock(CountryCodeConverter.class);
    doReturn(toCodes).when(mockCountryCodeConverter).convertFipsToIso3(fromCode);
    new NitfUtilities(mockCountryCodeConverter);
  }

  public static DateTime createNitfDateTime(
      int year, int month, int dayOfMonth, int hour, int minute, int second) {
    DateTimeImpl dateTime = new DateTimeImpl();
    dateTime.set(
        ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, 0, ZoneId.of("UTC")));
    return dateTime;
  }
}
