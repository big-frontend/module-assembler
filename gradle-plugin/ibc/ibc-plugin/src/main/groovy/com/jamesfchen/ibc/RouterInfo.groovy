package com.jamesfchen.ibc
class RouterInfo{
    String name
    String descriptor

    RouterInfo(String name, String descriptor) {
        this.name = name
        this.descriptor = descriptor
    }

    @Override
    String toString() {
        return "RouterInfo{" +
                "name='" + name + '\'' +
                ", descriptor='" + descriptor + '\'' +
                '}';
    }
}