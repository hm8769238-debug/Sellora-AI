package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.DemoSalesEngine
import com.example.data.model.BusinessProfile
import com.example.data.model.ProductEntity
import com.example.data.repository.SelloraRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context matches Sellora AI`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Sellora AI", appName)
    }

    @Test
    fun `sales engine generates accurate grounded english reply`() {
        val profile = BusinessProfile(
            name = "Royal Silks",
            shippingPolicy = "Free delivery above ₹999.",
            returnRefundPolicy = "7-day exchange.",
            contactWhatsapp = "+91 99999 88888"
        )
        val product = ProductEntity(
            name = "Banarasi Saree",
            category = "Ethnic",
            price = 2499.0,
            currency = "₹ INR",
            availableSizes = "Free Size",
            colors = "Maroon, Emerald Green",
            stockStatus = "In Stock",
            deliveryDetails = "2-3 days metro delivery"
        )

        val result = DemoSalesEngine.generateReply(
            customerMessage = "Is this saree available in maroon, and what is the price?",
            product = product,
            profile = profile,
            language = "English",
            tone = "Friendly",
            length = "Standard"
        )

        assertTrue(result.readyToSendReply.contains("Banarasi Saree"))
        assertTrue(result.readyToSendReply.contains("2499"))
        assertTrue(result.readyToSendReply.contains("Maroon"))
        assertNotNull(result.shortReply)
        assertNotNull(result.friendlyReply)
        assertNotNull(result.professionalReply)
        assertNotNull(result.premiumReply)
        assertNotNull(result.suggestedFollowUp)
    }

    @Test
    fun `sales engine generates multilingual hindi and bengali replies`() {
        val profile = BusinessProfile(name = "Kolkata Weaves")
        val product = ProductEntity(
            name = "Dhakai Jamdani",
            category = "Saree",
            price = 3500.0,
            currency = "₹ INR",
            colors = "White & Red",
            stockStatus = "In Stock"
        )

        val hindiResult = DemoSalesEngine.generateReply(
            customerMessage = "क्या यह उपलब्ध है?",
            product = product,
            profile = profile,
            language = "Hindi (हिंदी)",
            tone = "Friendly",
            length = "Standard"
        )
        assertTrue(hindiResult.readyToSendReply.contains("Dhakai Jamdani"))
        assertTrue(hindiResult.readyToSendReply.contains("3500"))

        val bengaliResult = DemoSalesEngine.generateReply(
            customerMessage = "এটার দাম কত এবং এভেইলেবল আছে?",
            product = product,
            profile = profile,
            language = "Bengali (বাংলা)",
            tone = "Friendly",
            length = "Standard"
        )
        assertTrue(bengaliResult.readyToSendReply.contains("Dhakai Jamdani"))
        assertTrue(bengaliResult.readyToSendReply.contains("3500"))
    }
}
