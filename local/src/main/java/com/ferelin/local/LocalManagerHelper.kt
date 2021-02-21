package com.ferelin.local

import com.ferelin.local.database.CompaniesManagerHelper
import com.ferelin.local.json.JsonManagerHelper
import com.ferelin.local.preferences.PreferencesManagerHelper

interface LocalManagerHelper : CompaniesManagerHelper, PreferencesManagerHelper, JsonManagerHelper