#############################
# COLLECT REMOTE STATE DATA #
#############################

## Bing API KEY
data "aws_ssm_parameter" "bing_apikey_search" {
  name = "/shared/keys/bing"
}
