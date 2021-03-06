variable "product" {}

variable "component" {}

variable "location" {
  default = "UK South"
}

variable "env" {}

variable "subscription" {}

variable "deployment_namespace" {}

variable "common_tags" {
  type = map(string)
}


variable "postgresql_database_name" {
  default = "wa_workflow_api"
}

variable "postgresql_user" {
  default = "wa_wa"
}
