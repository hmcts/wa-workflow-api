provider "azurerm" {
  features {}
}

data "azurerm_key_vault" "s2s_key_vault" {
  name                = "s2s-${var.env}"
  resource_group_name = "rpe-service-auth-provider-${var.env}"
}

data "azurerm_key_vault_secret" "s2s_secret" {
  key_vault_id = data.azurerm_key_vault.s2s_key_vault.id
  name         = "microservicekey-wa-workflow-api"
}

app_settings = {
  S2S_SECRET_WORKFLOW_API = "${data.azurerm_key_vault_secret.s2s_secret.value}"
}
