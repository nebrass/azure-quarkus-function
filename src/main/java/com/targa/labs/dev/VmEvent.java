package com.targa.labs.dev;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

enum VmEventType {
    START,
    STOP
}

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VmEvent {
    private VmEventType type;
    private String id;
    private String name;
    private String resourceGroup;
    private Date date;

    public VmEvent(VmEventType type, String name, String resourceGroup) {
        this.type = type;
        this.name = name;
        this.resourceGroup = resourceGroup;
        this.id = UUID.randomUUID().toString();
        this.date = new Date();
    }
}