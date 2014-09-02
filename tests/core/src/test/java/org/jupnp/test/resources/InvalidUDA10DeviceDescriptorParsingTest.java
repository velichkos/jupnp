/**
 * Copyright (C) 2014 4th Line GmbH, Switzerland and others
 *
 * The contents of this file are subject to the terms of either the
 * Common Development and Distribution License Version 1 or later
 * ("CDDL") (collectively, the "License"). You may not use this file
 * except in compliance with the License. See LICENSE.txt for more
 * information.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 */

package org.jupnp.test.resources;

import org.jupnp.UpnpService;
import org.jupnp.binding.xml.DescriptorBindingException;
import org.jupnp.binding.xml.DeviceDescriptorBinder;
import org.jupnp.binding.xml.RecoveringUDA10DeviceDescriptorBinderImpl;
import org.jupnp.binding.xml.UDA10DeviceDescriptorBinderSAXImpl;
import org.jupnp.mock.MockUpnpService;
import org.jupnp.mock.MockUpnpServiceConfiguration;
import org.jupnp.model.meta.RemoteDevice;
import org.jupnp.test.data.SampleData;
import org.jupnp.util.io.IO;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Christian Bauer
 */
public class InvalidUDA10DeviceDescriptorParsingTest {

    @DataProvider(name = "strict")
    public String[][] getStrict() throws Exception {
        return new String[][]{
            {"/invalidxml/device/atb_miviewtv.xml"},
            {"/invalidxml/device/doubletwist.xml"},
            {"/invalidxml/device/eyetv_netstream_sat.xml"},
            {"/invalidxml/device/makemkv.xml"},
            {"/invalidxml/device/tpg.xml"},
            {"/invalidxml/device/ceton_infinitv.xml"},
            {"/invalidxml/device/zyxel_miviewtv.xml"},
            {"/invalidxml/device/perfectwave.xml"},
            {"/invalidxml/device/escient.xml"},
            {"/invalidxml/device/eyecon.xml"},
            {"/invalidxml/device/kodak.xml"},
            {"/invalidxml/device/plutinosoft.xml"},
            {"/invalidxml/device/samsung.xml"},
            {"/invalidxml/device/philips_hue.xml"},
        };
    }

    @DataProvider(name = "recoverable")
    public String[][] getRecoverable() throws Exception {
        return new String[][]{
        		// TODO: These tests are currently failing!
//            {"/invalidxml/device/missing_namespaces.xml"},
//            {"/invalidxml/device/ushare.xml"},
//            {"/invalidxml/device/lg.xml"},
//            {"/invalidxml/device/readydlna.xml"},
        };
    }

    @DataProvider(name = "unrecoverable")
    public String[][] getUnrecoverable() throws Exception {
        return new String[][]{
            {"/invalidxml/device/unrecoverable/pms.xml"},
            {"/invalidxml/device/unrecoverable/awox.xml"},
            {"/invalidxml/device/philips.xml"},
            {"/invalidxml/device/simplecenter.xml"},
            {"/invalidxml/device/ums.xml"},
        };
    }

    /* ############################## TEST FAILURE ############################ */

    @Test(dataProvider = "recoverable", expectedExceptions = DescriptorBindingException.class)
    public void readFailure(String recoverable) throws Exception {
        readDevice(recoverable, new MockUpnpService());
    }

    @Test(dataProvider = "unrecoverable", expectedExceptions = Exception.class)
    public void readRecoveringFailure(String unrecoverable) throws Exception {
        readDevice(
            unrecoverable,
            new MockUpnpService(new MockUpnpServiceConfiguration() {
                @Override
                public DeviceDescriptorBinder getDeviceDescriptorBinderUDA10() {
                    return new RecoveringUDA10DeviceDescriptorBinderImpl();
                }
            })
        );
    }

    /* ############################## TEST SUCCESS ############################ */

    @Test(dataProvider = "strict")
    public void readDefault(String strict) throws Exception {
        readDevice(strict, new MockUpnpService());
    }

    @Test(dataProvider = "strict")
    public void readSAX(String strict) throws Exception {
        readDevice(
            strict,
            new MockUpnpService(new MockUpnpServiceConfiguration() {
                @Override
                public DeviceDescriptorBinder getDeviceDescriptorBinderUDA10() {
                    return new UDA10DeviceDescriptorBinderSAXImpl();
                }
            })
        );
    }

    @Test(dataProvider = "strict")
    public void readRecoveringStrict(String strict) throws Exception {
        readDevice(
            strict,
            new MockUpnpService(new MockUpnpServiceConfiguration() {
                @Override
                public DeviceDescriptorBinder getDeviceDescriptorBinderUDA10() {
                    return new RecoveringUDA10DeviceDescriptorBinderImpl();
                }
            })
        );
    }

    @Test(dataProvider = "recoverable")
    public void readRecovering(String recoverable) throws Exception {
        readDevice(
            recoverable,
            new MockUpnpService(new MockUpnpServiceConfiguration() {
                @Override
                public DeviceDescriptorBinder getDeviceDescriptorBinderUDA10() {
                    return new RecoveringUDA10DeviceDescriptorBinderImpl();
                }
            })
        );
    }

	protected void readDevice(String invalidXMLFile, UpnpService upnpService) throws Exception {
		RemoteDevice device = new RemoteDevice(SampleData.createRemoteDeviceIdentity());
		upnpService.getConfiguration().getDeviceDescriptorBinderUDA10()
            .describe(device, IO.readLines(getClass().getResourceAsStream(invalidXMLFile)));
	}

}
