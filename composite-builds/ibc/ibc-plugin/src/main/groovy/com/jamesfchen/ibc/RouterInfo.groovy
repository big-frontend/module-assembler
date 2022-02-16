package com.jamesfchen.ibc
class RouterInfo{
    String bindingBundleName
    String descriptor

    RouterInfo(String bindingBundleName, String descriptor) {
        this.bindingBundleName = bindingBundleName
        this.descriptor = descriptor
    }

    @Override
    String toString() {
        return "RouterInfo{" +
                "bindingBundleName='" + bindingBundleName + '\'' +
                ", descriptor='" + descriptor + '\'' +
                '}';
    }
}