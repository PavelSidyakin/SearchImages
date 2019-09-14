package com.searchimages.data.flickr.model.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

@Root(name="photo", strict=false)
public class Photo {
    @Attribute(name="id", required=false)
    public String id;

    @Attribute(name="farm", required=false)
    public String farmId;

    @Attribute(name="server", required=false)
    public String serverId;

    @Attribute(name="secret", required=false)
    public String secret;

    @Override
    public String toString() {
        return "Photo{" + "id='" + id + '\'' + ", farmId='" + farmId + '\'' + ", serverId='" + serverId + '\'' + ", secret='" + secret + '\'' + '}';
    }
}
