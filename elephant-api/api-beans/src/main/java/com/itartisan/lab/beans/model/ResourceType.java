package com.itartisan.lab.beans.model;

import java.util.HashMap;
import java.util.Map;

public enum ResourceType {
    Pod(1),
    StatefulSet(2),
    Service(3),
    ConfigMap(4),
    PersistentVolume(5),
    PersistentVolumeClaim(6);

    public final int TYPE_CODE;
    private static Map<Integer, ResourceType> codeLookup = new HashMap<>();

    static {
        for (ResourceType type : ResourceType.values()) {
            codeLookup.put(type.TYPE_CODE, type);
        }
    }

    ResourceType(int code) {
        this.TYPE_CODE = code;
    }

    public static ResourceType forCode(int code) {
        return codeLookup.get(code);
    }

}
