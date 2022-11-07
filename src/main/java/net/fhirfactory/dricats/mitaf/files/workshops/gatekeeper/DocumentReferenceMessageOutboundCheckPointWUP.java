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
package net.fhirfactory.dricats.mitaf.files.workshops.gatekeeper;

import net.fhirfactory.dricats.mitaf.files.workshops.gatekeeper.beans.DocumentReferencePolicyEnforcementPoint;
import net.fhirfactory.pegacorn.core.constants.systemwide.DRICaTSReferenceProperties;
import net.fhirfactory.pegacorn.core.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.dataparcel.valuesets.*;
import net.fhirfactory.pegacorn.internals.fhir.r4.internal.topics.FHIRElementTopicFactory;
import net.fhirfactory.pegacorn.workshops.PolicyEnforcementWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.MOAStandardWUP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class DocumentReferenceMessageOutboundCheckPointWUP extends MOAStandardWUP {
    private static final Logger LOG = LoggerFactory.getLogger(DocumentReferenceMessageOutboundCheckPointWUP.class);

    private static String WUP_VERSION = "1.0.0";

    @Inject
    private PolicyEnforcementWorkshop policyEnforcementWorkshop;

    @Inject
    private DRICaTSReferenceProperties referenceProperties;

    @Inject
    private FHIRElementTopicFactory fhirTopicFactory;

    @Override
    protected Logger specifyLogger() {
        return LOG;
    }

    @Override
    protected List<DataParcelManifest> specifySubscriptionTopics() {
        getLogger().debug(".specifySubscriptionTopics(): Entry");
        List<DataParcelManifest> subscriptionList = new ArrayList<>();
        DataParcelManifest subscriptionManifest = new DataParcelManifest();
        DataParcelTypeDescriptor messageDescriptor = fhirTopicFactory.newTopicToken("DocumentReference");
        subscriptionManifest.setContainerDescriptor(messageDescriptor);
        messageDescriptor.setVersion(DataParcelManifest.WILDCARD_CHARACTER);
        subscriptionManifest.setContentDescriptor(messageDescriptor);
        subscriptionManifest.setDataParcelFlowDirection(DataParcelDirectionEnum.INFORMATION_FLOW_OUTBOUND_DATA_PARCEL);
        subscriptionManifest.setSourceSystem(DataParcelManifest.WILDCARD_CHARACTER);
        subscriptionManifest.setIntendedTargetSystem(DataParcelManifest.WILDCARD_CHARACTER);
        subscriptionManifest.setSourceProcessingPlantParticipantName(DataParcelManifest.WILDCARD_CHARACTER);
        subscriptionManifest.setEnforcementPointApprovalStatus(PolicyEnforcementPointApprovalStatusEnum.POLICY_ENFORCEMENT_POINT_APPROVAL_NEGATIVE);
        subscriptionManifest.setDataParcelType(DataParcelTypeEnum.GENERAL_DATA_PARCEL_TYPE);
        subscriptionManifest.setValidationStatus(DataParcelValidationStatusEnum.DATA_PARCEL_CONTENT_VALIDATED_FALSE);
        subscriptionManifest.setNormalisationStatus(DataParcelNormalisationStatusEnum.DATA_PARCEL_CONTENT_NORMALISATION_TRUE);
        subscriptionManifest.setInterSubsystemDistributable(false);
        subscriptionList.add(subscriptionManifest);
        getLogger().debug(".specifySubscriptionTopics(): Exit, subscriptionList->{}", subscriptionList);
        return (subscriptionList);
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
        return (policyEnforcementWorkshop);
    }

    @Override
    protected String specifyParticipantDisplayName(){
        return("OutboundMessageCheckpoint");
    }

    @Override
    public void configure() throws Exception {
        getLogger().info("{}:: ingresFeed() --> {}", getClass().getName(), ingresFeed());
        getLogger().info("{}:: egressFeed() --> {}", getClass().getName(), egressFeed());

        fromIncludingPetasosServices(ingresFeed())
                .routeId(getNameSet().getRouteCoreWUP())
                .bean(DocumentReferencePolicyEnforcementPoint.class, "enforceOutboundPolicy")
                .to(egressFeed());
    }

    @Override
    protected List<DataParcelManifest> declarePublishedTopics() {
        return (new ArrayList<>());
    }
}
