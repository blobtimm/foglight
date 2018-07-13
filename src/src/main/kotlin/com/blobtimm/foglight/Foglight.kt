package com.blobtimm.foglight

import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties

internal class Foglight{

    private var isStarted = false
    private val exceptions = mutableMapOf<String, FoglightAlertLevel>()
    private var default = FoglightAlertLevel.CRASH_ON_ERROR

    fun addException(k: KClass<Any>, e: FoglightAlertLevel) = addException(k, e, exceptions, default)

    fun setDefaultException(e: FoglightAlertLevel) {
        if (isStarted) {
            throw IllegalStateException("Cannot change default permission after startup")
        }
        default = e
    }

    fun create() { isStarted = true }

    fun evaluate(data: Any): FoglightEvalResult {
        if (!isStarted) {
            throw IllegalStateException("Please invoke #create")
        }
        return evaluate(data, exceptions, default)
    }


    companion object {

        fun evaluate(data: Any, e: Map<String, FoglightAlertLevel>, default: FoglightAlertLevel): FoglightEvalResult {
            val isProguarded = containsProguardSignals(data)

            if (isProguarded) {

                val k = kClass(data)
                val name = getClassName(k)
                val state = e.getOrDefault(name, default)

                return when (state) {
                    FoglightAlertLevel.CRASH_ON_ERROR -> FoglightEvalResult.INFRACTION_CRASH
                    FoglightAlertLevel.LOG_ON_ERROR -> FoglightEvalResult.INFRACTION_LOG
                }
            }
            return FoglightEvalResult.DO_NOTHING
        }

        fun addException(k: KClass<Any>, e: FoglightAlertLevel, settings: MutableMap<String, FoglightAlertLevel>, default: FoglightAlertLevel) {
            if (default == e) {
                throw IllegalArgumentException("Specifying ${e.name} is illegal, it is already the default behavior.")
            }
            settings.put(getClassName(k), e)
        }

        fun getClassName(k: KClass<Any>): String {
            val name = k.qualifiedName
            if (name == null) {
                throw NullPointerException("The class that was specified is null and cannot be uniquely identified.")
            }
            return name
        }

        fun containsProguardSignals(data: Any): Boolean {

            val signals = proguardNameVariants()
            val clazz = kClass(data)

            var totalMembers = 0
            var totalProguardSignals = 0

            clazz.declaredMemberProperties.forEach {
                totalMembers++
                if (signals.contains(it.name)) {
                    totalProguardSignals++
                }
            }

            if (totalMembers == 0) {
                return false
            } else {
                return totalMembers == totalProguardSignals
            }
        }

        fun kClass(data: Any): KClass<Any> {
            return data.javaClass.kotlin
        }

        fun proguardNameVariants(): List<String> {
            val alphabet = listOf("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p",
                    "q", "r", "s", "t", "u", "v", "w", "x", "y", "z")

            return alphabet
        }
    }

}