package pozzo.apps.travelweather.direction

import org.junit.Test

class RouteBusinessTest {

	@Test fun understandingKotlinStream() {
        listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
                .asSequence()
                .filter {
                    println("Filtering $it")
                    it % 2 == 0
                }.forEach(::println)
	}
}
