package com.sushobh.fraglens



// Level 10 - deepest object
data class Level10(val id: Int, val description: String)

// Level 9
data class Level9(val name: String, val level10: Level10, val tags: List<String>)

// Level 8
data class Level8(val flag: Boolean, val nested: Level9)

// Level 7
data class Level7(val numbers: IntArray, val nested: Level8)

// Level 6
data class Level6(val map: Map<String, String>, val nested: Level7)

// Level 5
data class Level5(val list: List<Double>, val nested: Level6)

// Level 4
data class Level4(val text: String, val nested: Level5)

// Level 3
data class Level3(val value: Int, val nested: Level4)

// Level 2
data class Level2(val active: Boolean, val nested: Level3)

// Level 1 - top-level object
data class Level1(val name: String, val nested: Level2)

object NestedObj {
    // Build the nested object
    val obj = Level1(
        name = "TopObject",
        nested = Level2(
            active = true,
            nested = Level3(
                value = 42,
                nested = Level4(
                    text = "Level 4 text",
                    nested = Level5(
                        list = listOf(1.1, 2.2, 3.3),
                        nested = Level6(
                            map = mapOf("key1" to "value1", "key2" to "value2"),
                            nested = Level7(
                                numbers = intArrayOf(10, 20, 30),
                                nested = Level8(
                                    flag = false,
                                    nested = Level9(
                                        name = "Level 9 Object",
                                        level10 = Level10(
                                            id = 999,
                                            description = "Deepest level"
                                        ),
                                        tags = listOf("a", "b", "c")
                                    )
                                )
                            )
                        )
                    )
                )
            )
        )
    )
}