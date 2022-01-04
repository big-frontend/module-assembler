package com.jamesfchen.ibc

class ApiInfo {
    String descriptor

    ApiInfo(String descriptor) {
        this.descriptor = descriptor
    }

    @Override
    String toString() {
        return "ApiInfo{descriptor=" + descriptor + '}'
    }
}