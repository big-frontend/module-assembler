package com.jamesfchen.moduleruntime.qigsaw

import android.content.Context

class QigsawNonPlayBackendProvider : NonPlayBackendProvider {
    override fun create(context: Context): NonPlayInstallBackend {
        return QigsawNonPlayInstallBackend(
            bridge = QigsawInstallBridgeImpl(context.applicationContext),
        )
    }
}
