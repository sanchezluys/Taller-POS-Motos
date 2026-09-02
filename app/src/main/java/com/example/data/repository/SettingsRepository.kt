package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.models.ThousandsSeparator
import com.example.data.models.WorkshopCurrency
import com.example.data.models.WorkshopSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("taller_workshop_settings", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<WorkshopSettings> = _settings.asStateFlow()

    private fun loadSettings(): WorkshopSettings {
        val name = prefs.getString(KEY_NAME, "Taller Pro Multi-Vehículos") ?: "Taller Pro Multi-Vehículos"
        val tagline = prefs.getString(KEY_TAGLINE, "Motos • Bicis • Scooters • E-Bikes") ?: "Motos • Bicis • Scooters • E-Bikes"
        val address = prefs.getString(KEY_ADDRESS, "Av. Principal #123") ?: "Av. Principal #123"
        val phone = prefs.getString(KEY_PHONE, "+1 234 567 890") ?: "+1 234 567 890"
        val taxId = prefs.getString(KEY_TAX_ID, "") ?: ""
        val logoIcon = prefs.getString(KEY_LOGO_ICON, "WRENCH") ?: "WRENCH"
        val currencyStr = prefs.getString(KEY_CURRENCY, WorkshopCurrency.USD.name) ?: WorkshopCurrency.USD.name
        val customCurrencySymbol = prefs.getString(KEY_CUSTOM_CURRENCY_SYMBOL, "") ?: ""
        val separatorStr = prefs.getString(KEY_THOUSANDS_SEP, ThousandsSeparator.COMMA.name) ?: ThousandsSeparator.COMMA.name
        val showDecimals = prefs.getBoolean(KEY_SHOW_DECIMALS, true)
        val footerNote = prefs.getString(KEY_FOOTER_NOTE, "Garantía de 30 días en mano de obra. ¡Gracias por su preferencia!")
            ?: "Garantía de 30 días en mano de obra. ¡Gracias por su preferencia!"
        val isDarkMode = prefs.getBoolean(KEY_DARK_MODE, true)

        val currency = try {
            WorkshopCurrency.valueOf(currencyStr)
        } catch (e: Exception) {
            WorkshopCurrency.USD
        }

        val separator = try {
            ThousandsSeparator.valueOf(separatorStr)
        } catch (e: Exception) {
            ThousandsSeparator.COMMA
        }

        return WorkshopSettings(
            workshopName = name,
            tagline = tagline,
            address = address,
            phone = phone,
            taxId = taxId,
            logoIconName = logoIcon,
            currency = currency,
            customCurrencySymbol = customCurrencySymbol,
            thousandsSeparator = separator,
            showDecimals = showDecimals,
            receiptFooterNote = footerNote,
            isDarkMode = isDarkMode
        )
    }

    fun saveSettings(newSettings: WorkshopSettings) {
        prefs.edit().apply {
            putString(KEY_NAME, newSettings.workshopName)
            putString(KEY_TAGLINE, newSettings.tagline)
            putString(KEY_ADDRESS, newSettings.address)
            putString(KEY_PHONE, newSettings.phone)
            putString(KEY_TAX_ID, newSettings.taxId)
            putString(KEY_LOGO_ICON, newSettings.logoIconName)
            putString(KEY_CURRENCY, newSettings.currency.name)
            putString(KEY_CUSTOM_CURRENCY_SYMBOL, newSettings.customCurrencySymbol)
            putString(KEY_THOUSANDS_SEP, newSettings.thousandsSeparator.name)
            putBoolean(KEY_SHOW_DECIMALS, newSettings.showDecimals)
            putString(KEY_FOOTER_NOTE, newSettings.receiptFooterNote)
            putBoolean(KEY_DARK_MODE, newSettings.isDarkMode)
            apply()
        }
        _settings.value = newSettings
    }

    companion object {
        private const val KEY_NAME = "key_workshop_name"
        private const val KEY_TAGLINE = "key_tagline"
        private const val KEY_ADDRESS = "key_address"
        private const val KEY_PHONE = "key_phone"
        private const val KEY_TAX_ID = "key_tax_id"
        private const val KEY_LOGO_ICON = "key_logo_icon"
        private const val KEY_CURRENCY = "key_currency"
        private const val KEY_CUSTOM_CURRENCY_SYMBOL = "key_custom_currency_symbol"
        private const val KEY_THOUSANDS_SEP = "key_thousands_sep"
        private const val KEY_SHOW_DECIMALS = "key_show_decimals"
        private const val KEY_FOOTER_NOTE = "key_footer_note"
        private const val KEY_DARK_MODE = "key_dark_mode"
    }
}
