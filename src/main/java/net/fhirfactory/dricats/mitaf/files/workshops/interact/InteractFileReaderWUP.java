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
import net.fhirfactory.dricats.mitaf.files.workshops.interact.beans.FileReadActivity;
import net.fhirfactory.pegacorn.core.interfaces.topology.WorkshopInterface;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelManifest;
import net.fhirfactory.pegacorn.core.model.dataparcel.DataParcelTypeDescriptor;
import net.fhirfactory.pegacorn.core.model.petasos.wup.valuesets.WUPArchetypeEnum;
import net.fhirfactory.pegacorn.core.model.topology.endpoints.file.FileShareSourceTopologyEndpoint;
import net.fhirfactory.pegacorn.petasos.core.moa.wup.MessageBasedWUPEndpointContainer;
import net.fhirfactory.pegacorn.workshops.InteractWorkshop;
import net.fhirfactory.pegacorn.wups.archetypes.petasosenabled.messageprocessingbased.InteractIngresMessagingGatewayWUP;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class InteractFileReaderWUP extends InteractIngresMessagingGatewayWUP {
    private static final Logger LOG = LoggerFactory.getLogger(InteractFileReaderWUP.class);

    private static final String WUP_VERSION="1.0.0";

    private Long startupDelay = 180000L;

    private static final String FILE_SOURCE_DIRECTORY_PARAMETER_NAME = "FileSourceDirectory";
    private static final String FILENAME_PREFIX_PARAMETER_NAME = "FileNamePrefix";
    private static final String FILENAME_SUFFIX_PARAMETER_NAME = "FileNameSuffix";
    private static final String FILE_ACTIVITY_SUCCESS_DIRECTORY_PARAMETER_NAME = "FileActivitySuccessDirectory";
    private static final String FILE_ACTIVITY_FAIL_DIRECTORY_PARAMETER_NAME = "FileActivityFailDirectory";
    private static final String FILE_ACTIVITY_IN_PROGRESS_DIRECTORY_PARAMETER_NAME = "FileActivityInProgressDirectory";
    private static final String FILE_DELETE_AFTER_ACTIVITY_SUCCESS = "FileDeleteAfterActivitySuccess";
    private static final String ACTIVITY_STARTUP_DELAY = "ActivityStartupDelay";

    private static final String FILE_CONTENT_DESCRIPTOR_DEFINER = "FileContentDescriptorDefiner";
    private static final String FILE_CONTENT_DESCRIPTOR_CATEGORY = "FileContentDescriptorCategory";
    private static final String FILE_CONTENT_DESCRIPTOR_SUBCATEGORY = "FileContentDescriptorSubCategory";
    private static final String FILE_CONTENT_DESCRIPTOR_RESOURCE = "FileContentDescriptorResource";
    private static final String FILE_CONTENT_DESCRIPTOR_SEGMENT = "FileContentDescriptorSegment";
    private static final String FILE_CONTENT_DESCRIPTOR_ATTRIBUTE = "FileContentDescriptorAttribute";
    private static final String FILE_CONTENT_DESCRIPTOR_DISCRIMATOR_TYPE = "FileContentDescriptorDiscriminatorType";
    private static final String FILE_CONTENT_DESCRIPTOR_DISCRIMATOR_VALUE = "FileContentDescriptorDiscriminatorValue";
    private static final String FILE_CONTENT_DESCRIPTOR_VERSION = "FileContentDescriptorVersion";

    @Inject
    private InteractWorkshop workshop;

    @Inject
    private MITaFFilesNames names;

    @Inject
    private FileReadActivity fileReadActivity;

    //
    // Constructor(s)
    //


    //
    // Business Methods
    //

    @Override
    protected List<DataParcelManifest> declarePublishedTopics() {
        getLogger().debug(".declarePublishedTopics(): Entry");
        List<DataParcelManifest> publishedTopics = new ArrayList<>();

        getLogger().debug(".declarePublishedTopics(): Exit, publishedTopics->{}", publishedTopics);
        return (publishedTopics);
    }

    @Override
    protected List<DataParcelManifest> specifySubscriptionTopics() {
        getLogger().debug(".specifySubscriptionTopics(): Entry");

        List<DataParcelManifest> subscribedTopics = new ArrayList<>();

        getLogger().debug(".specifySubscriptionTopics(): Exit, subscribedTopics->{}", subscribedTopics);
        return (subscribedTopics);
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
    protected MessageBasedWUPEndpointContainer specifyIngresEndpoint() {
        getLogger().debug(".specifyIngresEndpoint(): Entry");
        MessageBasedWUPEndpointContainer ingresEndpoint = new MessageBasedWUPEndpointContainer();
        ingresEndpoint.setFrameworkEnabled(false);

        FileShareSourceTopologyEndpoint fileReaderEndpoint = (FileShareSourceTopologyEndpoint) getTopologyEndpoint("file-reader");
        if (fileReaderEndpoint == null) {
            getLogger().error(".specifyIngresTopologyEndpoint(): Unable to derive endpoint for File Share Source");
            return (ingresEndpoint);
        }
        // Assign the Associated TopologyNode to the Ingres Endpoint
        ingresEndpoint.setEndpointTopologyNode(fileReaderEndpoint);
        getLogger().trace(".specifyIngresTopologyEndpoint(): Resolved fileReaderEndpoint->{}", fileReaderEndpoint);
        // Building the Endpoint Specification (String)
        String ingresString = buildCamelComponentConfigurationString(fileReaderEndpoint);
        ingresEndpoint.setEndpointSpecification(ingresString);
        getLogger().info(".specifyIngresTopologyEndpoint(): Exit, ingresEndpoint->{}", ingresEndpoint);
        return (ingresEndpoint);
    }

    private String buildCamelComponentConfigurationString(FileShareSourceTopologyEndpoint topologyEndpoint) {
        getLogger().debug(".buildCamelComponentConfigurationString(): Entry");

        StringBuilder componentConfigurationBuilder = new StringBuilder();
        componentConfigurationBuilder.append("File:");
        // Source Directory
        if(!getFileReadActivity().hasFileSourceDirectory()){
            throw (new IllegalArgumentException("Source Directory Not Defined..."));
        }
        componentConfigurationBuilder.append(getFileReadActivity().getFileSourceDirectory());
        // parameters
        componentConfigurationBuilder.append("?");
        // Move on Success? or InProgress
        if(getFileReadActivity().hasFileActivitySuccessDirectory()){
            componentConfigurationBuilder.append("move=");
            componentConfigurationBuilder.append(getFileReadActivity().getFileActivitySuccessDirectory());
            componentConfigurationBuilder.append("&");
        } else if(getFileReadActivity().hasFileActivityInProgressDirectory()){
            componentConfigurationBuilder.append("move=");
            componentConfigurationBuilder.append(getFileReadActivity().getFileActivityInProgressDirectory());
            componentConfigurationBuilder.append("&");
        }
        // Move on Failed?
        if(getFileReadActivity().hasFileActivityFailDirectory()){
            componentConfigurationBuilder.append("moveFailed=");
            componentConfigurationBuilder.append(getFileReadActivity().getFileActivityFailDirectory());
            componentConfigurationBuilder.append("&");
        }
        // Delete After Read?
        if(getFileReadActivity().isDeleteAfterActivitySuccess()){
            componentConfigurationBuilder.append("delete=true");
            componentConfigurationBuilder.append("&");
        } else {
            componentConfigurationBuilder.append("delete=false");
            componentConfigurationBuilder.append("&");
        }
        // Include what files?
        boolean hasPrefix = getFileReadActivity().hasFileNamePrefix();
        boolean hasSuffix = getFileReadActivity().hasFileNameSuffix();
        if(hasPrefix && hasSuffix){
            componentConfigurationBuilder.append("include=");
            componentConfigurationBuilder.append(getFileReadActivity().getFileNamePrefix());
            componentConfigurationBuilder.append(getFileReadActivity().getFileNameSuffix());
            componentConfigurationBuilder.append("&");
        }
        if(!hasPrefix && hasSuffix){
            componentConfigurationBuilder.append("include=");
            componentConfigurationBuilder.append(".*");
            componentConfigurationBuilder.append(getFileReadActivity().getFileNameSuffix());
            componentConfigurationBuilder.append("&");
        }
        if(hasPrefix && !hasSuffix){
            componentConfigurationBuilder.append("include=");
            componentConfigurationBuilder.append(getFileReadActivity().getFileNamePrefix());
            componentConfigurationBuilder.append("&");
        }
        if(!hasPrefix && !hasSuffix){
            componentConfigurationBuilder.append("include=");
            componentConfigurationBuilder.append(".*");
            componentConfigurationBuilder.append("&");
        }
        // Startup Delay
        componentConfigurationBuilder.append("initialDelay=");
        componentConfigurationBuilder.append(Long.toString(getStartupDelay()));
        String componentConfigurationString = componentConfigurationBuilder.toString();
        getLogger().warn(".buildCamelComponentConfigurationString(): Exit, componentConfigurationString->{}", componentConfigurationString);
        return (componentConfigurationString);
    }

    protected void populateParameters(FileShareSourceTopologyEndpoint topologyEndpoint){
        getLogger().debug(".populateParameters(): Entry, topologyEndpoint->{}", topologyEndpoint);
        String fileSourceParameter = topologyEndpoint.getOtherConfigurationParameter(FILE_SOURCE_DIRECTORY_PARAMETER_NAME);
        if(StringUtils.isNotEmpty(fileSourceParameter)){
            getFileReadActivity().setFileSourceDirectory(fileSourceParameter);
            getLogger().debug(".populateParameters(): fileSourceDirector->{}", fileSourceParameter);
        }
        String fileNamePrefixParameter = topologyEndpoint.getOtherConfigurationParameter(FILENAME_PREFIX_PARAMETER_NAME);
        if(StringUtils.isNotEmpty(fileNamePrefixParameter)){
            getFileReadActivity().setFileNamePrefix(fileNamePrefixParameter);
            getLogger().debug(".populateParameters(): fileNamePrefix->{}", fileNamePrefixParameter);
        }
        String fileNameSuffixParameter = topologyEndpoint.getOtherConfigurationParameter(FILENAME_SUFFIX_PARAMETER_NAME);
        if(StringUtils.isNotEmpty(fileNameSuffixParameter)){
            getFileReadActivity().setFileNameSuffix(fileNameSuffixParameter);
            getLogger().debug(".populateParameters(): fileNameSuffix->{}", fileNameSuffixParameter);
        }
        String fileActivitySuccessParameter = topologyEndpoint.getOtherConfigurationParameter(FILE_ACTIVITY_SUCCESS_DIRECTORY_PARAMETER_NAME);
        if(StringUtils.isNotEmpty(fileActivitySuccessParameter)){
            getFileReadActivity().setFileActivitySuccessDirectory(fileActivitySuccessParameter);
            getLogger().debug(".populateParameters(): fileActivitySuccessDirectory->{}", fileActivitySuccessParameter);
        }
        String fileActivityFailParameter = topologyEndpoint.getOtherConfigurationParameter(FILE_ACTIVITY_FAIL_DIRECTORY_PARAMETER_NAME);
        if(StringUtils.isNotEmpty(fileActivityFailParameter)){
            getFileReadActivity().setFileActivityFailDirectory(fileActivityFailParameter);
            getLogger().debug(".populateParameters(): fileActivityFailDirectory->{}", fileActivityFailParameter);
        }
        String fileActivityInProgressParameter = topologyEndpoint.getOtherConfigurationParameter(FILE_ACTIVITY_IN_PROGRESS_DIRECTORY_PARAMETER_NAME);
        if(StringUtils.isNotEmpty(fileActivityInProgressParameter)){
            getFileReadActivity().setFileActivityInProgressDirectory(fileActivityInProgressParameter);
            getLogger().debug(".populateParameters(): fileActivityInProgressDirectory->{}", fileActivityInProgressParameter);
        }
        String fileDeleteAfterActivitySuccessParameter = topologyEndpoint.getOtherConfigurationParameter(FILE_DELETE_AFTER_ACTIVITY_SUCCESS);
        if(StringUtils.isNotEmpty(fileDeleteAfterActivitySuccessParameter)){
            if(fileDeleteAfterActivitySuccessParameter.equalsIgnoreCase("true")){
                getFileReadActivity().setDeleteAfterActivitySuccess(true);
                getLogger().debug(".populateParameters(): fileDeleteAfterActivitySuccess->{}", true);
            } else {
                getLogger().debug(".populateParameters(): fileDeleteAfterActivitySuccess->{}", false);
            }
        }

        //
        // Startup Delay

        String startupDelayString = topologyEndpoint.getOtherConfigurationParameter(ACTIVITY_STARTUP_DELAY);
        if(StringUtils.isNotEmpty(startupDelayString)){
            Long startupDelayParameter = null;
            try{
                startupDelayParameter = Long.getLong(startupDelayString);
            } catch(Exception ex){
                getLogger().warn(".populateParameters(): Cannot convert {} to Long value, using default of {}", ACTIVITY_STARTUP_DELAY, getStartupDelay());
            }
            if(startupDelayParameter != null) {
                setStartupDelay(startupDelayParameter);
                getLogger().debug(".populateParameters(): startupDelay->{}", getStartupDelay());
            }
        }

        //
        // Content Descriptor

        DataParcelTypeDescriptor descriptor = new DataParcelTypeDescriptor();
        boolean descriptorDefined = false;
        String descriptorDefiner = topologyEndpoint.getOtherConfigurationParameter(FILE_CONTENT_DESCRIPTOR_DEFINER);
        if(StringUtils.isNotEmpty(descriptorDefiner)){
            descriptor.setDataParcelDefiner(descriptorDefiner);
            descriptorDefined = true;
        }
        String descriptorCategory = topologyEndpoint.getOtherConfigurationParameter(FILE_CONTENT_DESCRIPTOR_CATEGORY);
        if(StringUtils.isNotEmpty(descriptorCategory)){
            descriptor.setDataParcelCategory(descriptorCategory);
            descriptorDefined = true;
        }
        String descriptorSubCategory = topologyEndpoint.getOtherConfigurationParameter(FILE_CONTENT_DESCRIPTOR_SUBCATEGORY);
        if(StringUtils.isNotEmpty(descriptorSubCategory)){
            descriptor.setDataParcelSubCategory(descriptorSubCategory);
            descriptorDefined = true;
        }
        String descriptorResource = topologyEndpoint.getOtherConfigurationParameter(FILE_CONTENT_DESCRIPTOR_RESOURCE);
        if(StringUtils.isNotEmpty(descriptorResource)){
            descriptor.setDataParcelResource(descriptorResource);
            descriptorDefined = true;
        }
        String descriptorSegment = topologyEndpoint.getOtherConfigurationParameter(FILE_CONTENT_DESCRIPTOR_SEGMENT);
        if(StringUtils.isNotEmpty(descriptorSegment)){
            descriptor.setDataParcelSegment(descriptorSegment);
            descriptorDefined = true;
        }
        String descriptorAttribute = topologyEndpoint.getOtherConfigurationParameter(FILE_CONTENT_DESCRIPTOR_ATTRIBUTE);
        if(StringUtils.isNotEmpty(descriptorAttribute)){
            descriptor.setDataParcelAttribute(descriptorAttribute);
            descriptorDefined = true;
        }
        String descriptorDiscriminatorType = topologyEndpoint.getOtherConfigurationParameter(FILE_CONTENT_DESCRIPTOR_DISCRIMATOR_TYPE);
        if(StringUtils.isNotEmpty(descriptorDiscriminatorType)){
            descriptor.setDataParcelDiscriminatorType(descriptorDiscriminatorType);
            descriptorDefined = true;
        }
        String descriptorDiscriminatorValue = topologyEndpoint.getOtherConfigurationParameter(FILE_CONTENT_DESCRIPTOR_DISCRIMATOR_VALUE);
        if(StringUtils.isNotEmpty(descriptorDiscriminatorValue)){
            descriptor.setDataParcelDiscriminatorValue(descriptorDiscriminatorValue);
            descriptorDefined = true;
        }
        String descriptorVersion = topologyEndpoint.getOtherConfigurationParameter(FILE_CONTENT_DESCRIPTOR_VERSION);
        if(StringUtils.isNotEmpty(descriptorVersion)){
            descriptor.setVersion(descriptorVersion);
            descriptorDefined = true;
        }
        if(descriptorDefined){
            getFileReadActivity().setContentDescriptor(descriptor);
        }
        getLogger().debug(".populateParameters(): Exit, descriptorDefined->{}, descriptor->{}", descriptorDefined, descriptor);
    }

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected WUPArchetypeEnum specifyWUPArchetype() {
        return null;
    }

    @Override
    protected MessageBasedWUPEndpointContainer specifyEgressEndpoint() {
        return null;
    }

    @Override
    protected String specifyIngresTopologyEndpointName() {
        return null;
    }

    @Override
    protected String specifyIngresEndpointVersion() {
        return null;
    }

    @Override
    protected String specifyEndpointParticipantName() {
        return null;
    }

    //
    // Camel Route
    //

    @Override
    public void configure() throws Exception {

    }

    //
    // Getters (and Setters)
    //

    protected FileReadActivity getFileReadActivity(){
        return(this.fileReadActivity);
    }

    protected Long getStartupDelay() {
        return startupDelay;
    }

    protected void setStartupDelay(Long startupDelay) {
        this.startupDelay = startupDelay;
    }
}
