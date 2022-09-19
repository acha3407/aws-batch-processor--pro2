package com.sysco.cdp.service;

import com.sysco.cdp.beans.BulkJobDTO;
import com.sysco.cdp.paginationHandler.PageRequest;

public interface ApiService {

    String[] invokeApi(BulkJobDTO adapterDTO, PageRequest nextPage);

}
