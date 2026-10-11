package com.example

import com.example.data.ai.DemoMarketingEngine
import com.example.data.model.BusinessCategories
import com.example.data.model.BusinessProfile
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun `business categories contains all 15 niches`() {
        assertEquals(15, BusinessCategories.ALL.size)
        assertTrue(BusinessCategories.ALL.contains("Instagram Clothing Store"))
        assertTrue(BusinessCategories.ALL.contains("Online Boutique"))
        assertTrue(BusinessCategories.ALL.contains("Jewellery Business"))
        assertTrue(BusinessCategories.ALL.contains("Saree & Ethnic Wear"))
        assertTrue(BusinessCategories.ALL.contains("WhatsApp Business Seller"))
    }

    @Test
    fun `marketing engine generates captions and hashtags`() {
        val profile = BusinessProfile(name = "Zari Studio", category = "Online Boutique")
        val caption = DemoMarketingEngine.generateContent(
            toolName = "Instagram Caption",
            data = mapOf("product_name" to "Chanderi Saree", "features" to "Pure handloom"),
            profile = profile
        )
        assertTrue(caption.contains("Chanderi Saree"))
        assertTrue(caption.contains("ZariStudio"))

        val hashtags = DemoMarketingEngine.generateContent(
            toolName = "Hashtag Suggestions",
            data = mapOf("product_name" to "Handcrafted Necklace"),
            profile = profile
        )
        assertTrue(hashtags.contains("#ShopLocal"))
    }
}
