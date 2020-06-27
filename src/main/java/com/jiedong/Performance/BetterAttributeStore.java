package com.jiedong.Performance;

import com.jiedong.annotations.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 * @author 19411
 * @date 2020/06/27 10:42
 **/
@ThreadSafe
public class BetterAttributeStore {
    @GuardedBy("this") private final Map<String, String>
            attributes = new HashMap<String, String>();

    public boolean userLocationMatches(String name, String regexp) {
        String key = "users." + name + ".location";
        String location;
        synchronized (this) {
            location = attributes.get(key);
        }
        if (location == null)
            return false;
        else
            return Pattern.matches(regexp, location);
    }
}
