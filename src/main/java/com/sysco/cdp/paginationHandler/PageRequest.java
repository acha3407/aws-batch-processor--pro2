package com.sysco.cdp.paginationHandler;

import lombok.Builder;
import lombok.Data;
import org.apache.http.client.utils.URIBuilder;

import java.net.HttpURLConnection;
import java.net.URISyntaxException;

public interface PageRequest {

    @Data
    @Builder
    class NextPage {

        private boolean hasNextPage;

        private URIBuilder nextUrl;
    }

    public NextPage checkNextPage(HttpURLConnection connection) throws URISyntaxException;

}
