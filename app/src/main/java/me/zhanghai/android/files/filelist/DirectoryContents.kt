/*
 * Copyright (c) 2026 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.filelist

import androidx.annotation.WorkerThread
import java8.nio.file.Path
import me.zhanghai.android.files.provider.common.isDirectory
import me.zhanghai.android.files.provider.common.isHidden
import me.zhanghai.android.files.provider.common.newDirectoryStream

data class DirectoryContents(
    val directoryCount: Int,
    val fileCount: Int
)

@WorkerThread
fun Path.loadDirectoryContents(showHiddenFiles: Boolean): DirectoryContents {
    var directoryCount = 0
    var fileCount = 0
    newDirectoryStream().use { directoryStream ->
        for (path in directoryStream) {
            if (!showHiddenFiles && path.isHidden) {
                continue
            }
            if (path.isDirectory()) {
                ++directoryCount
            } else {
                ++fileCount
            }
        }
    }
    return DirectoryContents(directoryCount, fileCount)
}
