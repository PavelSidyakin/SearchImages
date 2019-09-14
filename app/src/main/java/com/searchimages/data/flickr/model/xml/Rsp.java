package com.searchimages.data.flickr.model.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name="rsp", strict=false)
public class Rsp {

    @Attribute(name="stat", required=false)
    public String stat;

    @ElementList(name="photos", entry="photo", required=false)
    public List<Photo> photoList;

    @Override
    public String toString() {
        return "Rsp{" + "stat='" + stat + '\'' + ", photoList=" + photoList + '}';
    }
}
