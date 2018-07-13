package com.blobtimm.foglight

class FoglightException : RuntimeException {

    val classname: String

    constructor(data: Any) : super() {
        classname = Foglight.getClassName(Foglight.kClass(data))
    }

    override fun getLocalizedMessage(): String {
        return "$classname ${super.getLocalizedMessage()}"
    }
}