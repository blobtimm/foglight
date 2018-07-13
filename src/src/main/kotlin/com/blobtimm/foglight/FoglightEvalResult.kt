package com.blobtimm.foglight

enum class FoglightEvalResult {
    DO_NOTHING,

    // proguard detected, log please!
    INFRACTION_LOG,

    // proguard detected, make it known!
    INFRACTION_CRASH
}