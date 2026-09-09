import dev.apollointhehouse.data.nbt.NbtIO
import dev.apollointhehouse.data.nbt.UnknownTagException
import dev.apollointhehouse.data.nbt.tags.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.test.*

class NbtTest {

    @Test
    fun `test primitive tags read write`() {
        val originalCompound = CompoundTag("root").apply {
            putByte("byte", 42.toByte())
            putShort("short", 1234.toShort())
            putInt("int", 123456)
            putLong("long", 1234567890123L)
            putFloat("float", 3.14f)
            putDouble("double", 2.718281828)
            putString("string", "Hello NBT world!")
            putBoolean("boolTrue", true)
            putBoolean("boolFalse", false)
        }

        val baos = ByteArrayOutputStream()
        val dos = DataOutputStream(baos)
        Tag.write(originalCompound, dos)
        dos.flush()

        val bais = ByteArrayInputStream(baos.toByteArray())
        val dis = DataInputStream(bais)
        val readCompound = Tag.read(dis) as CompoundTag

        assertEquals(originalCompound, readCompound)
        assertEquals(42.toByte(), readCompound.getByte("byte"))
        assertEquals(1234.toShort(), readCompound.getShort("short"))
        assertEquals(123456, readCompound.getInt("int"))
        assertEquals(1234567890123L, readCompound.getLong("long"))
        assertEquals(3.14f, readCompound.getFloat("float"))
        assertEquals(2.718281828, readCompound.getDouble("double"))
        assertEquals("Hello NBT world!", readCompound.getString("string"))
        assertEquals(true, readCompound.getBoolean("boolTrue"))
        assertEquals(false, readCompound.getBoolean("boolFalse"))
    }

    @Test
    fun `test array tags read write`() {
        val originalCompound = CompoundTag("arrays").apply {
            putByteArray("bytes", byteArrayOf(1, 2, 3, -1, -128, 127))
            putShortArray("shorts", shortArrayOf(10, 20, 30, -32768, 32767))
            putLongArray("longs", longArrayOf(100L, 200L, Long.MIN_VALUE, Long.MAX_VALUE))
            putDoubleArray("doubles", doubleArrayOf(1.1, 2.2, 3.3, Double.NaN, Double.POSITIVE_INFINITY))
        }

        val baos = ByteArrayOutputStream()
        val dos = DataOutputStream(baos)
        Tag.write(originalCompound, dos)
        dos.flush()

        val bais = ByteArrayInputStream(baos.toByteArray())
        val dis = DataInputStream(bais)
        val readCompound = Tag.read(dis) as CompoundTag

        assertEquals(originalCompound, readCompound)
        assertContentEquals(byteArrayOf(1, 2, 3, -1, -128, 127), readCompound.getByteArray("bytes"))
        assertContentEquals(shortArrayOf(10, 20, 30, -32768, 32767), readCompound.getShortArray("shorts"))
        assertContentEquals(longArrayOf(100L, 200L, Long.MIN_VALUE, Long.MAX_VALUE), readCompound.getLongArray("longs"))
        assertContentEquals(doubleArrayOf(1.1, 2.2, 3.3, Double.NaN, Double.POSITIVE_INFINITY), readCompound.getDoubleArray("doubles"))
    }

