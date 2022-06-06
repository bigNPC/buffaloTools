package com.ddkj.buffalo.service.func.base;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ActivityConfigFile {
    private String sheetName;
    private String parentSheetName;
    private Map<String, String> types;
    private Map<String, String> names;
    private List<ActivityConfigFile> files;


    public ActivityConfigFile(String sheetName, String parentSheetName, Map<String, String> keys, Map<String, String> names) {
        this.sheetName = sheetName;
        this.parentSheetName = parentSheetName;
        this.types = keys;
        this.names = names;
    }
}
