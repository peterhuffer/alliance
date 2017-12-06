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

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import org.codice.imaging.nitf.core.common.DateTime;
import org.codice.imaging.nitf.core.common.impl.DateTimeImpl;
import org.junit.Test;

public class NitfUtilitiesTest {

  @Test
  public void testValidFipsToIsoAlpha3() {
    // when
    String isoAlpha3Result = NitfUtilities.fipsToAlpha3CountryCode("US");

    // then
    assertThat(isoAlpha3Result, is("USA"));
  }

  @Test
  public void testInvalidFipsToIsoAlpha3() {
    // when
    String isoAlpha3Result = NitfUtilities.fipsToAlpha3CountryCode("NOT_VALID_FIPS");

    // then
    assertThat(isoAlpha3Result, is(nullValue()));
  }

  @Test
  public void testFipsToIsoAlpha3WithNullCode() {
    // when
    String isoAlpha3Result = NitfUtilities.fipsToAlpha3CountryCode(null);

    // then
    assertThat(isoAlpha3Result, is(nullValue()));
  }

  @Test
  public void testFipsToIsoAlpha3WithEmptyCode() {
    // when
    String isoAlpha3Result = NitfUtilities.fipsToAlpha3CountryCode("");

    // then
    assertThat(isoAlpha3Result, is(nullValue()));
  }

  @Test
  public void testConvertNitfDate() {
    // setup
    DateTime dateTime = createNitfDateTime(1997, 12, 17, 10, 26, 30);

    // when
    Date convertedDate = NitfUtilities.convertNitfDate(dateTime);

    // then
    assertThat(dateTime.getZonedDateTime().toInstant(), is(convertedDate.toInstant()));
  }

  @Test
  public void testConvertNitfDateWithNull() {
    // when
    Date convertedDate = NitfUtilities.convertNitfDate(null);

    // then
    assertThat(convertedDate, is(nullValue()));
  }

  @Test
  public void testConvertNitfDateWithNullZonedTime() {
    // setup
    DateTime mockDateTime = mock(DateTime.class);
    doReturn(null).when(mockDateTime).getZonedDateTime();

    // when
    Date convertedDate = NitfUtilities.convertNitfDate(mockDateTime);

    // then
    assertThat(convertedDate, is(nullValue()));
  }

  private static DateTime createNitfDateTime(
      int year, int month, int dayOfMonth, int hour, int minute, int second) {
    DateTimeImpl dateTime = new DateTimeImpl();
    dateTime.set(
        ZonedDateTime.of(year, month, dayOfMonth, hour, minute, second, 0, ZoneId.of("UTC")));
    return dateTime;
  }
}
