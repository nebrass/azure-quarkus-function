package com.targa.labs.dev;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
    public List<VmDTO> getAvailableVMs() {
        return azureVmManager.getAvailableVMs();
    }

    @POST
    @Path("/start")
    @Consumes(MediaType.APPLICATION_JSON)
    public void startVM(VmDTO vmDTO) {
        azureVmManager.startVM(vmDTO);
    }

    @POST
    @Path("/stop")
    @Consumes(MediaType.APPLICATION_JSON)
    public void stopVM(VmDTO vmDTO) {
        azureVmManager.stopVM(vmDTO);
    }

    @GET
    @Path("/logs")
    @Produces(MediaType.APPLICATION_JSON)
    public List<VmEvent> logs() {
        return azureVmManager.getLogs();
    }
}
