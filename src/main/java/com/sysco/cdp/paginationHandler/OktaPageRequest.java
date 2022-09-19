package com.sysco.cdp.paginationHandler;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;

import java.net.HttpURLConnection;
import java.net.URISyntaxException;

@Slf4j
public class OktaPageRequest implements PageRequest {

    @Override
    public NextPage checkNextPage(HttpURLConnection connection) throws URISyntaxException {

        var build = NextPage.builder();
        String link = connection.getHeaderField("link");

        if (link != null && link.indexOf("rel=\"self\"") == -1) {
            String httpsUrl = link.substring(1, link.indexOf("rel=\"next\"") - 3);
            build.hasNextPage(false).nextUrl(new URIBuilder(httpsUrl));

            System.out.println("closing connection");


        } else {
            build.hasNextPage(false);

        }

        return build.build();
    }
}
