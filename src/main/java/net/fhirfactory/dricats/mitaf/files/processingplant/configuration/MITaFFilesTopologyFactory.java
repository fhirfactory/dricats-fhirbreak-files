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
package net.fhirfactory.dricats.mitaf.files.processingplant.configuration;

import net.fhirfactory.dricats.mitaf.files.common.MITaFFilesNames;
import net.fhirfactory.pegacorn.core.model.topology.nodes.*;
import net.fhirfactory.pegacorn.deployment.topology.factories.archetypes.base.PetasosEnabledSubsystemTopologyFactory;
import net.fhirfactory.pegacorn.deployment.topology.factories.archetypes.base.endpoints.FileShareTopologyEndpointFactory;
import net.fhirfactory.pegacorn.util.PegacornEnvironmentProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MITaFFilesTopologyFactory extends PetasosEnabledSubsystemTopologyFactory {
    private static final Logger LOG = LoggerFactory.getLogger(MITaFFilesTopologyFactory.class);

    @Inject
    private MITaFFilesNames names;

    @Inject
    private PegacornEnvironmentProperties pegacornEnvironmentProperties;

    @Inject
    private FileShareTopologyEndpointFactory fileShareTopologyEndpointFactory;

    @Override
    protected Logger specifyLogger() {
        return (LOG);
    }

    @Override
    protected Class specifyPropertyFileClass() {
        return (MITaFFilesConfigurationFile.class);
    }

    @Override
    protected ProcessingPlantSoftwareComponent buildSubsystemTopology() {
        SubsystemTopologyNode subsystemTopologyNode = buildSubsystemNodeFromConfigurationFile();
        BusinessServiceTopologyNode businessServiceTopologyNode = buildBusinessServiceNode(subsystemTopologyNode);
        DeploymentSiteTopologyNode deploymentSiteTopologyNode = buildDeploymentSiteNode(businessServiceTopologyNode);
        ClusterServiceTopologyNode clusterServiceTopologyNode = buildClusterServiceNode(deploymentSiteTopologyNode, businessServiceTopologyNode);

        PlatformTopologyNode platformTopologyNode = buildPlatformNode(clusterServiceTopologyNode);
        ProcessingPlantSoftwareComponent processingPlantSoftwareComponent = buildProcessingPlant(platformTopologyNode, clusterServiceTopologyNode);
        addPrometheusPort(processingPlantSoftwareComponent);
        addJolokiaPort(processingPlantSoftwareComponent);
        addKubeLivelinessPort(processingPlantSoftwareComponent);
        addKubeReadinessPort(processingPlantSoftwareComponent);
        addEdgeAnswerPort(processingPlantSoftwareComponent);
        addAllJGroupsEndpoints(processingPlantSoftwareComponent);

        //
        // Config File
        MITaFFilesConfigurationFile configFile = (MITaFFilesConfigurationFile)getPropertyFile();

        //
        // Unique to MITaF.Files
        getLogger().trace(".buildSubsystemTopology(): Add the File Source port to the ProcessingPlant Topology Node");
        fileShareTopologyEndpointFactory.newFileShareSinkEndpoint(getPropertyFile(), processingPlantSoftwareComponent, names.getMITaFFilesWriterName(), configFile.getFileWriter());
        fileShareTopologyEndpointFactory.newFileShareSinkEndpoint(getPropertyFile(), processingPlantSoftwareComponent, names.getMITaFFilesMoverName(), configFile.getFileMover());
        fileShareTopologyEndpointFactory.newFileShareSourceEndpoint(getPropertyFile(), processingPlantSoftwareComponent, names.getMITaFFilesReaderName(), configFile.getFileReader());

        return(processingPlantSoftwareComponent);
    }

    protected String specifyPropertyFileName() {
        LOG.info(".specifyPropertyFileName(): Entry");
        String configurationFileName = pegacornEnvironmentProperties.getMandatoryProperty("DEPLOYMENT_CONFIG_FILE");
        if(configurationFileName == null){
            throw(new RuntimeException("Cannot load configuration file!!!! (SUBSYSTEM-CONFIG_FILE="+configurationFileName+")"));
        }
        LOG.trace(".specifyPropertyFileName(): Exit, filename->{}", configurationFileName);
        LOG.info(".specifyPropertyFileName(): Exit");
        return configurationFileName;
    }
}
