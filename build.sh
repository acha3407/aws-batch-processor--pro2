export AMPERITY_PUBLIC_KEY_BUCKET=cdp-ci-dev-1;
export AMPERITY_PUBLIC_KEY_PATH=encryption/Amperity-PublicKey.pem;
export SYSCO_PUBLIC_KEY_BUCKET=cdp-ci-dev-1;
export SYSCO_PUBLIC_KEY_PATH=encryption/Sysco-PublicKey.pem;
export AMPERITY_DESTINATION_BUCKET=cdp-amperity-data-dev-2;
export SYSCO_DESTINATION_BUCKET=cdp-sysco-data-bucket-dev-1;
export AUDIT_TABLE=batchJobAudit;

./gradlew clean bootJar

aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 243012948420.dkr.ecr.us-east-1.amazonaws.com

export version=0.01

echo "start docker build"
docker build -t load-processor-demo:v${version} .
echo "complete docker build"

docker tag load-processor-demo:v${version} 243012948420.dkr.ecr.us-east-1.amazonaws.com/load-processor-demo:v${version}



echo "start docker push"
docker push 243012948420.dkr.ecr.us-east-1.amazonaws.com/load-processor-demo:v${version}
echo "complete docker push"

sleep 8
#./gradlew bootJar