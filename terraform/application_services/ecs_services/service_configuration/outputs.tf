output "services" {
  description = "The services object containing all configuration"
  value = yamldecode(templatefile("${path.module}/services.yaml.tftpl", {
      APP_TIMEZONE  = var.app_timezone,
      APP_VERSION   = var.app_version,
      REGISTRY_URL  = var.ecr_directory == null ? "${var.ecr_repo}/${lower(var.environment)}" : "${var.ecr_repo}/${var.ecr_directory}",
      
      BING_APIKEY = nonsensitive(data.aws_ssm_parameter.bing_apikey_search.value)

  })).services
}
