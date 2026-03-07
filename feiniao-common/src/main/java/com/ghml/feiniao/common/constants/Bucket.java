package com.ghml.feiniao.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-01 11:15
 * @description
 */
@Getter
@AllArgsConstructor
public enum Bucket {

    AVATARS("avatars", "ghml-feiniao-avatars-prod", false),
    COVERS("covers", "ghml-feiniao-covers-prod", false),
    VIDEOS("videos", "ghml-feiniao-videos-prod", false);

    private final String key;
    private final String name;
    private final boolean isPublic;
}
