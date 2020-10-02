provider "azurerm" {
  features {}
}


data "azurerm_key_vault" "wa_key_vault" {
  name                = "${var.product}-${var.env}"
  resource_group_name = "${var.product}-${var.env}"
}

data "azurerm_key_vault" "s2s_key_vault" {
  name                = "s2s-${var.env}"
  resource_group_name = "rpe-service-auth-provider-${var.env}"
}

data "azurerm_key_vault_secret" "s2s_secret" {
  name         = "microservicekey-wa-workflow-api"
  key_vault_id = data.azurerm_key_vault.s2s_key_vault.id
  vault_uri    = data.azurerm_key_vault.s2s_key_vault.vault_uri
}
