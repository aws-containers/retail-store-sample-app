#!/bin/bash

# Staging script for copying deployment resources to an S3 bucket. The resources
# copied here are used as part of the deployment process for this project.
# Provide two parameters, the S3 bucket and the path you would like to use

# Parameters
BUCKET="harrzjas-retail-demo-store"
S3PATH="cloudformation"
STACK_NAME="promo-pandemonium-store"

# Args
STACK_CONTROL=${1}

BUCKET_LOCATION="$(aws s3api get-bucket-location --bucket ${BUCKET}|grep ":"|cut -d\" -f4)"
if [ -z "$BUCKET_LOCATION" ]; then
    BUCKET_DOMAIN="s3.amazonaws.com"
    BUCKET_LOCATION="eu-west-2"
else
    BUCKET_DOMAIN="s3-${BUCKET_LOCATION}.amazonaws.com"
fi

case ${STACK_CONTROL} in

  DEPLOY)
    cd ../
    cfn-lint deploy/cloudformation/**/*.yaml
    aws s3 cp deploy/cloudformation/ s3://${BUCKET}/${S3PATH}/ --recursive 
    aws cloudformation deploy --template-file deploy/cloudformation/_template.yaml --stack-name ${STACK_NAME} --parameter-overrides file://deploy/cloudformation/parameters.json --capabilities CAPABILITY_NAMED_IAM --tags Purpose=Promo-Pandemonium-GameDay --disable-rollback
    ;;
  LINT)
    cd ../
    cfn-lint deploy/cloudformation/**/*.yaml
    ;;
  CLEAN)
    cd ../
    aws cloudformation delete-stack --stack-name ${STACK_NAME}
    cfn-lint deploy/cloudformation/**/*.yaml
    aws s3 cp deploy/cloudformation/ s3://${BUCKET}/${S3PATH}/ --recursive 
    aws cloudformation deploy --template-file deploy/cloudformation/_template.yaml --stack-name ${STACK_NAME} --parameter-overrides file://deploy/cloudformation/parameters.json --capabilities CAPABILITY_NAMED_IAM --tags Purpose=Promo-Pandemonium-GameDay --disable-rollback
    ;;
  DELETE)
    aws cloudformation delete-stack --stack-name ${STACK_NAME}
    ;;
  *)
    echo "Unknown command"
    ;;
esac
