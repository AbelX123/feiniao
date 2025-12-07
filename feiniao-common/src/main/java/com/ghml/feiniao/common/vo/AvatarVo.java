package com.ghml.feiniao.common.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AvatarVo {
    private String avatar;
    private long expiry;
}
