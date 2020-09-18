provider "azurerm" {
  features {}
}

data "azurerm_key_vault_secret" "s2s_key" {
  name      = "microservicekey-wa-workflow-api"
  vault_uri = "https://s2s-${var.env}.vault.azure.net/"
}
