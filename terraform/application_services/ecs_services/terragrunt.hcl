############################
# Terragrunt configuration
############################

# Include all settings from the root terraform.tfvars file
include {
  path = find_in_parent_folders()
}

dependencies {
  paths = ["../service_configuration"]
}

dependency "service_configuration" {
  config_path  = "../service_configuration"

  mock_outputs = {
    "services" = {
      "nginx" = {
        "image" = "myMockImage:latest"
        "cpu" = 1024
        "memory" = 2048
        "min_capacity" = 1
        "max_capacity" = 1
        "desired_count" = 1
        "portmappings" = [
          {"containerPort" = 1337},
          {"hostPort" = 1337}
        ]
      }
    }
  }

  mock_outputs_allowed_terraform_commands = ["init", "validate", "plan"]
}

terraform {
  source = ".//services_generator"
  extra_arguments "common_vars" {
    commands = get_terraform_commands_that_need_vars()
  }
}

inputs = {
  services = dependency.service_configuration.outputs.services
}