package com.targa.labs.dev;

import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.compute.models.VirtualMachine;
import com.azure.resourcemanager.resources.fluentcore.arm.models.HasName;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AzureVmManager {

    public List<VmDTO> getAvailableVMs() {

        /**
         * {
         *   "appId": "e7a385b9-504c-477c-8d51-df7fd0722957",
         *   "displayName": "QuarkusServicePrincipal",
         *   "name": "http://QuarkusServicePrincipal",
         *   "password": "uYE6TEIW.ZvOxZKw7p53.L_HBm9bxr5~sw",
         *   "tenant": "2cf6bd3b-eb51-47c4-9d5d-06cdf42bb3a6"
         * }
         *
         *
         * {
         *   "appId": "ff7a73d2-5898-4a4e-9efc-38cede7b883b",
         *   "displayName": "quarkus-function",
         *   "name": "http://quarkus-function",
         *   "password": "fVDZohU_dY2B_u7_hVK2Rxi96ZKJ1AtZ9h",
         *   "tenant": "72f988bf-86f1-41af-91ab-2d7cd011db47"
         *   "subscription": "3f39e493-5d18-4db8-9368-9e3e3ff6975e"
         * }
         */

        String clientId = System.getenv("CLIENT_ID");
        String clientSecret = System.getenv("CLIENT_SECRET");
        String tenantId = System.getenv("TENANT_ID");
        String subscriptionId = System.getenv("SUBSCRIPTION_ID");

        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .build();

        AzureProfile profile = new AzureProfile(tenantId, subscriptionId, AzureEnvironment.AZURE);

        AzureResourceManager azureResourceManager = AzureResourceManager
                .authenticate(clientSecretCredential, profile)
                .withDefaultSubscription();


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
}

