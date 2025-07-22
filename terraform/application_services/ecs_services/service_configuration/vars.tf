####################
# MODULE VARIABLES #
####################

#--------------------
# Standard Variables
#--------------------

variable "account_id" {
  type        = string
  description = "The AWS account ID in which to provision the infrastructure"
}

variable "service" {
  type        = map(string)
  description = "A map of service details, e.g.: name, type, theme etc."
}

variable "environment" {
  type        = string
  description = "A string for the Environment used in the terragrunt repository directory structure. E.g development, test, apps_shared_service. etc"
}

#==========================
# ECS Service Variables
#==========================

variable "app_version" {
  type        = string
  description = "The version of the application being deployed"
}

variable "app_timezone" {
  type    = string
  description = "Timezone configuration for the application"
}

variable "ecr_repo" {
  type        = string
  description = "Name of the ECR repository hosting the images"
}

variable "ecr_directory" {
  type        = string
  description = "Name of the directory hosting the images in the ECR repository"
  default     = null
}

variable "application_host_headers" {
  type        = map
  description = "Map containing host headers for specific applications"
}
