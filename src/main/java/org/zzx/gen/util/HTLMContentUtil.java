package org.zzx.gen.util;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.io.resource.ResourceUtil;

import java.io.IOException;

public class HTLMContentUtil {
    public static String load(String path){
       return ResourceUtil.readUtf8Str(path);
    }
}
