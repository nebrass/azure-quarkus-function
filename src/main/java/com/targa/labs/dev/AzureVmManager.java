package com.targa.labs.dev;

import com.azure.core.credential.TokenCredential;
import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.cosmos.CosmosClient;
import com.azure.cosmos.CosmosClientBuilder;
import com.azure.cosmos.CosmosContainer;
import com.azure.cosmos.models.CosmosQueryRequestOptions;
import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.resources.fluentcore.arm.models.HasName;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class AzureVmManager {

    public static final String COSMOS_DB_CONNECTION = "CosmosDbConnection";
    public static final String ACCOUNT_ENDPOINT = "AccountEndpoint";
    public static final String ACCOUNT_KEY = "AccountKey";
    public static final String EVENTS_DB_NAME = "events-db";
    public static final String EVENTS_CONTAINER_NAME = "events";
    public static final String SELECT_ALL_LOGS_QUERY = "SELECT * FROM c";

    private final AzureResourceManager azureResourceManager;
    private final CosmosContainer cosmosContainer;

    public AzureVmManager() {

        AzureProfile profile = new AzureProfile(AzureEnvironment.AZURE);
        TokenCredential credential = new DefaultAzureCredentialBuilder()
                .authorityHost(profile.getEnvironment().getActiveDirectoryEndpoint())
                .build();

        azureResourceManager = AzureResourceManager
                .authenticate(credential, profile)
                .withDefaultSubscription();

        Map<String, String> credentials = getCosmosDbCredentials();

        CosmosClient cosmosClient = new CosmosClientBuilder()
                .endpoint(credentials.get(ACCOUNT_ENDPOINT))
                .key(credentials.get(ACCOUNT_KEY))
                .buildClient();

        cosmosContainer = cosmosClient
                .getDatabase(EVENTS_DB_NAME)
                .getContainer(EVENTS_CONTAINER_NAME);
    }

    public List<VmDTO> getAvailableVMs() {

        List<String> rgList = azureResourceManager.resourceGroups()
                .list()
                .stream()
                .map(HasName::name)
                .collect(Collectors.toList());

        List<VmDTO> vmList = new ArrayList<>();

        for (String resourceGroup : rgList) {
            azureResourceManager.virtualMachines()
                    .listByResourceGroup(resourceGroup)
                    .forEach(vm -> vmList.add(
                            new VmDTO(
                                    vm.name(),
                                    vm.powerState().toString(),
                                    resourceGroup
                            )
                    ));
        }

        return vmList;
    }

    public void startVM(VmDTO vmDTO) {
        azureResourceManager.virtualMachines().start(vmDTO.getResourceGroup(), vmDTO.getName());
        sendData(
                new VmEvent(VmEventType.START, vmDTO.getName(), vmDTO.getResourceGroup())
        );
    }

    public void stopVM(VmDTO vmDTO) {
        azureResourceManager.virtualMachines().deallocate(vmDTO.getResourceGroup(), vmDTO.getName());
        sendData(
                new VmEvent(VmEventType.STOP, vmDTO.getName(), vmDTO.getResourceGroup())
        );
    }

    public void sendData(VmEvent vmEvent) {
        cosmosContainer.createItem(vmEvent);
    }

    public List<VmEvent> getLogs() {
        return cosmosContainer
                .queryItems(SELECT_ALL_LOGS_QUERY, new CosmosQueryRequestOptions(), VmEvent.class)
                .stream()
                .collect(Collectors.toList());
    }

    private Map<String, String> getCosmosDbCredentials() {
        Map<String, String> credentials = new HashMap<>();

        String cosmosDbConnection = System.getenv(COSMOS_DB_CONNECTION);
        String[] elements = cosmosDbConnection.split(";");

        for (String element : elements) {
            String[] split = element.split("=");
            credentials.put(split[0], split[1]);
        }

        return credentials;
    }
}
