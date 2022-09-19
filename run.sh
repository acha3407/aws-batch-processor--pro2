export v="{\"jobType\":\"okta\",\"apiUrl\":\"https://syscoconsumer.oktapreview.com/api/v1/users\",\"timeBase\":\"weekly/monthly\",\"requestType\":\"GET\",\"requestHeaders\":[{\"key\":\"Accept\",\"value\":\"application/json\"},{\"key\":\"Content-Type\",\"value\":\"application/json\"},{\"key\":\"Authorization\",\"value\":\"SSWS005rte9oPLTceZPOt7JooAQlzgBNCGSGzg4LB6aGAA\"}],\"requestBody\":\"body\"}"

export AMPERITY_PUBLIC_KEY_BUCKET=cdp-ci-dev-1;
export AMPERITY_PUBLIC_KEY_PATH=encryption/Amperity-PublicKey.pem;
export SYSCO_PUBLIC_KEY_BUCKET=cdp-ci-dev-1;
export SYSCO_PUBLIC_KEY_PATH=encryption/Sysco-PublicKey.pem;
export AMPERITY_DESTINATION_BUCKET=cdp-amperity-data-dev-2;
export SYSCO_DESTINATION_BUCKET=cdp-sysco-data-bucket-dev-1;
export AUDIT_TABLE=batchJobAudit;

#java -jar -DAMPERITY_PUBLIC_KEY_BUCKET=$AMPERITY_PUBLIC_KEY_BUCKET -DAMPERITY_PUBLIC_KEY_PATH=$AMPERITY_PUBLIC_KEY_PATH -DSYSCO_PUBLIC_KEY_BUCKET=$SYSCO_PUBLIC_KEY_BUCKET -DSYSCO_PUBLIC_KEY_PATH=$SYSCO_PUBLIC_KEY_PATH -DAMPERITY_DESTINATION_BUCKET=$AMPERITY_DESTINATION_BUCKET -DSYSCO_DESTINATION_BUCKET=$SYSCO_DESTINATION_BUCKET -DAUDIT_TABLE=$AUDIT_TABLE build/libs/cdp-bulk-api-adapter-0.0.1-SNAPSHOT.jar $v

java -jar build/libs/cdp-bulk-api-adapter-0.0.1-SNAPSHOT.jar $v


sleep 30