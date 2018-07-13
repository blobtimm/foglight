import com.blobtimm.foglight.FoglightAlertLevel
import com.blobtimm.foglight.Foglight
import com.blobtimm.foglight.FoglightEvalResult
import org.junit.Assert.*
import org.junit.Test

class FoglightTest {

    data class NotProguardedDataExample(val c: Boolean, val e: Int, val name: String)

    data class ProguardedDataExample(val a: String, val b: Boolean, val c: Int)

    data class RegularClass(val someName: String, val isSetup: Boolean, val range: Double, val nullable: Int?)

    @Test
    fun proguardNameVariants_ensureSize() {
        assertEquals(26, Foglight.proguardNameVariants().size)
    }

    @Test
    fun kClass_UnitCheck() {
        assertEquals("Unit", Foglight.kClass(Unit).simpleName)
    }

    @Test
    fun getClassName_unit() {
        val k = Foglight.kClass(Unit)
        assertEquals("kotlin.Unit", Foglight.getClassName(k))
    }

    @Test
    fun getClassName_proguardedDataExample() {
        val proguarded = ProguardedDataExample("hello", false, 42)

        val k = Foglight.kClass(proguarded)

        assertEquals("FoglightTest.ProguardedDataExample",
                Foglight.getClassName(k))
    }

    @Test
    fun evaluate_infractionCrash_noExceptions() {
        val default = FoglightAlertLevel.CRASH_ON_ERROR
        val exceptions = mutableMapOf<String, FoglightAlertLevel>()
        val proguarded = ProguardedDataExample("hello", false, 42)

        assertEquals(FoglightEvalResult.INFRACTION_CRASH, Foglight.evaluate(proguarded, exceptions, default))
    }

    @Test
    fun evaluate_infractionLog_noExceptions() {
        val default = FoglightAlertLevel.LOG_ON_ERROR
        val exceptions = mutableMapOf<String, FoglightAlertLevel>()
        val proguarded = ProguardedDataExample("hello", false, 42)

        assertEquals(FoglightEvalResult.INFRACTION_LOG, Foglight.evaluate(proguarded, exceptions, default))
    }

    @Test
    fun evaluate_infractionCrash_hasExceptions() {
        val default = FoglightAlertLevel.LOG_ON_ERROR
        val exceptions = mutableMapOf<String, FoglightAlertLevel>()
        exceptions.put("FoglightTest.ProguardedDataExample", FoglightAlertLevel.CRASH_ON_ERROR)

        val proguarded = ProguardedDataExample("hello", false, 42)

        assertEquals(FoglightEvalResult.INFRACTION_CRASH, Foglight.evaluate(proguarded, exceptions, default))
    }

    @Test
    fun evaluate_infractionLog_hasExceptions() {
        val default = FoglightAlertLevel.CRASH_ON_ERROR
        val exceptions = mutableMapOf<String, FoglightAlertLevel>()
        exceptions.put("FoglightTest.ProguardedDataExample", FoglightAlertLevel.LOG_ON_ERROR)

        val proguarded = ProguardedDataExample("hello", false, 42)

        assertEquals(FoglightEvalResult.INFRACTION_LOG, Foglight.evaluate(proguarded, exceptions, default))
    }

    @Test
    fun evaluate_doNothing_hasExceptions() {
        val default = FoglightAlertLevel.CRASH_ON_ERROR
        val exceptions = mutableMapOf<String, FoglightAlertLevel>()
        exceptions.put("FoglightTest.ProguardedDataExample", FoglightAlertLevel.LOG_ON_ERROR)

        val normal = RegularClass("a", true, 1.1, null)

        assertEquals(FoglightEvalResult.DO_NOTHING, Foglight.evaluate(normal, exceptions, default))
    }

    @Test
    fun addException_ensureAdded() {
        val exceptions = mutableMapOf<String, FoglightAlertLevel>()
        val proguarded = ProguardedDataExample("hello", false, 42)
        val k = Foglight.kClass(proguarded)

        Foglight.addException(k, FoglightAlertLevel.LOG_ON_ERROR, exceptions, FoglightAlertLevel.CRASH_ON_ERROR)

        assertEquals(1, exceptions.size)
    }

    @Test
    fun addException_ensureCrash() {
        val exceptions = mutableMapOf<String, FoglightAlertLevel>()
        val proguarded = ProguardedDataExample("hello", false, 42)
        val k = Foglight.kClass(proguarded)

        try {
            Foglight.addException(k, FoglightAlertLevel.CRASH_ON_ERROR, exceptions, FoglightAlertLevel.CRASH_ON_ERROR)
            fail("should not hit this line")
        } catch (e: IllegalArgumentException) { }
    }



    @Test
    fun containsProguardSignals_proguardedClass() {
        val proguarded = ProguardedDataExample("hello", false, 42)
        assertTrue(Foglight.containsProguardSignals(proguarded))
    }

    @Test
    fun containsProguardSignals_notQuiteProguardedClass() {
        // close but not quite!
        val notProguarded = NotProguardedDataExample(false, 1, "hello")
        assertFalse(Foglight.containsProguardSignals(notProguarded))
    }


    @Test
    fun containsProguardSignals_notProguardedClass() {
        val normal = RegularClass("a", true, 1.1, null)
        assertFalse(Foglight.containsProguardSignals(normal))
    }
}