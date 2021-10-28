/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.core.resolvers

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile
import javax.inject.Inject

/**
 * Provides necessary api for interacting with local files
 * */
class LocalFilesResolver @Inject constructor(
    private val context: Context
) {
    /**
     * Creates [DocumentFile]
     * @param treePath the Intent.getData() from a successful Intent.ACTION_OPEN_DOCUMENT_TREE request
     * @param pathAuthority is an authority of file
     * @return [DocumentFile] if created
     * */
    fun buildDocumentFile(treePath: String, pathAuthority: String): DocumentFile? {
        val uriByPath = Uri.Builder()
            .path(treePath)
            .authority(pathAuthority)
            .build()

        return DocumentFile.fromTreeUri(context, uriByPath)
    }

    /**
     * Builds absolute file path from [DocumentFile]
     * @return file path as [String]
     * */
    fun buildFilePath(file: DocumentFile): String {
        var finalPath = "${file.name}"
        var parentFile = file.parentFile

        while (parentFile != null) {
            finalPath = "${parentFile.name}/$finalPath"
            parentFile = parentFile.parentFile
        }

        return "sdcard/$finalPath"
    }
}