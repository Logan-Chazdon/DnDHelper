package services

import io.ktor.client.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Test

class FeatServiceIntegrationTest {

    private data class User(
        val client: HttpClient
    ) : ServiceProvider(client)


    private val users = listOf(
        User(
            client = client1,
        ),
        User(
            client = client2,
        )
    )

    @Test
    fun testFeats() = runTest {
        users.forEach { user ->
            val feats = user.featService.getUnfilledFeats()

            feats.first().forEach { feat ->
                 user.featService.getFeatFeatures(feat.id)
            }
        }
    }
}