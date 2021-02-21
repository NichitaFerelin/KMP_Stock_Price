package com.ferelin.repository

import com.ferelin.repository.tools.local.LocalManagerToolsHelper
import com.ferelin.repository.tools.remote.RemoteManagerToolsHelper

interface RepositoryManagerHelper : RemoteManagerToolsHelper, LocalManagerToolsHelper