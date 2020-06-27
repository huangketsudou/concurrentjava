package com.jiedong.Performance;

import com.jiedong.annotations.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author 19411
 * @date 2020/06/27 10:40
 **/
@ThreadSafe
public class AttributeStore {
    @GuardedBy("this") private final Map<String, String>
            attributes = new HashMap<String, String>();

    public synchronized boolean userLocationMatches(String name,
                                                    String regexp) {
        String key = "users." + name + ".location";
        String location = attributes.get(key);
        if (location == null)
            return false;
        else
            return Pattern.matches(regexp, location);
    }
}
