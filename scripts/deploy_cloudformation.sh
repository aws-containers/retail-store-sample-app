#!/bin/bash

# Staging script for copying deployment resources to an S3 bucket. The resources
# copied here are used as part of the deployment process for this project.
# Provide two parameters, the S3 bucket and the deployment command; DEPLOY, LINT, CLEAN or DELETE

# Parameters
S3PATH="cloudformation"
STACK_NAME="promo-pandemonium-store"

# Args
BUCKET=${1}
STACK_CONTROL=${2}

BUCKET_LOCATION="$(aws s3api get-bucket-location --bucket ${BUCKET}|grep ":"|cut -d\" -f4)"
if [ -z "$BUCKET_LOCATION" ]; then
    BUCKET_DOMAIN="s3.amazonaws.com"
    BUCKET_LOCATION="eu-west-2"
else
    BUCKET_DOMAIN="s3-${BUCKET_LOCATION}.amazonaws.com"
fi


case ${STACK_CONTROL} in

  # Deploy the main stack
  DEPLOY)
    cd ../
    cfn-lint deploy/cloudformation/**/*.yaml 
    aws s3 cp deploy/cloudformation/ s3://${BUCKET}/${S3PATH}/ --recursive 
    aws cloudformation deploy --template-file deploy/cloudformation/_template.yaml --stack-name ${STACK_NAME} --parameter-overrides file://deploy/cloudformation/parameters.json --capabilities CAPABILITY_NAMED_IAM --tags Purpose=Promo-Pandemonium-GameDay --disable-rollback
    ;;
  # Check your Cloudformation templates for errors
  LINT)
    cd ../
    cfn-lint deploy/cloudformation/**/*.yaml
    ;;
  # First delete the Clouformation stack, then redeploy
  CLEAN)
    cd ../
    aws cloudformation delete-stack --stack-name ${STACK_NAME}
    cfn-lint deploy/cloudformation/**/*.yaml
    aws s3 cp deploy/cloudformation/ s3://${BUCKET}/${S3PATH}/ --recursive 
    aws cloudformation deploy --template-file deploy/cloudformation/_template.yaml --stack-name ${STACK_NAME} --parameter-overrides file://deploy/cloudformation/parameters.json --capabilities CAPABILITY_NAMED_IAM --tags Purpose=Promo-Pandemonium-GameDay --disable-rollback
    ;;
  # Delete the Clouformation stack
  DELETE)
    aws cloudformation delete-stack --stack-name ${STACK_NAME}
    ;;
  *)
    echo "Unknown command"
    ;;
esac
