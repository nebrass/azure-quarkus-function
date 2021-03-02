package com.targa.labs.dev;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VmDTO {
    private String name;
    private String status;
    private String resourceGroup;
}