    @Test
    fun `test list tag with primitive and compound elements`() {
        val stringList = ListTag("strings").apply {
            add(StringTag(null, "first"))
            add(StringTag(null, "second"))
            add(StringTag(null, "third"))
        }

        val compoundList = ListTag("compounds").apply {
            add(CompoundTag(null).apply { putString("name", "item1"); putInt("count", 5) })
            add(CompoundTag(null).apply { putString("name", "item2"); putInt("count", 10) })
        }

        val emptyList = ListTag("empty")

        val root = CompoundTag("root").apply {
            putList("strings", stringList)
            putList("compounds", compoundList)
            putList("empty", emptyList)
        }

        val baos = ByteArrayOutputStream()
        val dos = DataOutputStream(baos)
        Tag.write(root, dos)
        dos.flush()

        val bais = ByteArrayInputStream(baos.toByteArray())
        val dis = DataInputStream(bais)
        val readRoot = Tag.read(dis) as CompoundTag

        assertEquals(root, readRoot)
        val readStringList = readRoot.getList("strings")
        assertNotNull(readStringList)
        assertEquals(3, readStringList.size)
        assertEquals("first", (readStringList[0] as StringTag).value)
        assertEquals("second", (readStringList[1] as StringTag).value)
        assertEquals("third", (readStringList[2] as StringTag).value)

        val readCompoundList = readRoot.getList("compounds")
        assertNotNull(readCompoundList)
        assertEquals(2, readCompoundList.size)
        assertEquals("item1", readCompoundList.getCompound(0)?.getString("name"))
        assertEquals(5, readCompoundList.getCompound(0)?.getInt("count"))
    }

    @Test
    fun `test nested compound tags and compression`() {
        val nested = CompoundTag("child").apply {
            putString("level", "inner")
            putInt("depth", 1)
        }

        val root = CompoundTag("root").apply {
            putCompound("child", nested)
            putString("title", "Root Level")
        }

        val baos = ByteArrayOutputStream()
        NbtIO.writeCompressed(root, baos)

        val bais = ByteArrayInputStream(baos.toByteArray())
        val decompressed = NbtIO.readCompressed(bais)

        assertEquals(root, decompressed)
        assertEquals("Root Level", decompressed.getString("title"))
        val readChild = decompressed.getCompound("child")
        assertNotNull(readChild)
        assertEquals("inner", readChild.getString("level"))
        assertEquals(1, readChild.getInt("depth"))
    }

    @Test
    fun `test compound tag operator overloads and accessors`() {
        val tag = CompoundTag("test")
        tag["str"] = "value"
        tag["int"] = 42
        tag["bool"] = true
        tag["bytes"] = byteArrayOf(10, 20)

        assertTrue("str" in tag)
        assertTrue(tag.contains("int", TagType.Int))
        assertFalse(tag.contains("int", TagType.String))
        assertEquals("value", tag.getString("str"))
        assertEquals(42, tag.getInt("int"))
        assertTrue(tag.getBoolean("bool"))
        assertContentEquals(byteArrayOf(10, 20), tag.getByteArray("bytes"))

        tag.remove("str")
        assertFalse("str" in tag)
        assertEquals("", tag.getString("str"))
        assertEquals("fallback", tag.getString("str", "fallback"))
    }

    @Test
    fun `test deep copying tags`() {
        val original = CompoundTag("orig").apply {
            putString("key", "val")
            putCompound("sub", CompoundTag("sub").apply {
                putInt("num", 100)
            })
            putByteArray("arr", byteArrayOf(1, 2, 3))
        }

        val copy = original.copy()
        assertEquals(original, copy)

        original.putString("key", "modified")
        original.getCompound("sub")?.putInt("num", 999)
        original.getByteArray("arr")[0] = 99

        assertEquals("val", copy.getString("key"))
        assertEquals(100, copy.getCompound("sub")?.getInt("num"))
        assertEquals(1.toByte(), copy.getByteArray("arr")[0])
    }

    @Test
    fun `test unknown tag exception`() {
        val badData = byteArrayOf(99, 0, 4, 't'.code.toByte(), 'e'.code.toByte(), 's'.code.toByte(), 't'.code.toByte())
        val dis = DataInputStream(ByteArrayInputStream(badData))
        assertFailsWith<UnknownTagException> {
            Tag.read(dis)
        }
    }

    @Test
    fun `test NbtIO byte array serialization helpers`() {
        val tag = CompoundTag("test").apply {
            putString("name", "Alex")
            putLong("time", 123456789L)
        }

        val bytes = NbtIO.write(tag)
        val readTag = NbtIO.read(bytes)

        assertEquals(tag, readTag)
    }
}
