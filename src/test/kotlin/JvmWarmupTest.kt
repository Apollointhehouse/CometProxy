import dev.apollointhehouse.utils.JvmWarmup
import kotlin.test.Test

class JvmWarmupTest {
    @Test
    fun `test jvm warmup executes successfully`() {
        JvmWarmup.warmup()
    }
}
