variable "product" {
  default = "wa"
}

variable "component" {
  default = "workflow-api"
}

variable "location" {
  default = "UK South"
}

variable "env" {}

variable "subscription" {}

variable "deployment_namespace" {
  default = ""
}

variable "common_tags" {
  type = map(string)
}

variable "jenkins_AAD_objectId" {
  description = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}

variable "aks_subscription_id" {}

variable "action_group_name" {
  description = "The name of the Action Group to create."
  type        = string
  default     = "wa-support"
}

variable "email_address_key" {
  description = "Email address key in azure Key Vault."
  type        = string
  default     = "db-alert-monitoring-email-address"
}

variable "cpu_threshold" {
  default     = 5
  type        = number
  description = "Average CPU utilisation threshold"
}

variable "memory_threshold" {
  default     = 5
  type        = number
  description = "Average memory utilisation threshold"
}

variable "storage_threshold" {
  default     = 0.5
  type        = number
  description = "Average storage utilisation threshold"
}