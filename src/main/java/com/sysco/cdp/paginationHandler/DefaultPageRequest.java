package com.sysco.cdp.paginationHandler;

import lombok.extern.slf4j.Slf4j;

import java.net.HttpURLConnection;
import java.net.URISyntaxException;

@Slf4j
public class DefaultPageRequest implements PageRequest {

    @Override
    public NextPage checkNextPage(HttpURLConnection connection) throws URISyntaxException {
        return NextPage.builder().hasNextPage(false).build();
    }
}
