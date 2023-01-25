############################
# Terragrunt configuration
############################

# Include all settings from the root terraform.tfvars file
include {
  path = find_in_parent_folders()
}


terraform {
  extra_arguments "common_vars" {
    commands = get_terraform_commands_that_need_vars()
  }
}
