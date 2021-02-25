package com.targa.labs.dev;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/vms")
public class VMsResource {

    @Inject
    AzureVmManager azureVmManager;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> getAvailableVMs() {
        return azureVmManager.getAvailableVMs();
    }
}
