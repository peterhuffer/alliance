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

import ddf.catalog.data.Attribute;
import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.AttributeImpl;
import ddf.catalog.data.impl.BasicTypes;
import ddf.catalog.data.types.Validation;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.codice.alliance.transformer.nitf.ExtNitfUtility;
import org.codice.alliance.transformer.nitf.NitfParsingException;
import org.codice.imaging.nitf.core.common.TaggedRecordExtensionHandler;
import org.codice.imaging.nitf.core.tre.Tre;
import org.codice.imaging.nitf.core.tre.TreGroup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SegmentHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(SegmentHandler.class);

  protected <T> void handleSegmentHeader(
      Metacard metacard, T segment, List<NitfAttribute<T>> attributes) {
    attributes.forEach(attribute -> handleValue(metacard, attribute, segment));
  }

  protected <T> void handleSegmentHeader(Metacard metacard, T segment, NitfAttribute[] attributes) {
    handleSegmentHeader(metacard, segment, Arrays.asList(attributes));
  }

  protected void handleTres(
      Metacard metacard, TaggedRecordExtensionHandler taggedRecordextensionHandler) {
    List<Tre> tres = taggedRecordextensionHandler.getTREsRawStructure().getTREs();

    tres.forEach(
        tre ->
            Optional.ofNullable(TreDescriptor.forName(tre.getName().trim()))
                .ifPresent(treDescriptor -> handleTre(metacard, tre, treDescriptor.getValues())));
  }

  private <T> void handleTre(Metacard metacard, Tre tre, List<NitfAttribute<T>> treValues) {
    treValues.forEach(attribute -> handleTreValues(metacard, attribute, tre));
  }

  private void handleTreValues(Metacard metacard, NitfAttribute attribute, Tre tre) {
    NitfAttributeImpl treAttribute = (NitfAttributeImpl) attribute;
    List<NitfAttribute<TreGroup>> indexedAttributes = treAttribute.getIndexedAttributes();
    if (indexedAttributes != null && !indexedAttributes.isEmpty()) {
      List<TreGroup> treGroups = TreUtility.getTreGroups(tre, attribute.getShortName());
      if (treGroups != null) {
        treGroups.forEach(treGroup -> handleSegmentHeader(metacard, treGroup, indexedAttributes));
      }
    }
    handleValue(metacard, attribute, tre);
  }

  private <T> void handleValue(Metacard metacard, NitfAttribute attribute, T segment) {
    Function<T, Serializable> accessor = attribute.getAccessorFunction();

    Serializable value;
    try {
      value = accessor.apply(segment);
    } catch (NitfParsingException e) {
      LOGGER.debug(
          "Error accessing NITF attribute value. Skipping attribute [{}] on Metacard with ID [{}]",
          attribute.getLongName(),
          metacard.getId(),
          e);

      if (!attribute.getLongName().startsWith(ExtNitfUtility.EXT_NITF_PREFIX)) {
        handleBadAttribute(metacard, attribute, e.getOriginalValue());
      }

      return;
    }

    Set<AttributeDescriptor> descriptors = attribute.getAttributeDescriptors();

    if (descriptors == null) {
      LOGGER.debug(
          "Could not set metacard attribute {} since it does not belong to this metacard type.",
          attribute.getLongName());
      return;
    }

    for (AttributeDescriptor descriptor : descriptors) {
      if (descriptor.getType().equals(BasicTypes.STRING_TYPE)
          && value != null
          && value.toString().length() == 0) {
        value = null;
      }
    }

    for (AttributeDescriptor descriptor : descriptors) {
      if (value != null) {
        Attribute catalogAttribute = populateAttribute(metacard, descriptor.getName(), value);
        LOGGER.trace("Setting the metacard attribute [{}, {}]", descriptor.getName(), value);
        metacard.setAttribute(catalogAttribute);
      }
    }
  }

  private void handleBadAttribute(
      Metacard metacard, NitfAttribute attribute, Serializable originalValue) {
    Set<AttributeDescriptor> attributeDescriptors = attribute.getAttributeDescriptors();

    for (AttributeDescriptor descriptor : attributeDescriptors) {
      Attribute catalogAttribute = populateAttribute(metacard, descriptor.getName(), originalValue);
      metacard.setAttribute(catalogAttribute);
    }

    attachValidationWarning(metacard, attribute);
  }

  private void attachValidationWarning(Metacard metacard, NitfAttribute attribute) {
    Set<AttributeDescriptor> metacardAttrDescriptors =
        metacard.getMetacardType().getAttributeDescriptors();

    boolean hasValidationWarningAttribute =
        metacardAttrDescriptors
            .stream()
            .anyMatch(attr -> attr.getName().equals(Validation.VALIDATION_WARNINGS));

    if (hasValidationWarningAttribute) {
      String warningMessage =
          String.format(
              "Error while processing NITF attribute %s (%s). This NITF attribute was set to its original value and needs to be fixed manually.",
              attribute.getLongName(), attribute.getShortName());

      Attribute validationAttribute =
          populateAttribute(metacard, Validation.VALIDATION_WARNINGS, warningMessage);
      metacard.setAttribute(validationAttribute);
    } else {
      LOGGER.debug(
          "Error while setting attribute [{}], but no validation warnings attribute to set on metacard with id [{}].",
          attribute.getLongName(),
          metacard.getId());
    }
  }

  private Attribute populateAttribute(Metacard metacard, String attributeName, Serializable value) {
    Attribute currentAttribute = metacard.getAttribute(attributeName);

    if (currentAttribute == null) {
      currentAttribute = new AttributeImpl(attributeName, value);
    } else {
      AttributeImpl newAttribute = new AttributeImpl(currentAttribute);
      newAttribute.addValue(value);
      currentAttribute = newAttribute;
    }

    return currentAttribute;
  }
}
