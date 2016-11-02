/**
 * Copyright (c) Codice Foundation
 * <p>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */

package org.codice.alliance.test.itests;

import static org.codice.ddf.itests.common.AbstractIntegrationTest.DynamicUrl.SECURE_ROOT;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.xml.HasXPath.hasXPath;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static com.jayway.restassured.RestAssured.delete;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import javax.imageio.ImageIO;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.codice.alliance.test.itests.common.AbstractAllianceIntegrationTest;
import org.codice.alliance.transformer.nitf.image.ImageAttribute;
import org.codice.ddf.itests.common.annotations.BeforeExam;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.service.cm.Configuration;

import com.jayway.restassured.response.ValidatableResponse;

/**
 * Alliance Imaging Application integration tests.
 * <p>
 * The images used to test the NITF JPEG 2000 transformer were downloaded from
 * http://www.gwg.nga.mil/ntb/baseline/software/testfile/Jpeg2000/index.htm.
 */
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class ImagingTest extends AbstractAllianceIntegrationTest {
    private static final String[] REQUIRED_APPS =
            {"catalog-app", "solr-app", "spatial-app", "imaging-app"};

    private static final String TEST_IMAGE_NITF = "i_3001a.ntf";

    private static final String TEST_MTI_NITF = "gmti-test.ntf";

    private final List<String> metacardIds = new ArrayList<>();

    @BeforeExam
    public void beforeAllianceTest() throws Exception {
        try {
            basePort = getBasePort();
            getAdminConfig().setLogLevels();
            getServiceManager().waitForRequiredApps(REQUIRED_APPS);
            getServiceManager().waitForAllBundles();
            getCatalogBundle().waitForCatalogProvider();
            configureSecurityStsClient();
        } catch (Exception e) {
            LOGGER.error("Failed in @BeforeExam: ", e);
            fail("Failed in @BeforeExam: " + e.getMessage());
        }
    }

    @Test
    public void testValidImageNitfMetacard() throws Exception {
        String id = ingestNitfFile(TEST_IMAGE_NITF);

        String url = REST_PATH.getUrl() + id;

        when().get(url)
                .then()
                .assertThat()
                .contentType(MediaType.TEXT_XML)
                .body(hasXPath("/metacard[@id='" + id + "']/type", is("isr.image")));
    }

    @Test
    public void testValidMtiNitfMetacard() throws Exception {
        String id = ingestNitfFile(TEST_MTI_NITF);

        String url = REST_PATH.getUrl() + id;

        when().get(url)
                .then()
                .assertThat()
                .contentType(MediaType.TEXT_XML)
                .body(hasXPath("/metacard[@id='" + id + "']/type", is("isr.gmti")));
    }

    @Test
    public void testNitfImageGeneration() throws Exception {
        String id = ingestNitfFile(TEST_IMAGE_NITF);

        assertGetJpeg(REST_PATH.getUrl() + id + "?transform=thumbnail");
        assertGetJpeg(REST_PATH.getUrl() + id + "?transform=resource&qualifier=original");
        assertGetJpeg(REST_PATH.getUrl() + id + "?transform=resource&qualifier=overview");
    }

    @Test
    public void testImageNitfChipCreation() throws Exception {
        String id = ingestNitfFile(TEST_IMAGE_NITF);

        String chippingUrl = SECURE_ROOT + HTTPS_PORT.getPort() + "/chipping/chipping.html?id=" + id
                + "&source=Alliance";
        given().get(chippingUrl)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK);

        final int width = 350;
        final int height = 240;

        String chippedImageUrl =
                SERVICE_ROOT + "/catalog/" + id + "?transform=chip&qualifier=overview&x=" + 300
                        + "&y=" + 200 + "&w=" + width + "&h=" + height;
        InputStream chippedImageStream = given().get(chippedImageUrl)
                .asInputStream();
        BufferedImage chippedImage = ImageIO.read(chippedImageStream);

        assertThat(chippedImage.getWidth(), is(width));
        assertThat(chippedImage.getHeight(), is(height));
    }

    /**
     * The feature or item tested in this file is: "5x3 wavelet, 64x64 codeblocks, MQ-coder,
     * context model"
     */
    @Test
    public void testNitfJpeg2000p001a() throws Exception {
        ingestAndValidateCommonNitfJpeg2000Attributes("p0_01a").and()
                .hasStringElement("resource-size", "8957")
                .hasDateElement(ImageAttribute.IMAGE_DATE_AND_TIME, "2001-11-02T06:30:48.000+00:00")
                .hasStringElement("checksum", "3a3bc72");
    }

    /**
     * The feature or item tested in this file is: "component subsampling, multiple layers,
     * termination every coding pass, predictable termination, segmentation symbols, COD, QCD, EPH,
     * SOP, and 0xFF30 marker segments, 32x32 codeblocks"
     */
    @Test
    public void testNitfJpeg2000p002a() throws Exception {
        ingestAndValidateCommonNitfJpeg2000Attributes("p0_02a").and()
                .hasStringElement("resource-size", "7750")
                .hasDateElement(ImageAttribute.IMAGE_DATE_AND_TIME, "2001-11-02T06:30:48.000+00:00")
                .hasStringElement("checksum", "972f2672");
    }

    /**
     * The feature or item tested in this file is: "Multiple components, termination every coding
     * pass, 9x7 wavelet, precinct sizes in COD, irreversible component transform, scalar expound
     * quantization"
     */
    @Test
    public void testNitfJpeg2000p004b() throws Exception {
        ingestAndValidateCommonNitfJpeg2000Attributes("p0_04b").and()
                .hasStringElement("resource-size", "265508")
                .hasDateElement(ImageAttribute.IMAGE_DATE_AND_TIME, "2001-12-10T05:38:34.000+00:00")
                .hasStringElement("checksum", "a0d12a41");
    }

    /**
     * The feature or item tested in this file is: "9x7 wavelet transform overflow"
     */
    @Test
    public void testNitfJpeg2000p009a() throws Exception {
        ingestAndValidateCommonNitfJpeg2000Attributes("p0_09a").and()
                .hasStringElement("resource-size", "2161")
                .hasDateElement(ImageAttribute.IMAGE_DATE_AND_TIME, "2001-12-17T18:22:24.000+00:00")
                .hasStringElement("checksum", "117990ba");
    }

    /**
     * The feature or item tested in this file is: "Image source is psuedo-random, subsampling by 4,
     * 0 guard bits, reversible color transform"
     */
    @Test
    public void testNitfJpeg2000p010b() throws Exception {
        ingestAndValidateCommonNitfJpeg2000Attributes("p0_10b").and()
                .hasStringElement("resource-size", "15004")
                .hasDateElement(ImageAttribute.IMAGE_DATE_AND_TIME, "2002-01-23T13:09:22.000+00:00")
                .hasStringElement("checksum", "cdf9dc3f");
    }

    /**
     * The feature or item tested in this file is: "1 sample high image, 0 decomposition level test,
     * segmentation symbols"
     */
    @Test
    public void testNitfJpeg2000p011xa() throws Exception {
        ingestAndValidateCommonNitfJpeg2000Attributes("p0_11xa").and()
                .hasStringElement("resource-size", "1800")
                .hasDateElement(ImageAttribute.IMAGE_DATE_AND_TIME, "2001-11-02T06:30:50.000+00:00")
                .hasStringElement("checksum", "76abdc9c");
    }

    /**
     * The feature or item tested in this file is: "Special wavelet transform cases"
     */
    @Test
    public void testNitfJpeg2000p012a() throws Exception {
        ingestAndValidateCommonNitfJpeg2000Attributes("p0_12a").and()
                .hasStringElement("resource-size", "1852")
                .hasDateElement(ImageAttribute.IMAGE_DATE_AND_TIME, "2001-12-13T11:15:18.000+00:00")
                .hasStringElement("checksum", "fa66cc4e");
    }

    /**
     * The feature or item tested in this file is: "5-3 Saturation test"
     */
    @Test
    public void testNitfJpeg2000p014b() throws Exception {
        ingestAndValidateCommonNitfJpeg2000Attributes("p0_14b").and()
                .hasStringElement("resource-size", "2507")
                .hasDateElement(ImageAttribute.IMAGE_DATE_AND_TIME, "2001-12-17T17:36:26.000+00:00")
                .hasStringElement("checksum", "9cf98387");
    }

    /**
     * The feature or item tested in this file is: "Empty packet header bit"
     */
    @Test
    public void testNitfJpeg2000p016a() throws Exception {
        ingestAndValidateCommonNitfJpeg2000Attributes("p0_16a").and()
                .hasStringElement("resource-size", "8974")
                .hasDateElement(ImageAttribute.IMAGE_DATE_AND_TIME, "2002-01-23T19:32:10.000+00:00")
                .hasStringElement("checksum", "9e81c514");
    }

    /**
     * The feature or item tested in this file is: "Reset context probabilities, vertically causal
     * contexts, precinct sizes, PPT marker segment"
     */
    @Test
    public void testNitfJpeg2000p102b() throws Exception {
        ingestAndValidateCommonNitfJpeg2000Attributes("p1_02b").and()
                .hasStringElement("resource-size", "263963")
                .hasDateElement(ImageAttribute.IMAGE_DATE_AND_TIME, "2001-12-26T15:02:10.000+00:00")
                .hasStringElement("checksum", "74b85e69");
    }

    /**
     * The feature or item tested in this file is: "QCD marker segment in tile header
     */
    @Test
    public void testNitfJpeg2000p104a() throws Exception {
        ingestAndValidateCommonNitfJpeg2000Attributes("p1_04a").and()
                .hasStringElement("resource-size", "103411")
                .hasDateElement(ImageAttribute.IMAGE_DATE_AND_TIME, "2001-11-02T06:30:50.000+00:00")
                .hasStringElement("checksum", "1dc68130");
    }

    /**
     * The feature or item tested in this file is: "Small tile size" (3 by 3)
     */
    @Test
    public void testNitfJpeg2000p106b() throws Exception {
        ingestAndValidateCommonNitfJpeg2000Attributes("p1_06b").and()
                .hasStringElement("resource-size", "4229")
                .hasDateElement(ImageAttribute.IMAGE_DATE_AND_TIME, "2001-11-02T06:30:50.000+00:00")
                .hasStringElement("checksum", "30c9ef90");
    }

    @After
    public void tearDown() {
        metacardIds.forEach(this::deleteMetacard);
        metacardIds.clear();
    }

    /**
     * Class used to validate Metacard XML responses using a fluent API.
     */
    private static class MetacardXmlValidator {
        private final ValidatableResponse response;

        private final String id;

        public MetacardXmlValidator(ValidatableResponse response, String id) {
            this.response = response;
            this.id = id;
        }

        private MetacardXmlValidator has(String type, String name, String expectedValue) {
            response.body(hasXPath(String.format("/metacard[@id='%s']/%s[@name='%s']/value",
                    id,
                    type,
                    name), is(expectedValue)));
            return this;
        }

        /**
         * Chains two conditions
         *
         * @return this validator
         */
        public MetacardXmlValidator and() {
            return this;
        }

        /**
         * Asserts that the response contains a {@code <string>} element with a specific value
         *
         * @param name          string element name as specified by the {@code name} attribute
         * @param expectedValue text expected in the {@code <value>} element
         * @return this validator
         */
        public MetacardXmlValidator hasStringElement(String name, String expectedValue) {
            return has("string", name, expectedValue);
        }

        /**
         * Asserts that the response contains an {@code <int>} element with a specific value
         *
         * @param name          int element name as specified by the {@code name} attribute
         * @param expectedValue integer expected in the {@code <value>} element
         * @return this validator
         */
        public MetacardXmlValidator hasIntElement(String name, Integer expectedValue) {
            return has("int", name, expectedValue.toString());
        }

        /**
         * Asserts that the response contains a {@code <dateTime>} element with a specific value
         *
         * @param name          dateTime element name as specified by the {@code name} attribute
         * @param expectedValue date and time expected in the {@code <value>} element
         * @return this validator
         */
        public MetacardXmlValidator hasDateElement(String name, String expectedValue) {
            return has("dateTime", name, expectedValue);
        }

        /**
         * Asserts that the response contains a {@code <base64Binary>} element with a specific
         * value
         *
         * @param name     base64Binary element name as specified by the {@code name} attribute
         * @param fileName name of the file that contains the expected base 64 encoded text in
         *                 the {@code <value>} element
         * @return this validator
         */
        public MetacardXmlValidator hasBase64Binary(String name, String fileName)
                throws IOException {
            InputStream inputStream = getClass().getClassLoader()
                    .getResourceAsStream(fileName);
            String expectedValue = IOUtils.toString(inputStream);
            return has("base64Binary", name, expectedValue);
        }
    }

    private MetacardXmlValidator ingestAndValidateCommonNitfJpeg2000Attributes(
            String fileNamePrefix) throws Exception {
        String id = ingestNitfFile(fileNamePrefix + ".ntf");

        String url = REST_PATH.getUrl() + id;

        ValidatableResponse response = when().get(url)
                .then()
                .assertThat()
                .contentType(MediaType.TEXT_XML)
                .body(hasXPath("/metacard[@id='" + id + "']/type", is("isr.image")));

        return new MetacardXmlValidator(response, id).hasStringElement("media.type", "image/nitf")
                .hasStringElement("media.compression", "JPEG2000")
                .hasStringElement(ImageAttribute.IMAGE_COMPRESSION, "JPEG2000")
                .hasBase64Binary("thumbnail", fileNamePrefix + ".thumbnail");
    }

    private String ingestNitfFile(String fileName) throws Exception {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream(fileName);
        byte[] fileBytes = IOUtils.toByteArray(inputStream);

        String id = given().multiPart("file", fileName, fileBytes, "image/nitf")
                .expect()
                .statusCode(HttpStatus.SC_CREATED)
                .when()
                .post(REST_PATH.getUrl())
                .getHeader("id");
        metacardIds.add(id);

        return id;
    }

    private void assertGetJpeg(String imageUrl) throws Exception {
        given().get(imageUrl)
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .header(HttpHeaders.CONTENT_TYPE, is("image/jpeg"));
    }

    private void deleteMetacard(String id) {
        delete(REST_PATH.getUrl() + id);
    }

    private void configureSecurityStsClient() throws IOException, InterruptedException {
        Configuration stsClientConfig = configAdmin.getConfiguration(
                "ddf.security.sts.client.configuration.cfg",
                null);
        Dictionary<String, Object> properties = new Hashtable<>();

        properties.put("address",
                SECURE_ROOT + HTTPS_PORT.getPort() + "/services/SecurityTokenService?wsdl");
        stsClientConfig.update(properties);
    }
}
