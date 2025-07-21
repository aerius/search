locals {
  basicinfra_version          = "v1"
  ecr_repo                    = "028339422996.dkr.ecr.eu-west-1.amazonaws.com"

  app_timezone                = "Europe/Amsterdam"

  target_groups = {
    "tg1" = {name = "search",  protocol = "HTTP", port = "8090", path = "/", matcher = "200-399"}
  }

  listener_rules = {
    "rule1"    = {tg = "tg1", application_type = "SEARCH", path_pattern = "/*", cognito = true}
  }

  ssm_passwords = {}

  ecs_ctr_fes_1_instance_type         = "m5.large"
  ecs_ctr_fes_1_max_instance_size     = "2"

}
