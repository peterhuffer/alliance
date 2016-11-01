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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

import org.codice.alliance.catalog.core.api.impl.types.IsrAttributes;
import org.codice.alliance.catalog.core.api.types.Isr;
import org.codice.alliance.transformer.nitf.ExtNitfUtility;
import org.codice.imaging.nitf.core.tre.Tre;

import ddf.catalog.data.AttributeDescriptor;
import ddf.catalog.data.AttributeType;
import ddf.catalog.data.impl.BasicTypes;

public class AcftbAttribute extends NitfAttributeImpl<Tre> {

    private static final String ATTRIBUTE_NAME_PREFIX = ExtNitfUtility.EXT_NITF_PREFIX + "acftb.";

    public static final String AIRCRAFT_MISSION_ID_NAME =
            ATTRIBUTE_NAME_PREFIX + "aircraft-mission-id";

    public static final String AIRCRAFT_TAIL_NUMBER_NAME =
            ATTRIBUTE_NAME_PREFIX + "aircraft-tail-number";

    public static final String SENSOR_ID_TYPE_NAME = ATTRIBUTE_NAME_PREFIX + "sensor-id-type";

    public static final String SENSOR_ID_NAME = ATTRIBUTE_NAME_PREFIX + "sensor-id";

    public static final String AIRCRAFT_TAKEOFF_NAME = ATTRIBUTE_NAME_PREFIX + "aircraft-take-off";

    public static final String SCENE_SOURCE_NAME = ATTRIBUTE_NAME_PREFIX + "scene-source";

    public static final String SCENE_NUMBER_NAME = ATTRIBUTE_NAME_PREFIX + "scene-number";

    public static final String PROCESSING_DATE_NAME = ATTRIBUTE_NAME_PREFIX + "processing-date";

    public static final String IMMEDIATE_SCENE_HOST_NAME =
            ATTRIBUTE_NAME_PREFIX + "immediate-scene-host";

    public static final String IMMEDIATE_SCENE_REQUEST_ID_NAME =
            ATTRIBUTE_NAME_PREFIX + "immediate-scene-request-id";

    public static final String MISSION_PLAN_MODE_NAME = ATTRIBUTE_NAME_PREFIX + "mission-plan-mode";

    private static final List<NitfAttribute<Tre>> ATTRIBUTES = new LinkedList<>();

    /*
     * Normalized attributes. These taxonomy terms will be duplicated by `ext.nitf.acftb.*` when
     * appropriate
     */

    public static final AcftbAttribute AIRCRAFT_MISSION_ID = new AcftbAttribute(Isr.MISSION_ID,
            "AC_MSN_ID",
            tre -> TreUtility.getTreValue(tre, "AC_MSN_ID"),
            new IsrAttributes().getAttributeDescriptor(Isr.MISSION_ID),
            AIRCRAFT_MISSION_ID_NAME);

    public static final AcftbAttribute AIRCRAFT_TAIL_NUMBER = new AcftbAttribute(Isr.PLATFORM_ID,
            "AC_TAIL_NO",
            tre -> TreUtility.getTreValue(tre, "AC_TAIL_NO"),
            new IsrAttributes().getAttributeDescriptor(Isr.PLATFORM_ID),
            AIRCRAFT_TAIL_NUMBER_NAME);

    public static final AcftbAttribute SENSOR_ID_TYPE = new AcftbAttribute(Isr.SENSOR_TYPE,
            "SENSOR_ID_TYPE",
            tre -> TreUtility.getTreValue(tre, "SENSOR_ID_TYPE"),
            new IsrAttributes().getAttributeDescriptor(Isr.SENSOR_TYPE),
            SENSOR_ID_TYPE_NAME);

    public static final AcftbAttribute SENSOR_ID = new AcftbAttribute(Isr.SENSOR_ID,
            "SENSOR_ID",
            tre -> TreUtility.getTreValue(tre, "SENSOR_ID"),
            new IsrAttributes().getAttributeDescriptor(Isr.SENSOR_ID),
            SENSOR_ID_NAME);

    /*
     * Non-normalized attributes
     */

    public static final AcftbAttribute AIRCRAFT_TAKEOFF = new AcftbAttribute(AIRCRAFT_TAKEOFF_NAME,
            "AC_TO",
            tre -> TreUtility.getTreValue(tre, "AC_TO"),
            BasicTypes.STRING_TYPE);

    public static final AcftbAttribute SCENE_SOURCE = new AcftbAttribute(SCENE_SOURCE_NAME,
            "SCENE_SOURCE",
            tre -> TreUtility.getTreValue(tre, "SCENE_SOURCE"),
            BasicTypes.STRING_TYPE);

    public static final AcftbAttribute SCENE_NUMBER = new AcftbAttribute(SCENE_NUMBER_NAME,
            "SCNUM",
            tre -> TreUtility.getTreValue(tre, "SCNUM"),
            BasicTypes.STRING_TYPE);

    public static final AcftbAttribute PROCESSING_DATE = new AcftbAttribute(PROCESSING_DATE_NAME,
            "PDATE",
            tre -> TreUtility.getTreValue(tre, "PDATE"),
            BasicTypes.STRING_TYPE);

    public static final AcftbAttribute IMMEDIATE_SCENE_HOST = new AcftbAttribute(
            IMMEDIATE_SCENE_HOST_NAME,
            "IMHOSTNO",
            tre -> TreUtility.getTreValue(tre, "IMHOSTNO"),
            BasicTypes.STRING_TYPE);

    public static final AcftbAttribute IMMEDIATE_SCENE_REQUEST_ID = new AcftbAttribute(
            IMMEDIATE_SCENE_REQUEST_ID_NAME,
            "IMREQID",
            tre -> TreUtility.getTreValue(tre, "IMREQID"),
            BasicTypes.STRING_TYPE);

    public static final AcftbAttribute MISSION_PLAN_MODE =
            new AcftbAttribute(MISSION_PLAN_MODE_NAME,
                    "MPLAN",
                    tre -> TreUtility.getTreValue(tre, "MPLAN"),
                    BasicTypes.STRING_TYPE);

    private AcftbAttribute(final String longName, final String shortName,
            final Function<Tre, Serializable> accessorFunction,
            AttributeDescriptor attributeDescriptor, String extNitfName) {
        super(longName, shortName, accessorFunction, attributeDescriptor, extNitfName);
        ATTRIBUTES.add(this);
    }

    private AcftbAttribute(String longName, String shortName,
            Function<Tre, Serializable> accessorFunction, AttributeType attributeType) {
        super(longName, shortName, accessorFunction, attributeType);
        ATTRIBUTES.add(this);
    }

    public static List<NitfAttribute<Tre>> getAttributes() {
        return Collections.unmodifiableList(ATTRIBUTES);
    }

}
