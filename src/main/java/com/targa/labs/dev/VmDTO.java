package com.targa.labs.dev;

public class VmDTO {
    private String name;
    private String status;
    private String resourceGroup;

    public VmDTO(){
        // Empty Constructor
    }

    public VmDTO(String name, String status, String resourceGroup) {
        this.name = name;
        this.status = status;
        this.resourceGroup = resourceGroup;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResourceGroup() {
        return resourceGroup;
    }

    public void setResourceGroup(String resourceGroup) {
        this.resourceGroup = resourceGroup;
    }
}
