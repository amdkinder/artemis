package uz.devcraft.artemis

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ArtemisApplicationTests {

    @Test
    fun contextLoads() {
        MyEnum.values().forEach{ print("$it -> ${it.ordinal}")}
    }

}
