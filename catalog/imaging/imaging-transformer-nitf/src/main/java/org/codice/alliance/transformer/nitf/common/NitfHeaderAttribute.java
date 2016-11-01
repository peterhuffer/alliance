/**
 * Copyright (c) Codice Foundation
 * <p/>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.alliance.transformer.nitf.common;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.codice.alliance.catalog.core.api.impl.types.IsrAttributes;
import org.codice.alliance.catalog.core.api.impl.types.SecurityAttributes;
import org.codice.alliance.catalog.core.api.types.Isr;
import org.codice.alliance.catalog.core.api.types.Security;
import org.codice.alliance.transformer.nitf.ExtNitfUtility;
import org.codice.imaging.nitf.core.common.DateTime;
import org.codice.imaging.nitf.core.header.NitfHeader;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.AttributeType;
import ddf.catalog.data.Metacard;
import ddf.catalog.data.impl.AttributeDescriptorImpl;
import ddf.catalog.data.impl.BasicTypes;
import ddf.catalog.data.impl.types.ContactAttributes;
import ddf.catalog.data.impl.types.CoreAttributes;
import ddf.catalog.data.impl.types.MediaAttributes;
import ddf.catalog.data.types.Contact;
import ddf.catalog.data.types.Core;
import ddf.catalog.data.types.Media;

public class NitfHeaderAttribute extends NitfAttributeImpl<NitfHeader> {

    private static final String PREFIX = ExtNitfUtility.EXT_NITF_PREFIX;

    public static final String FILE_PROFILE_NAME_NAME = PREFIX + "file-profile-name";

    public static final String FILE_VERSION_NAME = PREFIX + "file-version";

    public static final String ORIGINATING_STATION_ID_NAME = PREFIX + "originating-station-id";

    public static final String FILE_TITLE_NAME = PREFIX + "file-title";

    public static final String FILE_DATE_AND_TIME_NAME = PREFIX + "file-date-and-time";

    public static final String FILE_SECURITY_CLASSIFICATION_NAME =
            PREFIX + "file-security-classification";

    public static final String FILE_CLASSIFICATON_SECURITY_SYSTEM_NAME =
            PREFIX + "file-classification-security-system";

    public static final String FILE_CODEWORDS_NAME = PREFIX + "file-codewords";

    public static final String FILE_CONTROL_AND_HANDLING_NAME =
            PREFIX + "file-control-and-handling";

    public static final String FILE_RELEASING_INSTRUCTIONS_NAME =
            PREFIX + "file-releasing-instructions";

    public static final String ORIGINATORS_NAME_NAME = PREFIX + "originators-name";

    public static final String ORIGINATORS_PHONE_NUMBER_NAME = PREFIX + "originators-phone-number";

    public static final String COMPLEXITY_LEVEL_NAME = PREFIX + "complexity-level";

    public static final String STANDARD_TYPE_NAME = PREFIX + "standard-type";

    public static final String FILE_DECLASSIFICATION_EXEMPTION_NAME =
            PREFIX + "file-declassification-exemption";

    public static final String FILE_DECLASSIFICATION_TYPE_NAME =
            PREFIX + "file-declassification-type";

    public static final String FILE_DECLASSIFICATION_DATE_NAME =
            PREFIX + "file-declassification-date";

    public static final String FILE_DOWNGRADE_NAME = PREFIX + "file-downgrade";

    public static final String FILE_DOWNGRADE_DATE_NAME = PREFIX + "file-downgrade-date";

    public static final String FILE_CLASSIFICATION_TEXT_NAME = PREFIX + "file-classification-text";

    public static final String FILE_CLASSIFICATION_AUTHORITY_TYPE_NAME =
            PREFIX + "file-classification-authority-type";

    public static final String FILE_CLASSIFICATION_AUTHORITY_NAME =
            PREFIX + "file-classification-authority";

    public static final String FILE_CLASSIFICATION_REASON_NAME =
            PREFIX + "file-classification-reason";

    public static final String FILE_SECURITY_SOURCE_DATE_NAME =
            PREFIX + "file-security-source-date";

    public static final String FILE_SECURITY_CONTROL_NUMBER_NAME =
            PREFIX + "file-security-control-number";

    public static final String FILE_COPY_NUMBER_NAME = PREFIX + "file-copy-number";

    public static final String FILE_NUMBER_OF_COPIES_NAME = PREFIX + "file-number-of-copies";

    public static final String FILE_BACKGROUND_COLOR_NAME = PREFIX + "file-background-color";

    private static final List<NitfAttribute<NitfHeader>> ATTRIBUTES = new LinkedList<>();

    /*
     * Normalized attributes. These taxonomy terms will be duplicated by `ext.nitf.*` when
     * appropriate
     */

    public static final NitfHeaderAttribute FILE_PROFILE_NAME =
            new NitfHeaderAttribute(Media.FORMAT,
                    "FHDR",
                    header -> header.getFileType()
                            .name(),
                    new MediaAttributes().getAttributeDescriptor(Media.FORMAT),
                    FILE_PROFILE_NAME_NAME);

    public static final NitfHeaderAttribute FILE_VERSION =
            new NitfHeaderAttribute(Media.FORMAT_VERSION,
                    "FVER",
                    header -> header.getFileType()
                            .name(),
                    new MediaAttributes().getAttributeDescriptor(Media.FORMAT),
                    FILE_VERSION_NAME);

    public static final NitfHeaderAttribute ORIGINATING_STATION_ID =
            new NitfHeaderAttribute(Isr.ORGANIZATIONAL_UNIT,
                    "OSTAID",
                    NitfHeader::getOriginatingStationId,
                    new IsrAttributes().getAttributeDescriptor(Isr.ORGANIZATIONAL_UNIT),
                    ORIGINATING_STATION_ID_NAME);

    public static final NitfHeaderAttribute FILE_TITLE = new NitfHeaderAttribute(Core.TITLE,
            "FTITLE",
            NitfHeader::getFileTitle,
            new CoreAttributes().getAttributeDescriptor(Core.TITLE),
            FILE_TITLE_NAME);

    public static final NitfHeaderAttribute FILE_DATE_AND_TIME_CREATED = new NitfHeaderAttribute(
            Core.CREATED,
            "FDT",
            header -> convertNitfDate(header.getFileDateTime()),
            new CoreAttributes().getAttributeDescriptor(Core.CREATED),
            FILE_DATE_AND_TIME_NAME);

    public static final NitfHeaderAttribute FILE_DATE_AND_TIME_MODIFIED = new NitfHeaderAttribute(
            Core.MODIFIED,
            "FDT",
            header -> convertNitfDate(header.getFileDateTime()),
            new CoreAttributes().getAttributeDescriptor(Core.MODIFIED),
            "");

    public static final NitfHeaderAttribute FILE_DATE_AND_TIME_EFFECTIVE = new NitfHeaderAttribute(
            Metacard.EFFECTIVE,
            "FDT",
            header -> convertNitfDate(header.getFileDateTime()),
            new AttributeDescriptorImpl(Metacard.EFFECTIVE, true, /* indexed */
                    true, /* stored */
                    false, /* tokenized */
                    false, /* multivalued */
                    BasicTypes.DATE_TYPE),
            "");

    public static final NitfHeaderAttribute FILE_SECURITY_CLASSIFICATION = new NitfHeaderAttribute(
            Security.CLASSIFICATION,
            "FSCLAS",
            header -> header.getFileSecurityMetadata()
                    .getSecurityClassification()
                    .name(),
            new SecurityAttributes().getAttributeDescriptor(Security.CLASSIFICATION),
            FILE_SECURITY_CLASSIFICATION_NAME);

    public static final NitfHeaderAttribute FILE_CLASSIFICATION_SECURITY_SYSTEM =
            new NitfHeaderAttribute(Security.CLASSIFICATION_SYSTEM,
                    "FSCLSY",
                    header -> header.getFileSecurityMetadata()
                            .getSecurityClassificationSystem(),
                    new SecurityAttributes().getAttributeDescriptor(Security.CLASSIFICATION_SYSTEM),
                    FILE_CLASSIFICATON_SECURITY_SYSTEM_NAME);

    public static final NitfHeaderAttribute FILE_CODE_WORDS =
            new NitfHeaderAttribute(Security.CODEWORDS,
                    "FSCODE",
                    header -> header.getFileSecurityMetadata()
                            .getCodewords(),
                    new SecurityAttributes().getAttributeDescriptor(Security.CODEWORDS),
                    FILE_CODEWORDS_NAME);

    public static final NitfHeaderAttribute FILE_CONTROL_AND_HANDLING = new NitfHeaderAttribute(
            Security.DISSEMINATION_CONTROLS,
            "FSCTLH",
            header -> header.getFileSecurityMetadata()
                    .getControlAndHandling(),
            new SecurityAttributes().getAttributeDescriptor(Security.DISSEMINATION_CONTROLS),
            FILE_CONTROL_AND_HANDLING_NAME);

    public static final NitfHeaderAttribute FILE_RELEASING_INSTRUCTIONS = new NitfHeaderAttribute(
            Security.RELEASABILITY,
            "FSREL",
            header -> header.getFileSecurityMetadata()
                    .getReleaseInstructions(),
            new SecurityAttributes().getAttributeDescriptor(Security.RELEASABILITY),
            FILE_RELEASING_INSTRUCTIONS_NAME);

    public static final NitfHeaderAttribute ORIGINATORS_NAME =
            new NitfHeaderAttribute(Contact.CREATOR_NAME,
                    "ONAME",
                    NitfHeader::getOriginatorsName,
                    new ContactAttributes().getAttributeDescriptor(Contact.CREATOR_NAME),
                    ORIGINATORS_NAME_NAME);

    public static final NitfHeaderAttribute ORIGINATORS_PHONE_NUMBER = new NitfHeaderAttribute(
            Contact.CREATOR_PHONE,
            "OPHONE",
            NitfHeader::getOriginatorsPhoneNumber,
            new ContactAttributes().getAttributeDescriptor(Contact.CREATOR_PHONE),
            ORIGINATORS_PHONE_NUMBER_NAME);

    /*
     * Non normalized attributes
     */

    public static final NitfHeaderAttribute COMPLEXITY_LEVEL = new NitfHeaderAttribute(
            COMPLEXITY_LEVEL_NAME,
            "CLEVEL",
            NitfHeader::getComplexityLevel,
            BasicTypes.INTEGER_TYPE);

    public static final NitfHeaderAttribute FILE_DATE_AND_TIME = new NitfHeaderAttribute(
            FILE_DATE_AND_TIME_NAME,
            "FDT",
            header -> convertNitfDate(header.getFileDateTime()),
            BasicTypes.DATE_TYPE);

    public static final NitfHeaderAttribute STANDARD_TYPE = new NitfHeaderAttribute(
            STANDARD_TYPE_NAME,
            "STYPE",
            NitfHeader::getStandardType,
            BasicTypes.STRING_TYPE);

    public static final NitfHeaderAttribute FILE_DECLASSIFICATION_EXEMPTION =
            new NitfHeaderAttribute(FILE_DECLASSIFICATION_EXEMPTION_NAME,
                    "FSDCXM",
                    header -> header.getFileSecurityMetadata()
                            .getDeclassificationExemption(),
                    BasicTypes.STRING_TYPE);

    public static final NitfHeaderAttribute FILE_DECLASSIFICATION_TYPE = new NitfHeaderAttribute(
            FILE_DECLASSIFICATION_TYPE_NAME,
            "FSDCTP",
            header -> header.getFileSecurityMetadata()
                    .getDeclassificationType(),
            BasicTypes.STRING_TYPE);

    public static final NitfHeaderAttribute FILE_DECLASSIFICATION_DATE = new NitfHeaderAttribute(
            FILE_DECLASSIFICATION_DATE_NAME,
            "FSDCDT",
            header -> header.getFileSecurityMetadata()
                    .getDeclassificationDate(),
            BasicTypes.STRING_TYPE);

    public static final NitfHeaderAttribute FILE_DOWNGRADE = new NitfHeaderAttribute(
            FILE_DOWNGRADE_NAME,
            "FSDG",
            header -> header.getFileSecurityMetadata()
                    .getDowngrade(),
            BasicTypes.STRING_TYPE);

    public static final NitfHeaderAttribute FILE_DOWNGRADE_DATE = new NitfHeaderAttribute(
            FILE_DOWNGRADE_DATE_NAME,
            "FSDGDT",
            header -> header.getFileSecurityMetadata()
                    .getDowngradeDate(),
            BasicTypes.STRING_TYPE);

    public static final NitfHeaderAttribute FILE_CLASSIFICATION_TEXT = new NitfHeaderAttribute(
            FILE_CLASSIFICATION_TEXT_NAME,
            "FSCLTX",
            header -> header.getFileSecurityMetadata()
                    .getClassificationText(),
            BasicTypes.STRING_TYPE);

    public static final NitfHeaderAttribute FILE_CLASSIFICATION_AUTHORITY_TYPE =
            new NitfHeaderAttribute(FILE_CLASSIFICATION_AUTHORITY_TYPE_NAME,
                    "FSCATP",
                    header -> header.getFileSecurityMetadata()
                            .getClassificationAuthorityType(),
                    BasicTypes.STRING_TYPE);

    public static final NitfHeaderAttribute FILE_CLASSIFICATION_AUTHORITY = new NitfHeaderAttribute(
            FILE_CLASSIFICATION_AUTHORITY_NAME,
            "FSCAUT",
            header -> header.getFileSecurityMetadata()
                    .getClassificationAuthority(),
            BasicTypes.STRING_TYPE);

    public static final NitfHeaderAttribute FILE_CLASSIFICATION_REASON = new NitfHeaderAttribute(
            FILE_CLASSIFICATION_REASON_NAME,
            "FSCRSN",
            header -> header.getFileSecurityMetadata()
                    .getClassificationReason(),
            BasicTypes.STRING_TYPE);

    public static final NitfHeaderAttribute FILE_SECURITY_SOURCE_DATE = new NitfHeaderAttribute(
            FILE_SECURITY_SOURCE_DATE_NAME,
            "FSSRDT",
            header -> header.getFileSecurityMetadata()
                    .getSecuritySourceDate(),
            BasicTypes.STRING_TYPE);

    public static final NitfHeaderAttribute FILE_SECURITY_CONTROL_NUMBER = new NitfHeaderAttribute(
            FILE_SECURITY_CONTROL_NUMBER_NAME,
            "FSCTLN",
            header -> header.getFileSecurityMetadata()
                    .getSecurityControlNumber(),
            BasicTypes.STRING_TYPE);

    public static final NitfHeaderAttribute FILE_COPY_NUMBER = new NitfHeaderAttribute(
            FILE_COPY_NUMBER_NAME,
            "FSCOP",
            header -> header.getFileSecurityMetadata()
                    .getFileCopyNumber(),
            BasicTypes.STRING_TYPE);

    public static final NitfHeaderAttribute FILE_NUMBER_OF_COPIES = new NitfHeaderAttribute(
            FILE_NUMBER_OF_COPIES_NAME,
            "FSCPYS",
            header -> header.getFileSecurityMetadata()
                    .getFileNumberOfCopies(),
            BasicTypes.STRING_TYPE);

    public static final NitfHeaderAttribute FILE_BACKGROUND_COLOR = new NitfHeaderAttribute(
            FILE_BACKGROUND_COLOR_NAME,
            "FBKGC",
            header -> header.getFileBackgroundColour() != null ?
                    header.getFileBackgroundColour()
                            .toString() :
                    "",
            BasicTypes.STRING_TYPE);

    private NitfHeaderAttribute(String longName, String shortName,
            Function<NitfHeader, Serializable> accessorFunction, AttributeType attributeType) {
        super(longName, shortName, accessorFunction, attributeType);
        ATTRIBUTES.add(this);
    }

    private NitfHeaderAttribute(String longName, String shortName,
            Function<NitfHeader, Serializable> accessorFunction,
            AttributeDescriptor attributeDescriptor, String extNitfName) {
        super(longName, shortName, accessorFunction, attributeDescriptor, extNitfName);
        ATTRIBUTES.add(this);
    }

    private static Date convertNitfDate(DateTime nitfDateTime) {
        if (nitfDateTime == null || nitfDateTime.getZonedDateTime() == null) {
            return null;
        }

        ZonedDateTime zonedDateTime = nitfDateTime.getZonedDateTime();
        Instant instant = zonedDateTime.toInstant();

        if (instant != null) {
            return Date.from(instant);
        }

        return null;
    }

    public static List<NitfAttribute<NitfHeader>> getAttributes() {
        return Collections.unmodifiableList(ATTRIBUTES);
    }
}
