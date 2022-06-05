/*
 * Copyright (c) 2022 Mark A. Hunter
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.fhirfactory.dricats.mitaf.files.workshops.interact;

import net.fhirfactory.dricats.mitaf.files.common.MITaFFilesNames;
import net.fhirfactory.pegacorn.core.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.MessageBasedWUPEndpointContainer;
import net.fhirfactory.pegacorn.workshops.InteractWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.InteractEgressMessagingGatewayWUP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class InteractFileMoverWUP extends InteractEgressMessagingGatewayWUP {
    private static final Logger LOG = LoggerFactory.getLogger(InteractFileMoverWUP.class);

    private static final String WUP_VERSION="1.0.0";

    @Inject
    private InteractWorkshop workshop;

    @Inject
    private MITaFFilesNames names;

    @Override
    protected List<DataParcelManifest> specifySubscriptionTopics() {
        List<DataParcelManifest> subscriptionList = new ArrayList<>();

        DataParcelManifest moveTaskManifest = new DataParcelManifest();
        DataParcelTypeDescriptor moveTaskContentDescriptor = new DataParcelTypeDescriptor();
        moveTaskContentDescriptor.setDataParcelDefiner("AustralianGovernmentArchitectureReferenceModel");
        moveTaskContentDescriptor.setDataParcelCategory("130-DigitalAssetServices");
        moveTaskContentDescriptor.setDataParcelSubCategory("1302-DocumentManagement");
        moveTaskContentDescriptor.setDataParcelResource("130204-LibraryAndStorage");
        moveTaskContentDescriptor.setDataParcelSegment("File-Management");
        moveTaskContentDescriptor.setDataParcelSegment("File-Move");


        return (subscriptionList);
    }

    @Override
    protected List<DataParcelManifest> declarePublishedTopics() {
        return null;
    }

    @Override
    protected String specifyWUPInstanceName() {
        return (getClass().getSimpleName());
    }

    @Override
    protected String specifyWUPInstanceVersion() {
        return (WUP_VERSION);
    }

    @Override
    protected WorkshopInterface specifyWorkshop() {
        return (workshop);
    }

    @Override
    protected MessageBasedWUPEndpointContainer specifyEgressEndpoint() {
        return null;
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected String specifyEgressTopologyEndpointName() {
        return (names.getMITaFFilesMoverName());
    }

    @Override
    protected String specifyEndpointParticipantName() {
        return null;
    }

    @Override
    public void configure() throws Exception {

    }
}
