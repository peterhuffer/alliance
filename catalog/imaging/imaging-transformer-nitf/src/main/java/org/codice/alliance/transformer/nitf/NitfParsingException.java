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

import java.io.Serializable;

/**
 * A {@code RuntimeException} that is used when there are errors parsing a NITF attribute's value.
 * The {@code NitfParsingException} holds a reference to the original NITF attribute's value before
 * any transformation may have occurred.
 */
public class NitfParsingException extends RuntimeException {

  private final Serializable originalValue;

  public NitfParsingException(String s, Serializable originalValue) {
    super(s);
    this.originalValue = originalValue;
  }

  public Serializable getOriginalValue() {
    return originalValue;
  }
}
