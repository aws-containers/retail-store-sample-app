import json
import logging

logger = logging.getLogger()
logger.setLevel(logging.INFO)

def lambda_handler(event, context):
    """
    Lambda function to process S3 object creation events.
    Logs the name of the uploaded file.
    """
    logger.info("Received event: " + json.dumps(event, indent=2))

    try:
        # Loop through records in the event
        for record in event.get('Records', []):
            s3_data = record.get('s3', {})
            bucket = s3_data.get('bucket', {}).get('name')
            key = s3_data.get('object', {}).get('key')
            
            # Log the required message
            print(f"Image received: {key}")
            logger.info(f"Successfully processed file: {key} from bucket: {bucket}")
            
        return {
            'statusCode': 200,
            'body': json.dumps('File processing log complete')
        }
        
    except Exception as e:
        logger.error(e)
        raise e
