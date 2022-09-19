package com.sysco.cdp;

import com.amazonaws.util.StringUtils;
import com.sysco.cdp.beans.BulkJobDTO;
import com.sysco.cdp.common.Constants;
import com.sysco.cdp.service.BulkApiAdapterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.UUID;

import static com.sysco.cdp.common.Utility.jsonToObject;

@Component
@Slf4j
@RequiredArgsConstructor
public class CdpBulkApiAdapter implements CommandLineRunner {

    @Value("${cdp.serviceName}")
    private String serviceName;

    @Value("${cdp.process}")
    private String processName;

    private final BulkApiAdapterService apiAdapterService;


    @Override
    public void run(String... args) throws Exception {

        Arrays.stream(args).peek(s -> log.info("Input to application -> {}", s));

        if(args.length == 4)
            throw new Exception("Input argument count does not match");

        log.info("Input to application-> {}", args[3]);

        BulkJobDTO dto = jsonToObject(args[3], BulkJobDTO.class);
        dto = formBulkJobDTO(dto);

        log.info(
                "{\"service\":\"{}\", \"process\":\"{}\", \"jobtype\":\"{}\", \"jobstatus\":\"{}\","
                        + " \"jobId\":\"{}\", \"joberror\":\"{}\", "
                        + "\"jobdate\":\"{}\", \"jobtime\":\"{}\", \"jobprocescounter\":{} }",
                serviceName, processName, dto.getJobType(), dto.getJobStatus(), dto.getJobId(),
                dto.getJobError(), LocalDate.now(), LocalTime.now(), 0);

        dto.setJobStatus(Constants.JobStatus.JOB_INITIATED.getStatus());

        boolean isValid = validate(dto);
        if (isValid) {
            apiAdapterService.loadBulkData(dto);
        } else {
            dto.setJobStatus(Constants.JobStatus.JOB_ERROR.getStatus());
            dto.setJobError("Job validation error");
        }

    }

    private BulkJobDTO formBulkJobDTO(BulkJobDTO dto) {
        if (StringUtils.isNullOrEmpty(dto.getJobId())) {
            // Generate Unique Id to identify the trigger process
            String jobId = UUID.randomUUID().toString();
            dto.setJobId(jobId);
        }
        return dto;
    }

    private boolean validate(@Valid BulkJobDTO dto) {
        /**
         * @TODO: 7/15/2022 implement validation logic here
         */
        return true;
    }
}
