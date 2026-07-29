#############################
# COLLECT REMOTE STATE DATA #
#############################

## Bing API Key from Secrets Manager

data "aws_secretsmanager_secret" "bing_api_key_search" {
  name = "/aerius/shared/search/bing-api-key"
}
