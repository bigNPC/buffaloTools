package com.ddkj.buffalo.service.func.base;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JsonGenFile {
    private String name;
    private String parentName;
    private boolean hasList;
    private Map<String, String> nameTypes;
    private List<JsonGenFile> files;


    public JsonGenFile(String name, String parentName, Map<String, String> nameTypes, boolean hasList) {
        this.name = name;
        this.parentName = parentName;
        this.nameTypes = nameTypes;
        this.hasList = hasList;
    }
}
