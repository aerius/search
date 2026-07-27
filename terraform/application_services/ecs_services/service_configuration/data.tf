#############################
# COLLECT REMOTE STATE DATA #
#############################

## Bing API Key from Secrets Manager

data "aws_secretsmanager_secret_version" "bing_api_key_search" {
  secret_id = "/aerius/shared/search/bing-api-key"
}
