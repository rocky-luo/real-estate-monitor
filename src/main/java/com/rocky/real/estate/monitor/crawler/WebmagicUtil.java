package com.rocky.real.estate.monitor.crawler;

import us.codecraft.webmagic.selector.Selectable;

/**
 * Created by rocky on 18/6/18.
 */
public class WebmagicUtil {
    public static String xmlText(Selectable selectable) {
        return selectable.regex(">(.*)<", 1).toString();
    }
}
