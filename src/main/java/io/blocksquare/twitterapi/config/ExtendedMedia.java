package io.blocksquare.twitterapi.config;

import com.google.gson.annotations.SerializedName;
import com.twitter.clientlib.model.Media;

public class ExtendedMedia extends Media {
    @SerializedName("preview_image_url")
    private String previewImageUrl;

    @SerializedName("url")
    private String url;

    public String getPreviewImageUrl() {
        return previewImageUrl;
    }

    public String getUrl() {
        return url;
    }


}
