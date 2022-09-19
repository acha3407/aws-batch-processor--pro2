package com.sysco.cdp.service.impl;


import com.amazonaws.util.StringUtils;
import com.sysco.cdp.beans.BulkJobDTO;
import com.sysco.cdp.common.Constants;
import com.sysco.cdp.paginationHandler.PageRequest;
import com.sysco.cdp.service.ApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Service("apiServiceImpl")
@Slf4j
public class ApiServiceImpl implements ApiService {

    protected URI getUri(String api) {
        URI uri;
        try {
            log.info("getUri | Encoding Api url : {}", api);
            URL url = new URL(api);
            uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            log.info("getUri Encoded Api url : {}", uri);
        } catch (MalformedURLException | URISyntaxException e) {
            log.error("getUri | Invalid API url {} : {}", api, e);
            throw new IllegalArgumentException("Can't find valid API url");
        }
        return uri;
    }

    @Override
    public String[] invokeApi(BulkJobDTO adapterDTO, PageRequest nextPage) {

        URIBuilder uriBuilder = new URIBuilder(getUri(adapterDTO.getApiUrl()));

        log.info("invokeGetApiData | Invoke api url for url: {}, request type: {},",
                adapterDTO.getApiUrl(), adapterDTO.getRequestType());
        HttpURLConnection conn = null;
        String contentType = null;

        String link = null;
        boolean flag = true;
        BufferedReader br = null;
        InputStreamReader ir = null;
        String content = "";
        String error = null;

        try {
            do {
                conn = createConnection(uriBuilder);
                conn.setConnectTimeout(60000);
                conn.setReadTimeout(120000);
                conn.setRequestMethod(adapterDTO.getRequestType());
                conn.setRequestProperty("Cache-control", "no-cache");

                if (adapterDTO.getRequestHeaders() != null && !adapterDTO.getRequestHeaders().isEmpty()) {
                    log.info("invokeGetApiData |Adding header to the request");
                    for (BulkJobDTO.RequestHeader requestHeader : adapterDTO.getRequestHeaders()) {
                        conn.setRequestProperty(requestHeader.getKey(), requestHeader.getValue());
                    }
                }

                if (adapterDTO.getRequestType().equalsIgnoreCase("post") &&
                        !StringUtils.isNullOrEmpty(adapterDTO.getRequestBody())) {
                    conn.setDoOutput(true);
                    try (OutputStream os = conn.getOutputStream()) {
                        byte[] input = adapterDTO.getRequestBody().getBytes("utf-8");
                        os.write(input, 0, input.length);
                        os.flush();
                    }
                }

                contentType = conn.getContentType();

                if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 203) {
                    int fileLength = conn.getContentLength();
                    log.info("{\"service\":\"{}\", \"process\":\"{}\",\"jobtype\":\"{}\",\"jobdate\":\"{}\", "
                                    + "\"jobtime\":\"{}\", \"archivesize\":{}}",
                            "cdp", "GetApiDataFileSize", adapterDTO.getJobType(), LocalDate.now(), LocalTime.now(),
                            (double) fileLength / Constants.BLOCK_SIZE / Constants.BLOCK_SIZE);
                    ir = new InputStreamReader(conn.getInputStream());
                    br = new BufferedReader(ir);
                    String line;
                    while ((line = br.readLine()) != null) {
                        content = content.equals("") ? content.concat(line) : content.concat("\n" + line);
                    }

                    PageRequest.NextPage next = nextPage.checkNextPage(conn);

                    if (next.isHasNextPage()) {
                        log.info("Reading Page..");
                        uriBuilder = next.getNextUrl();
                    } else {
                        log.info("Completed reading all pages");
                        flag = false;
                    }

                } else {
                    if (conn != null) {
                        log.info("Error calling API - Error code :" + conn.getResponseCode() + " Error :" + conn.getResponseMessage());
                        content = null;
                        contentType = null;
                        error = "Error code :" + conn.getResponseCode() + " Error :" + conn.getResponseMessage();
                    }
                    flag = false;
                }
                if (ir != null)
                    ir.close();
                if (br != null)
                    br.close();
            } while (flag);
        } catch (IOException | URISyntaxException e) {
            log.error("IOException occurred in invokeGetApiData : verify your connection: {}", e);
            content = null;
            contentType = null;
            error = e.getMessage();
        } finally {
            try {
                if (ir != null)
                    ir.close();
                if (br != null)
                    br.close();
                if (conn != null)
                    conn.disconnect();
            } catch (Exception e) {
                log.error("Exception in closing the connection in invokeGetApiData", e);
            }
        }
        return new String[]{content, contentType, error};
    }

    protected HttpURLConnection createConnection(URIBuilder uriBuilder) throws URISyntaxException, IOException {
        return (HttpURLConnection) uriBuilder.build().toURL().openConnection();
    }

}
