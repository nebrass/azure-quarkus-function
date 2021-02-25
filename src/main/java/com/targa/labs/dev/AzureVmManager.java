package com.targa.labs.dev;

import com.azure.core.management.AzureEnvironment;
import com.azure.core.management.profile.AzureProfile;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.azure.resourcemanager.AzureResourceManager;
import com.azure.resourcemanager.resources.fluentcore.arm.models.HasName;

import javax.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class AzureVmManager {

    public List<String> getAvailableVMs() {

        /**
         * {
         *   "appId": "ff7a73d2-5898-4a4e-9efc-38cede7b883b",
         *   "displayName": "quarkus-function",
         *   "name": "http://quarkus-function",
         *   "password": "fVDZohU_dY2B_u7_hVK2Rxi96ZKJ1AtZ9h",
         *   "tenant": "72f988bf-86f1-41af-91ab-2d7cd011db47"
         * }
         */

        String clientId = System.getenv("CLIENT_ID");
        String clientSecret = System.getenv("CLIENT_SECRET");
        String tenantId = System.getenv("TENANT_ID");

        ClientSecretCredential clientSecretCredential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .build();

        AzureProfile profile = new AzureProfile(tenantId, "3f39e493-5d18-4db8-9368-9e3e3ff6975e", AzureEnvironment.AZURE);

        AzureResourceManager azureResourceManager = AzureResourceManager
                .authenticate(clientSecretCredential, profile)
                .withDefaultSubscription();


        List<String> rgList = azureResourceManager.resourceGroups().list().stream().map(HasName::name).collect(Collectors.toList());

        List<String> vmList = new ArrayList<>();

        for (String rg : rgList) {
            azureResourceManager.virtualMachines()
                    .listByResourceGroup(rg)
                    .forEach(vm -> vmList.add(vm.name()));
        }

        return vmList;
    }
}

