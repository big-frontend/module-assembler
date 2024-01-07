package com.jamesfchen.export

import com.jamesfchen.ibc.cbpc.IExport

abstract class ICall : IExport() {
    abstract fun call():Boolean
}