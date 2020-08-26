data "azurerm_key_vault_secret" "wa" {
  name      = "wa"
  vault_uri = "https://s2s-${var.env}.vault.azure.net/"
}

app_settings = {
S2S_KEY = "${data.azurerm_key_vault_secret.s2s_key.value}"
}
