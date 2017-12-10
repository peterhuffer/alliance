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
package org.codice.alliance.transformer.nitf.common;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import org.codice.alliance.transformer.nitf.NitfUtilities;
import org.codice.ddf.internal.country.converter.api.CountryCodeConverter;
import org.codice.imaging.nitf.core.header.NitfHeader;
import org.codice.imaging.nitf.core.security.FileSecurityMetadata;
import org.junit.Before;
import org.junit.Test;

public class NitfHeaderAttributeTest {

  private NitfHeader nitfHeader;

  @Before
  public void setup() {
    nitfHeader = mock(NitfHeader.class);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMultipleConvertedCountryCodesForFileClassificationSystem() throws Exception {
    setupNitfUtilies("US", Arrays.asList("ABC", "XYZ"));

    FileSecurityMetadata fsmMock = mock(FileSecurityMetadata.class);
    when(nitfHeader.getFileSecurityMetadata()).thenReturn(fsmMock);
    when(nitfHeader.getFileSecurityMetadata().getSecurityClassificationSystem()).thenReturn("US");
    NitfHeaderAttribute.FILE_CLASSIFICATION_SECURITY_SYSTEM_ATTRIBUTE
        .getAccessorFunction()
        .apply(nitfHeader);
  }

  // This method is needed even though the NitfUtilties object created is not used. It will populate
  // the static CountryCodeConverter reference of the NitfUtilies for use in these tests
  private void setupNitfUtilies(String fromCode, List<String> toCodes) {
    CountryCodeConverter mockCountryCodeConverter = mock(CountryCodeConverter.class);
    doReturn(toCodes).when(mockCountryCodeConverter).convertFipsToIso3(fromCode);
    new NitfUtilities(mockCountryCodeConverter);
  }
}
